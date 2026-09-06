package com.elevator.strategy.dispatch;

import com.elevator.model.Elevator;
import com.elevator.model.Request;

import java.util.List;

/**
 * Advanced Dispatch Strategy that estimates Arrival Time (ETA)
 * by simulating future trajectory, stops, and passenger boarding times.
 */
public class EtaSimulationDispatchStrategy extends BaseDispatchStrategy {

    @Override
    protected Elevator selectFromOperatingElevators(Request request, List<Elevator> operatingElevators) {
        // Fallback to DirectionAwareDispatchStrategy until full simulation engine is wired
        return new DirectionAwareDispatchStrategy().selectBestElevator(request, operatingElevators);
    }
}
