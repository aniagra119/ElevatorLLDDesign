package com.elevator.strategy.dispatch;

import com.elevator.enums.Direction;
import com.elevator.enums.RequestType;
import com.elevator.model.Elevator;
import com.elevator.model.Floor;
import com.elevator.model.Request;
import com.elevator.strategy.movement.ScanQueueStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnergySaverDispatchStrategyTest {

    private EnergySaverDispatchStrategy energySaverStrategy;

    @BeforeEach
    void setUp() {
        energySaverStrategy = new EnergySaverDispatchStrategy();
    }

    @Test
    @DisplayName("Should prioritize already-moving car to avoid spinning up an idle motor")
    void testPrioritizesActiveCarOverIdleCarToConsolidatePower() {
        // Car 1: Already moving UP at floor 1 (distance to floor 5 is 4)
        Elevator car1 = new Elevator("E1", 500, new ScanQueueStrategy());
        car1.setCurrentFloor(Floor.of(1));
        car1.setCurrentDirection(Direction.UP);

        // Car 2: Parked IDLE at floor 4 (distance to floor 5 is only 1, but has startup power cost)
        Elevator car2 = new Elevator("E2", 500, new ScanQueueStrategy());
        car2.setCurrentFloor(Floor.of(4));
        car2.setCurrentDirection(Direction.IDLE);

        Request request = new Request(Floor.of(5), RequestType.EXTERNAL_UP);

        Elevator selected = energySaverStrategy.selectBestElevator(request, List.of(car1, car2));
        assertEquals("E1", selected.getId(),
                "Energy saver should consolidate request into active car E1 to avoid spinning up idle motor E2");
    }

    @Test
    @DisplayName("Should wake up idle car if active car is moving away in opposite direction")
    void testWakesUpIdleCarWhenActiveCarIsMovingAway() {
        // Car 1: At floor 8 moving UP (moving away from floor 3)
        Elevator car1 = new Elevator("E1", 500, new ScanQueueStrategy());
        car1.setCurrentFloor(Floor.of(8));
        car1.setCurrentDirection(Direction.UP);

        // Car 2: Parked IDLE at floor 0
        Elevator car2 = new Elevator("E2", 500, new ScanQueueStrategy());
        car2.setCurrentFloor(Floor.of(0));
        car2.setCurrentDirection(Direction.IDLE);

        Request request = new Request(Floor.of(3), RequestType.EXTERNAL_UP);

        Elevator selected = energySaverStrategy.selectBestElevator(request, List.of(car1, car2));
        assertEquals("E2", selected.getId(),
                "Should wake up idle car E2 rather than forcing car E1 to complete a long opposite sweep");
    }
}
