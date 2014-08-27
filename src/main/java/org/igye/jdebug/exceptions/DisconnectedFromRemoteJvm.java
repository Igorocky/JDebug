package org.igye.jdebug.exceptions;

public class DisconnectedFromRemoteJvm extends RuntimeException {
    public DisconnectedFromRemoteJvm(String message) {
        super(message);
    }
}
