package com.elevator.command;

import com.elevator.command.receiver.ExternalRequestReceiver;
import com.elevator.enums.RequestType;
import com.elevator.model.Floor;
import com.elevator.model.Request;
public class ExternalRequestCommand implements ElevatorCommand {

    private final ExternalRequestReceiver receiver;
    private final Request request;

    public ExternalRequestCommand(ExternalRequestReceiver receiver, Floor floor, RequestType type) {
        this.receiver = receiver;
        this.request = new Request(floor, type);
    }

    @Override
    public void execute() {
        receiver.addRequest(request);
    }

    public Request getRequest() {
        return request;
    }
}