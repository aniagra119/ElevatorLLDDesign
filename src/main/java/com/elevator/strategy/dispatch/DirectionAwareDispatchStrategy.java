package com.elevator.strategy.dispatch;

import com.elevator.enums.Direction;
import com.elevator.model.Elevator;
import com.elevator.model.Request;

import java.util.List;

public class DirectionAwareDispatchStrategy extends BaseDispatchStrategy {

    @Override
    protected Elevator selectFromOperatingElevators(Request request, List<Elevator> operatingElevators) {
        Elevator bestElevator = null;
        int bestScore = Integer.MAX_VALUE;

        for (Elevator elevator : operatingElevators) {
            int score = calculateScore(elevator, request);
            if (score < bestScore) {
                bestScore = score;
                bestElevator = elevator;
            }
        }

        return bestElevator;
    }

    private int calculateScore(Elevator elevator, Request request) {
        int distance = calculateDistance(elevator, request);

        // Scenario 1: Approaching the request floor in the same direction -> Ideal match!
        if (isApproachingInSameDirection(elevator, request)) {
            return distance;
        }

        // Scenario 2: Idle elevator -> Good candidate, can start immediately
        if (elevator.getCurrentDirection() == Direction.IDLE) {
            return distance + 5;
        }

        // Scenario 3: Moving away or opposite direction -> Heavy penalty
        return distance + 100;
    }
}
