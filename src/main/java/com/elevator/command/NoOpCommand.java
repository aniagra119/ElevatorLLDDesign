package com.elevator.command;

/**
 * Null Object pattern implementation for ElevatorCommand.
 * Represents an intentional no-operation command, eliminating null checks
 * when constructing dummy, placeholder, or unassigned hardware buttons.
 */
public class NoOpCommand implements ElevatorCommand {
    public static final NoOpCommand INSTANCE = new NoOpCommand();

    @Override
    public void execute() {
        // Intentional no-operation (Null Object Pattern)
    }
}
