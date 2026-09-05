package com.elevator.strategy.movement;

import com.elevator.model.Elevator;
import com.elevator.model.Request;
public interface MovementStrategy {
    void addRequest(Request request, Elevator elevator);

    void step(Elevator elevator);
}
