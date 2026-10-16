package com.elevator.strategy.movement;

import com.elevator.enums.Direction;
import com.elevator.enums.RequestType;
import com.elevator.model.Elevator;
import com.elevator.model.Floor;
import com.elevator.model.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScanQueueStrategyTest {

    private ScanQueueStrategy strategy;
    private Elevator elevator;

    @BeforeEach
    void setUp() {
        strategy = new ScanQueueStrategy();
        elevator = new Elevator("E1", 500, strategy);
    }

    @Test
    @DisplayName("Should climb floors sequentially, serve target floor, and become IDLE")
    void testSingleFloorRequestAndArrival() {
        assertEquals(0, elevator.getCurrentFloorNumber());
        assertEquals(Direction.IDLE, elevator.getCurrentDirection());

        strategy.addRequest(new Request(Floor.of(2), RequestType.EXTERNAL_UP), elevator);

        // Step 1: Moves UP to floor 1
        elevator.step();
        assertEquals(Direction.UP, elevator.getCurrentDirection());
        assertEquals(1, elevator.getCurrentFloorNumber());

        // Step 2: Arrives at floor 2
        elevator.step();
        assertEquals(2, elevator.getCurrentFloorNumber());

        // Step 3: Serves floor 2 (opens/closes door, removes from queue)
        elevator.step();
        assertEquals(2, elevator.getCurrentFloorNumber());

        // Step 4: No more requests in any queue, transitions to IDLE
        elevator.step();
        assertEquals(Direction.IDLE, elevator.getCurrentDirection());
    }

    @Test
    @DisplayName("Should complete upward sweep before reversing to complete downward sweep")
    void testUpwardAndDownwardSweep() {
        assertEquals(0, elevator.getCurrentFloorNumber());

        // Add an UP request at floor 3 and a DOWN request at floor 1
        strategy.addRequest(new Request(Floor.of(3), RequestType.EXTERNAL_UP), elevator);
        strategy.addRequest(new Request(Floor.of(1), RequestType.EXTERNAL_DOWN), elevator);

        // Climb to 3
        elevator.step(); // floor 1
        assertEquals(1, elevator.getCurrentFloorNumber());
        elevator.step(); // floor 2
        assertEquals(2, elevator.getCurrentFloorNumber());
        elevator.step(); // arrives at floor 3
        assertEquals(3, elevator.getCurrentFloorNumber());

        // Serves floor 3
        elevator.step();
        assertEquals(3, elevator.getCurrentFloorNumber());

        // Detects no more UP requests, switches direction to DOWN
        elevator.step();
        assertEquals(Direction.DOWN, elevator.getCurrentDirection());

        // Descends to 1
        elevator.step(); // floor 2
        assertEquals(2, elevator.getCurrentFloorNumber());
        elevator.step(); // arrives at floor 1
        assertEquals(1, elevator.getCurrentFloorNumber());

        // Serves floor 1
        elevator.step();
        assertEquals(1, elevator.getCurrentFloorNumber());

        // Transitions to IDLE
        elevator.step();
        assertEquals(Direction.IDLE, elevator.getCurrentDirection());
    }

    @Test
    @DisplayName("Should route INTERNAL requests based on current floor position")
    void testInternalRequestRouting() {
        elevator.setCurrentFloor(Floor.of(4));

        // Floor 6 is above -> upQueue; Floor 2 is below -> downQueue
        strategy.addRequest(new Request(Floor.of(6), RequestType.INTERNAL), elevator);
        strategy.addRequest(new Request(Floor.of(2), RequestType.INTERNAL), elevator);

        // Climbs towards 6
        elevator.step(); // floor 5
        assertEquals(5, elevator.getCurrentFloorNumber());
        assertEquals(Direction.UP, elevator.getCurrentDirection());

        elevator.step(); // arrives at floor 6
        assertEquals(6, elevator.getCurrentFloorNumber());

        // Serves floor 6
        elevator.step();
        assertEquals(6, elevator.getCurrentFloorNumber());

        // Reverses to DOWN
        elevator.step();
        assertEquals(Direction.DOWN, elevator.getCurrentDirection());

        // Descends towards 2
        elevator.step(); // floor 5
        elevator.step(); // floor 4
        elevator.step(); // floor 3
        elevator.step(); // arrives at floor 2
        assertEquals(2, elevator.getCurrentFloorNumber());

        // Serves floor 2
        elevator.step();
        assertEquals(2, elevator.getCurrentFloorNumber());

        // Transitions to IDLE
        elevator.step();
        assertEquals(Direction.IDLE, elevator.getCurrentDirection());
    }
}
