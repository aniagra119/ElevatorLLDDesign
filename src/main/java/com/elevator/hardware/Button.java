package com.elevator.hardware;

import com.elevator.command.ElevatorCommand;
public class Button {
    private boolean isIlluminated;
    private final ElevatorCommand command;

    public Button(ElevatorCommand command) {
        this.command = command;
    }

    public void press() {
        if (isIlluminated) {
            return;
        }
        isIlluminated = true;
        command.execute();
    }

    public void turnOff() {
        isIlluminated = false;
    }

    public boolean isIlluminated() {
        return isIlluminated;
    }
}