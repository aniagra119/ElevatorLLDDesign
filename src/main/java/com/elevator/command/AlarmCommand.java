package com.elevator.command;

import com.elevator.command.receiver.AlarmReceiver;

public class AlarmCommand implements ElevatorCommand {
    private final AlarmReceiver receiver;

    public AlarmCommand(AlarmReceiver receiver) {
        this.receiver = receiver;
    }

    @Override
    public void execute() {
        receiver.activateAlarm();
    }
}
