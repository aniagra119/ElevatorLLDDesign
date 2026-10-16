package com.elevator.command;

import com.elevator.command.receiver.InternalRequestReceiver;
import com.elevator.enums.RequestType;
import com.elevator.model.Floor;
import com.elevator.model.Request;
public class InternalRequestCommand implements ElevatorCommand {

    private final Request request;
    private final InternalRequestReceiver receiver;

    public InternalRequestCommand(InternalRequestReceiver receiver, Floor floor) {
        this.receiver = receiver;
        this.request = new Request(floor, RequestType.INTERNAL);
    }

    @Override
    public void execute() {
        receiver.addRequest(request);
    }

    public Request getRequest() {
        return request;
    }
}