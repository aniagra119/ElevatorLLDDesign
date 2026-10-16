package com.elevator.hardware;

import com.elevator.enums.Direction;
import com.elevator.model.Floor;
public class HallPanel {
    private final Floor floor;
    private final Button upButton;
    private final Button downButton;

    public HallPanel(Floor floor, Button upButton, Button downButton) {
        if (floor == null) {
            throw new IllegalArgumentException("Floor cannot be null");
        }
        this.floor = floor;
        this.upButton = upButton;
        this.downButton = downButton;
    }

    public Floor getFloor() {
        return floor;
    }

    public int getFloorNumber() {
        return floor.getFloorNumber();
    }

    public Button getUpButton() {
        if (upButton != null) {
            return upButton;
        }
        throw new IllegalStateException("Up button not initialized");
    }

    public Button getDownButton() {
        if (downButton != null) {
            return downButton;
        }
        throw new IllegalStateException("Down button not initialized");
    }

    public void pressUpButton() {
        if (upButton == null) {
            throw new IllegalStateException("Up button not initialized");
        }
        upButton.press();
    }

    public void pressDownButton() {
        if (downButton == null) {
            throw new IllegalStateException("Down button not initialized");
        }
        downButton.press();
    }

    public void turnOff(Direction direction) {
        if (direction == Direction.UP && upButton != null) {
            upButton.turnOff();
        } else if (direction == Direction.DOWN && downButton != null) {
            downButton.turnOff();
        }
    }
}