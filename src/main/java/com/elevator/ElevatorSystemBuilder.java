package com.elevator;

import com.elevator.hardware.ElevatorPanel;
import com.elevator.hardware.HallPanel;
import com.elevator.hardware.PanelFactory;
import com.elevator.model.Elevator;
import com.elevator.model.ElevatorDispatcher;
import com.elevator.model.Floor;
import com.elevator.strategy.dispatch.DirectionAwareDispatchStrategy;
import com.elevator.strategy.dispatch.DispatchStrategy;
import com.elevator.strategy.movement.MovementStrategy;
import com.elevator.strategy.movement.ScanQueueStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fluent builder for configuring and assembling an ElevatorSystem.
 * Provides sensible defaults for floor ranges, dispatching, and movement algorithms.
 */
public class ElevatorSystemBuilder {
    private final List<Elevator> elevators = new ArrayList<>();
    private DispatchStrategy dispatchStrategy;
    private Floor minFloor = Floor.of(0);
    private Floor maxFloor = Floor.of(10);
    private final Map<Floor, HallPanel> customHallPanels = new HashMap<>();
    private final Map<String, ElevatorPanel> customCabinPanels = new HashMap<>();

    public ElevatorSystemBuilder withFloorRange(Floor minFloor, Floor maxFloor) {
        if (minFloor == null || maxFloor == null) {
            throw new IllegalArgumentException("minFloor and maxFloor cannot be null");
        }
        if (minFloor.compareTo(maxFloor) >= 0) {
            throw new IllegalArgumentException("minFloor must be strictly less than maxFloor");
        }
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        return this;
    }

    public ElevatorSystemBuilder withFloors(List<Floor> floors) {
        if (floors == null || floors.isEmpty()) {
            throw new IllegalArgumentException("Floors list cannot be empty");
        }
        int min = floors.stream().mapToInt(Floor::getFloorNumber).min().orElse(0);
        int max = floors.stream().mapToInt(Floor::getFloorNumber).max().orElse(0);
        return withFloorRange(Floor.of(min), Floor.of(max));
    }

    public ElevatorSystemBuilder withDispatchStrategy(DispatchStrategy dispatchStrategy) {
        this.dispatchStrategy = dispatchStrategy;
        return this;
    }

    public ElevatorSystemBuilder withElevators(int count, int capacity) {
        return withElevators(count, capacity, new ScanQueueStrategy());
    }

    public ElevatorSystemBuilder withElevators(int count, int capacity, MovementStrategy movementStrategy) {
        for (int i = 1; i <= count; i++) {
            this.elevators.add(new Elevator("E" + i, capacity, movementStrategy));
        }
        return this;
    }

    public ElevatorSystemBuilder addElevator(Elevator elevator) {
        if (elevator != null) {
            this.elevators.add(elevator);
        }
        return this;
    }

    public ElevatorSystemBuilder withCustomHallPanel(Floor floor, HallPanel panel) {
        this.customHallPanels.put(floor, panel);
        return this;
    }

    public ElevatorSystemBuilder withCustomCabinPanel(String elevatorId, ElevatorPanel panel) {
        this.customCabinPanels.put(elevatorId, panel);
        return this;
    }

    public ElevatorSystem build() {
        if (elevators.isEmpty()) {
            throw new IllegalStateException("Cannot build ElevatorSystem with 0 elevators");
        }

        DispatchStrategy strategy = this.dispatchStrategy != null ? this.dispatchStrategy : new DirectionAwareDispatchStrategy();
        ElevatorDispatcher dispatcher = new ElevatorDispatcher(this.elevators, strategy);

        // Generate Hall Panels across floor range using Floor entities
        Map<Floor, HallPanel> hallPanels = new HashMap<>(customHallPanels);
        for (int floor = minFloor.getFloorNumber(); floor <= maxFloor.getFloorNumber(); floor++) {
            Floor floorObj = Floor.of(floor);
            if (!hallPanels.containsKey(floorObj)) {
                hallPanels.put(floorObj, PanelFactory.createHallPanel(dispatcher, floorObj, minFloor, maxFloor));
            }
        }
        dispatcher.setHallPanels(hallPanels);

        // Generate Cabin Panels for each car
        Map<String, ElevatorPanel> cabinPanels = new HashMap<>(customCabinPanels);
        for (Elevator elevator : elevators) {
            if (!cabinPanels.containsKey(elevator.getId())) {
                cabinPanels.put(elevator.getId(), PanelFactory.createElevatorPanel(elevator, minFloor, maxFloor));
            }
        }

        return new ElevatorSystem(dispatcher, elevators, hallPanels, cabinPanels);
    }
}
