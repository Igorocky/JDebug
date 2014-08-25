package org.igye.jdebug.exceptions;

public class JDebugRuntimeException extends RuntimeException {
    public JDebugRuntimeException(Throwable cause) {
        super(cause);
    }

    public JDebugRuntimeException(String message) {
        super(message);
    }

    public JDebugRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
