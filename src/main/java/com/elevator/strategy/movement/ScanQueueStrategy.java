package com.elevator.strategy.movement;

import com.elevator.enums.Direction;
import com.elevator.enums.RequestType;
import com.elevator.model.Elevator;
import com.elevator.model.Floor;
import com.elevator.model.Request;

import java.util.TreeSet;

public class ScanQueueStrategy implements MovementStrategy {

    private final TreeSet<Floor> upQueue = new TreeSet<>();
    private final TreeSet<Floor> downQueue = new TreeSet<>();

    @Override
    public void addRequest(Request request, Elevator elevator) {
        Floor floor = request.getFloor();
        if (request.getType() == RequestType.EXTERNAL_UP) {
            upQueue.add(floor);
        } else if (request.getType() == RequestType.EXTERNAL_DOWN) {
            downQueue.add(floor);
        } else {
            if (floor.compareTo(elevator.getCurrentFloor()) > 0) {
                upQueue.add(floor);
            } else {
                downQueue.add(floor);
            }
        }
    }

    @Override
    public void step(Elevator elevator) {
        if (upQueue.isEmpty() && downQueue.isEmpty()) {
            elevator.setCurrentDirection(Direction.IDLE);
            return;
        }

        if (elevator.getCurrentDirection() == Direction.IDLE) {
            elevator.setCurrentDirection(!upQueue.isEmpty() ? Direction.UP : Direction.DOWN);
        }

        if (elevator.getCurrentDirection() == Direction.UP) {
            moveUp(elevator);
        } else if (elevator.getCurrentDirection() == Direction.DOWN) {
            moveDown(elevator);
        }
    }

    private void serveFloor(Elevator elevator, TreeSet<Floor> queue, Floor floor) {
        elevator.openDoor();
        elevator.closeDoor();
        queue.remove(floor);
    }

    private void moveUp(Elevator elevator) {
        Floor next = upQueue.ceiling(elevator.getCurrentFloor());
        if (next == null) {
            elevator.setCurrentDirection(Direction.DOWN);
            return;
        }

        if (elevator.getCurrentFloor().equals(next)) {
            serveFloor(elevator, upQueue, next);
        } else {
            elevator.setCurrentFloor(elevator.getCurrentFloor().next());
        }
    }

    private void moveDown(Elevator elevator) {
        Floor next = downQueue.floor(elevator.getCurrentFloor());
        if (next == null) {
            elevator.setCurrentDirection(Direction.UP);
            return;
        }

        if (elevator.getCurrentFloor().equals(next)) {
            serveFloor(elevator, downQueue, next);
        } else {
            elevator.setCurrentFloor(elevator.getCurrentFloor().previous());
        }
    }
}
