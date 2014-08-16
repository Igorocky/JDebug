package org.igye.jdebug.exceptions;

public class JDebugRuntimeException extends RuntimeException {
    public JDebugRuntimeException(String message) {
        super(message);
    }

    public JDebugRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
