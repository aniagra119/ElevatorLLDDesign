package com.elevator.strategy.dispatch;

import java.util.List;
import com.elevator.model.Elevator;
import com.elevator.model.Request;
public interface DispatchStrategy {
    Elevator selectBestElevator(Request request, List<Elevator> elevatorList);
}
