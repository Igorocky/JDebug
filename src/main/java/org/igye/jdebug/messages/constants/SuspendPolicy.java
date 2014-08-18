package org.igye.jdebug.messages.constants;

public enum SuspendPolicy {
    NONE(0, "Suspend no threads when this event is encountered."),
    EVENT_THREAD(1, "Suspend the event thread when this event is encountered."),
    ALL(2, "Suspend all threads when this event is encountered."),
    ;
    private int code;
    private String description;

    SuspendPolicy(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }
}
