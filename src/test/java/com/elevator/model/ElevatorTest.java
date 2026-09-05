package com.elevator.model;

import com.elevator.enums.Direction;
import com.elevator.enums.ElevatorState;
import com.elevator.observer.ElevatorObserver;
import com.elevator.strategy.movement.MovementStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;
class ElevatorTest {

    private Elevator elevator;
    private MovementStrategy dummyStrategy;

    @BeforeEach
    void setUp() {
        dummyStrategy = new MovementStrategy() {
            @Override
            public void addRequest(Request request, Elevator elevator) {
            }

            @Override
            public void step(Elevator elevator) {
            }
        };
        elevator = new Elevator("E1", 500, dummyStrategy);
    }

    @Test
    @DisplayName("Should detect overload and refuse to close door")
    void testOverloadPreventsDoorClosing() {
        elevator.getDoor().open();
        elevator.setCurrentLoad(600);
        assertTrue(elevator.isOverloaded());

        boolean closed = elevator.closeDoor();
        assertFalse(closed, "Door should not close when elevator is overloaded");
        assertTrue(elevator.getDoor().isOpen(), "Door should remain open");
    }

    @Test
    @DisplayName("Should close door when load is within capacity")
    void testNormalDoorClosing() {
        elevator.setCurrentLoad(300);
        assertFalse(elevator.isOverloaded());

        boolean closed = elevator.closeDoor();
        assertTrue(closed, "Door should close when within capacity");
        assertFalse(elevator.getDoor().isOpen());
    }

    @Test
    @DisplayName("Emergency stop should open door and transition state")
    void testEmergencyStop() {
        elevator.closeDoor();
        assertFalse(elevator.getDoor().isOpen());

        elevator.emergencyStop();
        assertEquals(ElevatorState.EMERGENCY_STOP, elevator.getState());
        assertTrue(elevator.getDoor().isOpen(), "Door must open during emergency stop");
    }

    @Test
    @DisplayName("Observers should receive updates on floor and direction changes")
    void testObserverNotifications() {
        AtomicBoolean updated = new AtomicBoolean(false);
        ElevatorObserver observer = car -> updated.set(true);

        elevator.addObserver(observer);

        elevator.setCurrentFloor(Floor.of(3));
        assertTrue(updated.get(), "Observer should be notified on floor change");

        updated.set(false);
        elevator.setCurrentDirection(Direction.UP);
        assertTrue(updated.get(), "Observer should be notified on direction change");
    }
}
