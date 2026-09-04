package com.elevator.command.receiver;

/**
 * Abstraction for any hardware or subsystem capable of raising an emergency alarm.
 * Follows Dependency Inversion Principle (DIP).
 */
public interface AlarmReceiver {
    void activateAlarm();
}
