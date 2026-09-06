package com.elevator.model;

import com.elevator.command.receiver.ExternalRequestReceiver;
import com.elevator.hardware.HallPanel;
import com.elevator.observer.ElevatorObserver;
import com.elevator.strategy.dispatch.DispatchStrategy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElevatorDispatcher implements ElevatorObserver, ExternalRequestReceiver {
    private final List<Elevator> elevators;
    private final DispatchStrategy dispatchStrategy;
    private Map<Floor, HallPanel> hallPanels;

    public ElevatorDispatcher(List<Elevator> elevators, DispatchStrategy dispatchStrategy) {
        this(elevators, dispatchStrategy, new HashMap<>());
    }

    public ElevatorDispatcher(List<Elevator> elevators, DispatchStrategy dispatchStrategy, Map<Floor, HallPanel> hallPanels) {
        this.elevators = elevators;
        this.dispatchStrategy = dispatchStrategy;
        this.hallPanels = hallPanels != null ? hallPanels : new HashMap<>();

        // Subscribe dispatcher as observer to all elevators in fleet
        for (Elevator elevator : elevators) {
            elevator.addObserver(this);
        }
    }

    @Override
    public void update(Elevator elevator) {
        // When any car arrives at a floor with open doors, turn off the hallway button light!
        if (elevator.getDoor().isOpen() && hallPanels != null) {
            HallPanel panel = hallPanels.get(elevator.getCurrentFloor());
            if (panel != null) {
                panel.turnOff(elevator.getCurrentDirection());
            }
        }
    }

    public void setHallPanels(Map<Floor, HallPanel> hallPanels) {
        this.hallPanels = hallPanels;
    }

    public Map<Floor, HallPanel> getHallPanels() {
        return hallPanels;
    }

    /**
     * Receives an external hall call from a HallPanel and dispatches it
     * to the best available elevator car using the configured strategy.
     */
    @Override
    public void addRequest(Request request) {
        Elevator best = dispatchStrategy.selectBestElevator(request, elevators);
        if (best != null) {
            best.addRequest(request);
        } else {
            System.err.println("No available operating elevator to service request: " + request);
        }
    }

    /**
     * Advances all elevators in the fleet by one simulation step.
     */
    public void step() {
        for (Elevator elevator : elevators) {
            elevator.step();
        }
    }

    public List<Elevator> getElevators() {
        return Collections.unmodifiableList(elevators);
    }

    public DispatchStrategy getDispatchStrategy() {
        return dispatchStrategy;
    }
}
