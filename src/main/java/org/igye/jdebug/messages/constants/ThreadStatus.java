package org.igye.jdebug.messages.constants;

public enum ThreadStatus {
    ZOMBIE(0),
    RUNNING(1),
    SLEEPING(2),
    MONITOR(3),
    WAIT(4),
    ;
    private int code;

    ThreadStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
