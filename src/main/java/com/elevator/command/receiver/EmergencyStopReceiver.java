package com.elevator.command.receiver;

/**
 * Abstraction for any hardware or subsystem capable of executing an emergency stop.
 * Follows Dependency Inversion Principle (DIP).
 */
public interface EmergencyStopReceiver {
    void emergencyStop();
}
