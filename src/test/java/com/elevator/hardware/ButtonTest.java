package com.elevator.hardware;

import com.elevator.command.ElevatorCommand;
import com.elevator.command.NoOpCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
class ButtonTest {

    @Test
    @DisplayName("Button should illuminate and execute command on first press")
    void testFirstPressExecutesCommand() {
        AtomicInteger executions = new AtomicInteger(0);
        ElevatorCommand command = executions::incrementAndGet;

        Button button = new Button(command);
        assertFalse(button.isIlluminated());

        button.press();
        assertTrue(button.isIlluminated());
        assertEquals(1, executions.get());
    }

    @Test
    @DisplayName("Duplicate press should be ignored while button is illuminated")
    void testDuplicatePressIgnored() {
        AtomicInteger executions = new AtomicInteger(0);
        ElevatorCommand command = executions::incrementAndGet;

        Button button = new Button(command);
        button.press();
        assertEquals(1, executions.get());

        // Second press while illuminated
        button.press();
        assertEquals(1, executions.get(), "Duplicate press must not re-trigger command");
        assertTrue(button.isIlluminated());
    }

    @Test
    @DisplayName("TurnOff should reset illumination allowing future presses")
    void testTurnOffAllowsRepress() {
        AtomicInteger executions = new AtomicInteger(0);
        ElevatorCommand command = executions::incrementAndGet;

        Button button = new Button(command);
        button.press();
        button.turnOff();

        assertFalse(button.isIlluminated());
        button.press();
        assertEquals(2, executions.get(), "Should execute again after turning off");
    }

    @Test
    @DisplayName("NoOpCommand should safely execute as Null Object without error")
    void testNoOpCommandNullObject() {
        Button dummyButton = new Button(NoOpCommand.INSTANCE);
        assertFalse(dummyButton.isIlluminated());

        assertDoesNotThrow(dummyButton::press);
        assertTrue(dummyButton.isIlluminated());

        dummyButton.turnOff();
        assertFalse(dummyButton.isIlluminated());
    }
}
