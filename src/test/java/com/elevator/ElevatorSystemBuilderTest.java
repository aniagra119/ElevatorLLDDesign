package com.elevator;

import com.elevator.enums.Direction;
import com.elevator.enums.ElevatorState;
import com.elevator.model.Elevator;
import com.elevator.model.Floor;
import com.elevator.strategy.dispatch.EnergySaverDispatchStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ElevatorSystemBuilderTest {

    @Test
    void testBuilderDefaultsAndAssembly() {
        ElevatorSystem system = new ElevatorSystemBuilder()
                .withElevators(3, 800)
                .withFloorRange(Floor.of(0), Floor.of(10))
                .build();

        assertNotNull(system);
        assertEquals(3, system.getElevators().size());
        assertNotNull(system.getDispatcher());

        for (Elevator elevator : system.getElevators()) {
            assertEquals(800, elevator.getCapacity());
            assertEquals(0, elevator.getCurrentFloorNumber());
            assertEquals(ElevatorState.OPERATING, elevator.getState());
        }
    }

    @Test
    void testBuilderWithCustomStrategyAndFloorRequest() {
        ElevatorSystem system = new ElevatorSystemBuilder()
                .withElevators(2, 1000)
                .withFloorRange(Floor.of(0), Floor.of(15))
                .withDispatchStrategy(new EnergySaverDispatchStrategy())
                .build();

        assertNotNull(system);
        assertInstanceOf(EnergySaverDispatchStrategy.class, system.getDispatcher().getDispatchStrategy());

        // Test normal workflow through builder-created system
        system.requestElevator(Floor.of(5), Direction.UP);
        system.step();

        // One elevator should have moved towards floor 5
        boolean moved = system.getElevators().stream().anyMatch(e -> e.getCurrentFloorNumber() > 0);
        assertTrue(moved);
    }

    @Test
    void testBuilderValidation() {
        assertThrows(IllegalStateException.class, () -> new ElevatorSystemBuilder().build());
        assertThrows(IllegalArgumentException.class, () -> new ElevatorSystemBuilder().withFloorRange(Floor.of(10), Floor.of(5)));
    }
}
