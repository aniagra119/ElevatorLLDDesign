package com.elevator.hardware;

import com.elevator.model.Elevator;
import com.elevator.model.Floor;
import com.elevator.observer.ElevatorObserver;

import java.util.HashMap;
import java.util.Map;

public class ElevatorPanel implements ElevatorObserver {
    private final Button emergencyStopButton;
    private final Button alarmButton;
    private final Map<Floor, Button> floorButtons;

    public ElevatorPanel(Map<Floor, Button> floorButtons, Button emergencyStopButton, Button alarmButton) {
        this.floorButtons = floorButtons != null ? floorButtons : new HashMap<>();
        this.emergencyStopButton = emergencyStopButton;
        this.alarmButton = alarmButton;
    }

    @Override
    public void update(Elevator elevator) {
        // When car arrives at floor with doors open, automatically turn off floor button light
        if (elevator.getDoor().isOpen()) {
            turnOffFloorButton(elevator.getCurrentFloor());
        }
    }

    public Map<Floor, Button> getFloorButtons() {
        return floorButtons;
    }

    public Button getButton(Floor floor) {
        return floorButtons.get(floor);
    }

    public Button getEmergencyStopButton() {
        return emergencyStopButton;
    }

    public Button getAlarmButton() {
        return alarmButton;
    }

    public void pressFloorButton(Floor floor) {
        Button btn = floorButtons.get(floor);
        if (btn != null) {
            btn.press();
        }
    }

    public void pressAlarmButton() {
        if (alarmButton != null) {
            alarmButton.press();
        }
    }

    public void pressEmergencyStopButton() {
        if (emergencyStopButton != null) {
            emergencyStopButton.press();
        }
    }

    public void turnOffFloorButton(Floor floor) {
        Button btn = floorButtons.get(floor);
        if (btn != null) {
            btn.turnOff();
        }
    }

    public void turnOffAlarmButton() {
        if (alarmButton != null) {
            alarmButton.turnOff();
        }
    }

    public void turnOffEmergencyStopButton() {
        if (emergencyStopButton != null) {
            emergencyStopButton.turnOff();
        }
    }
}