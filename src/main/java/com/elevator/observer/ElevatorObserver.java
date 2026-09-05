package com.elevator.observer;

import com.elevator.model.Elevator;
public interface ElevatorObserver {
    void update(Elevator elevator);
}