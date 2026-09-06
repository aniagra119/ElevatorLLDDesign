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

class DirectionAwareDispatchStrategyTest {

    private DirectionAwareDispatchStrategy dispatchStrategy;

    @BeforeEach
    void setUp() {
        dispatchStrategy = new DirectionAwareDispatchStrategy();
    }

    @Test
    @DisplayName("Should prefer elevator approaching the floor in the requested direction")
    void testPrefersApproachingCarInSameDirection() {
        Elevator car1 = new Elevator("E1", 500, new ScanQueueStrategy());
        car1.setCurrentFloor(Floor.of(2));
        car1.setCurrentDirection(Direction.UP);

        Elevator car2 = new Elevator("E2", 500, new ScanQueueStrategy());
        car2.setCurrentFloor(Floor.of(3));
        car2.setCurrentDirection(Direction.DOWN); // moving away from 4!

        Request request = new Request(Floor.of(4), RequestType.EXTERNAL_UP);

        Elevator selected = dispatchStrategy.selectBestElevator(request, List.of(car1, car2));
        assertEquals("E1", selected.getId(), "Should select E1 because it is approaching in the same direction");
    }

    @Test
    @DisplayName("Should prefer IDLE elevator over one moving in the opposite direction")
    void testPrefersIdleCarOverOppositeDirectionCar() {
        Elevator car1 = new Elevator("E1", 500, new ScanQueueStrategy());
        car1.setCurrentFloor(Floor.of(8));
        car1.setCurrentDirection(Direction.UP); // moving away from 2

        Elevator car2 = new Elevator("E2", 500, new ScanQueueStrategy());
        car2.setCurrentFloor(Floor.of(1));
        car2.setCurrentDirection(Direction.IDLE); // ready immediately

        Request request = new Request(Floor.of(2), RequestType.EXTERNAL_UP);

        Elevator selected = dispatchStrategy.selectBestElevator(request, List.of(car1, car2));
        assertEquals("E2", selected.getId(), "Should select idle car E2 over car moving away");
    }

    @Test
    @DisplayName("Should ignore cars in emergency stop or maintenance mode")
    void testIgnoresNonOperatingCars() {
        Elevator car1 = new Elevator("E1", 500, new ScanQueueStrategy());
        car1.setCurrentFloor(Floor.of(5));
        car1.emergencyStop(); // EMERGENCY_STOP

        Elevator car2 = new Elevator("E2", 500, new ScanQueueStrategy());
        car2.setCurrentFloor(Floor.of(1));
        car2.setCurrentDirection(Direction.IDLE);

        Request request = new Request(Floor.of(5), RequestType.EXTERNAL_UP);

        Elevator selected = dispatchStrategy.selectBestElevator(request, List.of(car1, car2));
        assertEquals("E2", selected.getId(), "Must never assign jobs to a car in emergency stop");
    }
}
