package com.elevator.model;

import com.elevator.command.receiver.AlarmReceiver;
import com.elevator.command.receiver.EmergencyStopReceiver;
import com.elevator.command.receiver.InternalRequestReceiver;
import com.elevator.enums.Direction;
import com.elevator.enums.ElevatorState;
import com.elevator.hardware.WeightSensor;
import com.elevator.observer.ElevatorObserver;
import com.elevator.strategy.movement.MovementStrategy;

import java.util.ArrayList;
import java.util.List;

public class Elevator implements InternalRequestReceiver, EmergencyStopReceiver, AlarmReceiver {
    private final String id;
    private Floor currentFloor;
    private Direction currentDirection;
    private ElevatorState state;

    private final WeightSensor weightSensor;
    private final Door door;
    private final List<ElevatorObserver> observers = new ArrayList<>();
    private MovementStrategy movementStrategy;

    public Elevator(String id, int capacity, MovementStrategy movementStrategy) {
        this(id, capacity, movementStrategy, Floor.of(0));
    }

    public Elevator(String id, int capacity, MovementStrategy movementStrategy, Floor initialFloor) {
        this.id = id;
        this.currentFloor = initialFloor != null ? initialFloor : Floor.of(0);
        this.currentDirection = Direction.IDLE;
        this.state = ElevatorState.OPERATING;
        this.weightSensor = new WeightSensor(capacity);
        this.door = new Door();
        this.movementStrategy = movementStrategy;
    }

    public boolean isOverloaded() {
        return weightSensor.isOverloaded();
    }

    public void addObserver(ElevatorObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(ElevatorObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers() {
        for (ElevatorObserver observer : observers) {
            observer.update(this);
        }
    }

    public void emergencyStop() {
        this.state = ElevatorState.EMERGENCY_STOP;
        this.door.open();
        notifyObservers();
    }

    public void activateAlarm() {
        System.out.println("Alarm activated in elevator " + id);
    }

    public void setMaintenanceMode() {
        this.state = ElevatorState.MAINTENANCE;
        notifyObservers();
    }

    public void addRequest(Request request) {
        movementStrategy.addRequest(request, this);
    }

    public void step() {
        if (this.state == ElevatorState.OPERATING) {
            movementStrategy.step(this);
        }
    }

    public void setCurrentFloor(Floor currentFloor) {
        if (currentFloor == null) {
            throw new IllegalArgumentException("currentFloor cannot be null");
        }
        this.currentFloor = currentFloor;
        notifyObservers(); // Display screens update
    }

    public void setCurrentDirection(Direction currentDirection) {
        this.currentDirection = currentDirection;
        notifyObservers(); // Display screens update: "Up" or "Down" or "Idle"
    }

    public void setCurrentLoad(int currentLoad) {
        this.weightSensor.updateWeight(currentLoad);
    }

    public void updateLoad(int weight) {
        this.weightSensor.updateWeight(weight);
    }

    public int getCapacity() {
        return this.weightSensor.getMaxCapacity();
    }

    public int getCurrentLoad() {
        return this.weightSensor.getCurrentWeight();
    }

    public WeightSensor getWeightSensor() {
        return this.weightSensor;
    }

    public void openDoor() {
        this.door.open();
        notifyObservers();
    }

    public boolean closeDoor() {
        if (isOverloaded()) {
            System.out.println("Cannot close door. Elevator is overloaded.");
            return false;
        }
        this.door.close();
        notifyObservers();
        return true;
    }

    public String getId() {
        return this.id;
    }

    public Floor getCurrentFloor() {
        return this.currentFloor;
    }

    public int getCurrentFloorNumber() {
        return this.currentFloor.getFloorNumber();
    }

    public Direction getCurrentDirection() {
        return this.currentDirection;
    }

    public Door getDoor() {
        return this.door;
    }

    public MovementStrategy getMovementStrategy() {
        return this.movementStrategy;
    }

    public ElevatorState getState() {
        return this.state;
    }
}