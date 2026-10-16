package com.elevator.command.receiver;

import com.elevator.model.Request;
/**
 * Abstraction for any component that can accept an internal cabin floor selection request.
 * Decouples commands from concrete elevator cars (Dependency Inversion Principle).
 */
public interface InternalRequestReceiver {
    void addRequest(Request request);
}
