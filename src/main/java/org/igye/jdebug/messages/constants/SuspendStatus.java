package org.igye.jdebug.messages.constants;

public enum SuspendStatus {
    SUSPEND_STATUS_SUSPENDED(1)
    ;
    private int code;

    SuspendStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
