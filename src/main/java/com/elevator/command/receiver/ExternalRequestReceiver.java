package com.elevator.command.receiver;

import com.elevator.model.Request;
/**
 * Abstraction for any component that can accept an external hall call request.
 * Decouples commands from concrete dispatchers (Dependency Inversion Principle).
 */
public interface ExternalRequestReceiver {
    void addRequest(Request request);
}
