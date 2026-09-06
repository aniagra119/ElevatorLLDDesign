package com.elevator.strategy.dispatch;

import com.elevator.enums.Direction;
import com.elevator.model.Elevator;
import com.elevator.model.Request;

import java.util.List;

/**
 * Energy-Saving Dispatch Strategy:
 * Minimizes total power consumption during off-peak / green hours by:
 * 1. Prioritizing already-moving cars to consolidate passenger trips and reuse momentum.
 * 2. Applying a startup power penalty to idle parked cars (preventing cold motor inrush power).
 * 3. Avoiding waking up multiple elevators when a single active car can batch the requests.
 */
public class EnergySaverDispatchStrategy extends BaseDispatchStrategy {

    private static final int MOTOR_STARTUP_ENERGY_PENALTY = 25;
    private static final int OPPOSITE_DIRECTION_PENALTY = 150;

    @Override
    protected Elevator selectFromOperatingElevators(Request request, List<Elevator> operatingElevators) {
        Elevator bestElevator = null;
        int lowestEnergyCost = Integer.MAX_VALUE;

        for (Elevator elevator : operatingElevators) {
            int energyCost = calculateEnergyCost(elevator, request);
            if (energyCost < lowestEnergyCost) {
                lowestEnergyCost = energyCost;
                bestElevator = elevator;
            }
        }

        return bestElevator;
    }

    private int calculateEnergyCost(Elevator elevator, Request request) {
        int distance = calculateDistance(elevator, request);

        // 1. Moving car heading towards request in same direction -> Most energy efficient!
        if (isApproachingInSameDirection(elevator, request)) {
            return distance;
        }

        // 2. Idle car -> Incurs motor startup / inrush current penalty to spin up from sleep
        if (elevator.getCurrentDirection() == Direction.IDLE) {
            return distance + MOTOR_STARTUP_ENERGY_PENALTY;
        }

        // 3. Moving away or opposite direction -> High turnaround travel energy
        return distance + OPPOSITE_DIRECTION_PENALTY;
    }
}
