package com.elevator.hardware;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class WeightSensorTest {

    private WeightSensor sensor;

    @BeforeEach
    void setUp() {
        sensor = new WeightSensor(1000);
    }

    @Test
    void testInitialState() {
        assertEquals(1000, sensor.getMaxCapacity());
        assertEquals(0, sensor.getCurrentWeight());
        assertTrue(sensor.isCalibrated());
        assertFalse(sensor.isOverloaded());
    }

    @Test
    void testWeightUpdatesAndOverload() {
        sensor.updateWeight(800);
        assertEquals(800, sensor.getCurrentWeight());
        assertFalse(sensor.isOverloaded());

        sensor.updateWeight(1000);
        assertFalse(sensor.isOverloaded());

        sensor.updateWeight(1001);
        assertTrue(sensor.isOverloaded());
    }

    @Test
    void testCalibration() {
        sensor.updateWeight(500);
        assertEquals(500, sensor.getCurrentWeight());

        sensor.calibrate();
        assertEquals(0, sensor.getCurrentWeight());
        assertTrue(sensor.isCalibrated());
        assertFalse(sensor.isOverloaded());
    }

    @Test
    void testInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> new WeightSensor(0));
        assertThrows(IllegalArgumentException.class, () -> new WeightSensor(-500));
        assertThrows(IllegalArgumentException.class, () -> sensor.updateWeight(-10));
    }
}
