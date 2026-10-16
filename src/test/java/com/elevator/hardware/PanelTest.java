package com.elevator.hardware;

import com.elevator.enums.Direction;
import com.elevator.model.Elevator;
import com.elevator.model.Floor;
import com.elevator.strategy.movement.MovementStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class PanelTest {

    @Test
    @DisplayName("HallPanel should illuminate buttons on press and turn off by direction")
    void testHallPanelButtonInteraction() {
        AtomicInteger upCount = new AtomicInteger(0);
        AtomicInteger downCount = new AtomicInteger(0);

        Button upButton = new Button(upCount::incrementAndGet);
        Button downButton = new Button(downCount::incrementAndGet);

        HallPanel panel = new HallPanel(Floor.of(2), upButton, downButton);

        assertEquals(Floor.of(2), panel.getFloor());
        assertEquals(2, panel.getFloorNumber());

        panel.pressUpButton();
        assertTrue(upButton.isIlluminated());
        assertEquals(1, upCount.get());

        panel.pressDownButton();
        assertTrue(downButton.isIlluminated());
        assertEquals(1, downCount.get());

        panel.turnOff(Direction.UP);
        assertFalse(upButton.isIlluminated());
        assertTrue(downButton.isIlluminated());

        panel.turnOff(Direction.DOWN);
        assertFalse(downButton.isIlluminated());
    }

    @Test
    @DisplayName("HallPanel should throw IllegalStateException when pressing unconfigured button")
    void testHallPanelMissingButtonThrows() {
        HallPanel topFloorPanel = new HallPanel(Floor.of(10), null, new Button(() -> {
        }));

        assertThrows(IllegalStateException.class, topFloorPanel::pressUpButton);
        assertThrows(IllegalStateException.class, topFloorPanel::getUpButton);
        assertNotNull(topFloorPanel.getDownButton());
    }

    @Test
    @DisplayName("ElevatorPanel should support floor, alarm, and emergency stop buttons")
    void testElevatorPanelFullLifecycle() {
        AtomicBoolean alarmTriggered = new AtomicBoolean(false);
        AtomicBoolean emergencyTriggered = new AtomicBoolean(false);
        AtomicBoolean floor3Triggered = new AtomicBoolean(false);

        Button alarmBtn = new Button(() -> alarmTriggered.set(true));
        Button emergencyBtn = new Button(() -> emergencyTriggered.set(true));
        Button floor3Btn = new Button(() -> floor3Triggered.set(true));

        Map<Floor, Button> floorButtons = new HashMap<>();
        floorButtons.put(Floor.of(3), floor3Btn);

        ElevatorPanel panel = new ElevatorPanel(floorButtons, emergencyBtn, alarmBtn);

        // Floor button interaction
        panel.pressFloorButton(Floor.of(3));
        assertTrue(floor3Triggered.get());
        assertTrue(floor3Btn.isIlluminated());

        panel.turnOffFloorButton(Floor.of(3));
        assertFalse(floor3Btn.isIlluminated());

        // Alarm button interaction
        panel.pressAlarmButton();
        assertTrue(alarmTriggered.get());
        assertTrue(alarmBtn.isIlluminated());

        panel.turnOffAlarmButton();
        assertFalse(alarmBtn.isIlluminated());

        // Emergency button interaction
        panel.pressEmergencyStopButton();
        assertTrue(emergencyTriggered.get());
        assertTrue(emergencyBtn.isIlluminated());

        panel.turnOffEmergencyStopButton();
        assertFalse(emergencyBtn.isIlluminated());
    }

    @Test
    @DisplayName("ElevatorPanel should automatically turn off arrived floor button when elevator door opens")
    void testElevatorPanelAutoShutoffOnDoorOpen() {
        Button floor2Btn = new Button(() -> {
        });
        floor2Btn.press();
        assertTrue(floor2Btn.isIlluminated());

        Map<Floor, Button> floorButtons = new HashMap<>();
        floorButtons.put(Floor.of(2), floor2Btn);

        ElevatorPanel panel = new ElevatorPanel(floorButtons, null, null);

        Elevator dummyElevator = new Elevator("E1", 500, new MovementStrategy() {
            @Override
            public void addRequest(com.elevator.model.Request request, Elevator elevator) {
            }

            @Override
            public void step(Elevator elevator) {
            }
        });

        dummyElevator.setCurrentFloor(Floor.of(2));
        dummyElevator.openDoor();

        panel.update(dummyElevator);
        assertFalse(floor2Btn.isIlluminated(), "Floor 2 button must auto-shutoff when elevator door opens on Floor 2");
    }
}
