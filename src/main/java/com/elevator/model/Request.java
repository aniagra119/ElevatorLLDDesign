package com.elevator.model;

import java.util.Objects;
import com.elevator.enums.RequestType;
/**
 * Immutable value object encapsulating a floor call and direction/origin type.
 * Holds a rich Floor domain entity.
 */
public class Request {
    private final Floor floor;
    private final RequestType type;

    public Request(Floor floor, RequestType type) {
        if (floor == null) {
            throw new IllegalArgumentException("Floor cannot be null");
        }
        this.floor = floor;
        this.type = type;
    }

    public Floor getFloor() {
        return floor;
    }

    public int getFloorNumber() {
        return floor.getFloorNumber();
    }

    public RequestType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(this.floor, request.floor) && this.type == request.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(floor, type);
    }

    @Override
    public String toString() {
        return "Request(floor=" + floor + ", type=" + type + ")";
    }
}