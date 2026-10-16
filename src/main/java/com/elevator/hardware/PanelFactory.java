package com.elevator.hardware;

import com.elevator.command.AlarmCommand;
import com.elevator.command.ElevatorCommand;
import com.elevator.command.EmergencyStopCommand;
import com.elevator.command.ExternalRequestCommand;
import com.elevator.command.InternalRequestCommand;
import com.elevator.enums.RequestType;
import com.elevator.model.Elevator;
import com.elevator.model.ElevatorDispatcher;
import com.elevator.model.Floor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PanelFactory {

    public static ElevatorPanel createElevatorPanel(Elevator elevator, List<Floor> floors) {
        if (floors == null || floors.isEmpty()) {
            throw new IllegalArgumentException("Floors list cannot be empty");
        }
        Map<Floor, Button> floorButtons = new HashMap<>();
        for (Floor floor : floors) {
            ElevatorCommand internalRequestCommand = new InternalRequestCommand(elevator, floor);
            Button floorButton = new Button(internalRequestCommand);
            floorButtons.put(floor, floorButton);
        }

        ElevatorCommand emergencyStopCommand = new EmergencyStopCommand(elevator);
        ElevatorCommand alarmCommand = new AlarmCommand(elevator);

        Button emergencyStopButton = new Button(emergencyStopCommand);
        Button alarmButton = new Button(alarmCommand);

        ElevatorPanel panel = new ElevatorPanel(floorButtons, emergencyStopButton, alarmButton);
        elevator.addObserver(panel);
        return panel;
    }

    public static ElevatorPanel createElevatorPanel(Elevator elevator, Floor minFloor, Floor maxFloor) {
        if (minFloor == null || maxFloor == null) {
            throw new IllegalArgumentException("Min and max floor cannot be null");
        }
        if (minFloor.compareTo(maxFloor) > 0) {
            throw new IllegalArgumentException("minFloor cannot be greater than maxFloor");
        }
        List<Floor> floors = new ArrayList<>();
        for (int f = minFloor.getFloorNumber(); f <= maxFloor.getFloorNumber(); f++) {
            floors.add(Floor.of(f));
        }
        return createElevatorPanel(elevator, floors);
    }

    public static HallPanel createHallPanel(ElevatorDispatcher dispatcher, Floor floor, Floor minFloor, Floor maxFloor) {
        if (floor == null || minFloor == null || maxFloor == null) {
            throw new IllegalArgumentException("Floor parameters cannot be null");
        }
        if (floor.compareTo(minFloor) < 0 || floor.compareTo(maxFloor) > 0) {
            throw new IllegalArgumentException("Floor is out of range");
        }

        Button upButton = null;
        Button downButton = null;

        if (floor.compareTo(maxFloor) < 0) {
            ElevatorCommand upCommand = new ExternalRequestCommand(dispatcher, floor, RequestType.EXTERNAL_UP);
            upButton = new Button(upCommand);
        }

        if (floor.compareTo(minFloor) > 0) {
            ElevatorCommand downCommand = new ExternalRequestCommand(dispatcher, floor, RequestType.EXTERNAL_DOWN);
            downButton = new Button(downCommand);
        }

        return new HallPanel(floor, upButton, downButton);
    }

    public static HallPanel createHallPanel(ElevatorDispatcher dispatcher, Floor floor) {
        if (floor == null) {
            throw new IllegalArgumentException("Floor cannot be null");
        }
        ElevatorCommand upCommand = new ExternalRequestCommand(dispatcher, floor, RequestType.EXTERNAL_UP);
        ElevatorCommand downCommand = new ExternalRequestCommand(dispatcher, floor, RequestType.EXTERNAL_DOWN);
        Button upButton = new Button(upCommand);
        Button downButton = new Button(downCommand);
        return new HallPanel(floor, upButton, downButton);
    }
}
