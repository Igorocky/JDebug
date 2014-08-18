package org.igye.jdebug.messages.constants;

public enum ClassStatus {
    VERIFIED(1),
    PREPARED(2),
    INITIALIZED(4),
    ERROR(8),
    ;
    private int code;

    ClassStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
