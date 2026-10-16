package com.elevator;

import com.elevator.enums.Direction;
import com.elevator.hardware.ElevatorPanel;
import com.elevator.hardware.HallPanel;
import com.elevator.model.Elevator;
import com.elevator.model.ElevatorDispatcher;
import com.elevator.model.Floor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ElevatorSystem {
    private final ElevatorDispatcher dispatcher;
    private final List<Elevator> elevators;
    private final Map<Floor, HallPanel> hallPanels;
    private final Map<String, ElevatorPanel> cabinPanels;

    public ElevatorSystem(ElevatorDispatcher dispatcher,
                          List<Elevator> elevators,
                          Map<Floor, HallPanel> hallPanels,
                          Map<String, ElevatorPanel> cabinPanels) {
        this.dispatcher = dispatcher;
        this.elevators = elevators;
        this.hallPanels = hallPanels;
        this.cabinPanels = cabinPanels;
        if (dispatcher != null && hallPanels != null) {
            dispatcher.setHallPanels(hallPanels);
        }
    }

    /**
     * Passenger calls an elevator from a hallway panel using a Floor entity.
     */
    public void requestElevator(Floor floor, Direction direction) {
        HallPanel panel = hallPanels.get(floor);
        if (panel == null) {
            throw new IllegalArgumentException("Invalid floor: " + floor);
        }
        if (direction == Direction.UP) {
            panel.pressUpButton();
        } else if (direction == Direction.DOWN) {
            panel.pressDownButton();
        }
    }

    /**
     * Passenger inside an elevator selects their target destination floor using a Floor entity.
     */
    public void selectFloor(String elevatorId, Floor destinationFloor) {
        ElevatorPanel panel = cabinPanels.get(elevatorId);
        if (panel == null) {
            throw new IllegalArgumentException("Invalid elevatorId: " + elevatorId);
        }
        panel.pressFloorButton(destinationFloor);
    }

    /**
     * Advances the simulation/system clock by one step.
     */
    public void step() {
        for (Elevator elevator : elevators) {
            elevator.step();
        }
    }

    /**
     * Triggers emergency stop for a specific elevator car.
     */
    public void emergencyStop(String elevatorId) {
        ElevatorPanel panel = cabinPanels.get(elevatorId);
        if (panel != null) {
            panel.pressEmergencyStopButton();
        }
    }

    /**
     * Activates the alarm for a specific elevator car.
     */
    public void activateAlarm(String elevatorId) {
        ElevatorPanel panel = cabinPanels.get(elevatorId);
        if (panel != null) {
            panel.pressAlarmButton();
        }
    }

    public List<Elevator> getElevators() {
        return Collections.unmodifiableList(elevators);
    }

    public ElevatorDispatcher getDispatcher() {
        return dispatcher;
    }

    public Map<Floor, HallPanel> getHallPanels() {
        return Collections.unmodifiableMap(hallPanels);
    }

    public Map<String, ElevatorPanel> getCabinPanels() {
        return Collections.unmodifiableMap(cabinPanels);
    }
}
