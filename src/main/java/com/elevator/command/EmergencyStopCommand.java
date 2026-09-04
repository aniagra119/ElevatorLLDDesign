package com.elevator.command;

import com.elevator.command.receiver.EmergencyStopReceiver;

public class EmergencyStopCommand implements ElevatorCommand {
    private final EmergencyStopReceiver receiver;

    public EmergencyStopCommand(EmergencyStopReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.emergencyStop();
    }
}
