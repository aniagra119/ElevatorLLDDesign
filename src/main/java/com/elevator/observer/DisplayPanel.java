package com.elevator.observer;

import com.elevator.enums.Direction;
import com.elevator.enums.ElevatorState;
import com.elevator.model.Elevator;
import com.elevator.model.Floor;

public class DisplayPanel implements ElevatorObserver {
    private final String name;
    private Floor displayedFloor;
    private Direction displayedDirection;
    private ElevatorState displayedState;

    public DisplayPanel(String name) {
        this.name = name;
        this.displayedFloor = Floor.of(0);
        this.displayedDirection = Direction.IDLE;
        this.displayedState = ElevatorState.OPERATING;
    }

    @Override
    public void update(Elevator elevator) {
        this.displayedFloor = elevator.getCurrentFloor();
        this.displayedDirection = elevator.getCurrentDirection();
        this.displayedState = elevator.getState();
        System.out.printf("[%s] Elevator %s -> Floor: %s | Direction: %s | State: %s%n",
                name, elevator.getId(), displayedFloor.getDisplayName(), displayedDirection, displayedState);
    }

    public String getName() {
        return name;
    }

    public Floor getDisplayedFloor() {
        return displayedFloor;
    }

    public int getDisplayedFloorNumber() {
        return displayedFloor.getFloorNumber();
    }

    public Direction getDisplayedDirection() {
        return displayedDirection;
    }

    public ElevatorState getDisplayedState() {
        return displayedState;
    }
}
