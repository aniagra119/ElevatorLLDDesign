package com.elevator.hardware;

/**
 * Physical sensor measuring the load/weight inside the elevator cabin.
 * Encapsulates calibration, tare, and threshold breach detection.
 */
public class WeightSensor {
    private final int maxCapacity;
    private int currentWeight;
    private boolean isCalibrated;

    public WeightSensor(int maxCapacity) {
        if (maxCapacity <= 0) {
            throw new IllegalArgumentException("Max capacity must be positive: " + maxCapacity);
        }
        this.maxCapacity = maxCapacity;
        this.currentWeight = 0;
        this.isCalibrated = true;
    }

    public void updateWeight(int weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Weight cannot be negative: " + weight);
        }
        this.currentWeight = weight;
    }

    public boolean isOverloaded() {
        return currentWeight > maxCapacity;
    }

    public int getCurrentWeight() {
        return currentWeight;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public boolean isCalibrated() {
        return isCalibrated;
    }

    public void calibrate() {
        this.isCalibrated = true;
        this.currentWeight = 0;
    }
}
