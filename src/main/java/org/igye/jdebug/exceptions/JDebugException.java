package org.igye.jdebug.exceptions;

public class JDebugException extends Exception {
    public JDebugException(String message) {
        super(message);
    }

    public JDebugException(String message, Throwable cause) {
        super(message, cause);
    }
}
