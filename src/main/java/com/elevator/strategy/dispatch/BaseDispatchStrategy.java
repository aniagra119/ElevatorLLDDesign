package com.elevator.strategy.dispatch;

import com.elevator.enums.Direction;
import com.elevator.enums.ElevatorState;
import com.elevator.enums.RequestType;
import com.elevator.model.Elevator;
import com.elevator.model.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class providing common dispatch infrastructure:
 * - Filtering operational elevators
 * - Calculating physical Manhattan distance
 * - Direction alignment matching
 * - Implements Template Method pattern in selectBestElevator(...)
 */
public abstract class BaseDispatchStrategy implements DispatchStrategy {

    @Override
    public final Elevator selectBestElevator(Request request, List<Elevator> elevatorList) {
        List<Elevator> operatingElevators = filterOperatingElevators(elevatorList);
        if (operatingElevators.isEmpty()) {
            return null;
        }
        return selectFromOperatingElevators(request, operatingElevators);
    }

    /**
     * Primitive hook for subclasses to apply their custom scoring/cost algorithm.
     */
    protected abstract Elevator selectFromOperatingElevators(Request request, List<Elevator> operatingElevators);

    /**
     * Shared filter: Only elevators in OPERATING state can be assigned new tasks.
     */
    protected List<Elevator> filterOperatingElevators(List<Elevator> elevators) {
        List<Elevator> operating = new ArrayList<>();
        if (elevators != null) {
            for (Elevator elevator : elevators) {
                if (elevator.getState() == ElevatorState.OPERATING) {
                    operating.add(elevator);
                }
            }
        }
        return operating;
    }

    /**
     * Shared Manhattan distance calculator between current car position and call floor.
     */
    protected int calculateDistance(Elevator elevator, Request request) {
        return Math.abs(elevator.getCurrentFloor().getFloorNumber() - request.getFloorNumber());
    }

    /**
     * Shared direction matcher: Checks if the elevator is already moving toward
     * the requested floor in the requested direction.
     */
    protected boolean isApproachingInSameDirection(Elevator elevator, Request request) {
        if (request.getType() == RequestType.EXTERNAL_UP) {
            return elevator.getCurrentDirection() == Direction.UP
                    && elevator.getCurrentFloor().compareTo(request.getFloor()) <= 0;
        } else if (request.getType() == RequestType.EXTERNAL_DOWN) {
            return elevator.getCurrentDirection() == Direction.DOWN
                    && elevator.getCurrentFloor().compareTo(request.getFloor()) >= 0;
        }
        return false;
    }
}
