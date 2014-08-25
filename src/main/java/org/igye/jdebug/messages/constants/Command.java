package org.igye.jdebug.messages.constants;

public enum Command {
    //VirtualMachine
    VERSION(1),
    ALL_CLASSES(3),
    ID_SIZES(7),
    RESUME(9),

    //EventRequest
    SET(1),

    //Event Command Set
    COMPOSITE_COMMAND(100),

    //ThreadReference
    THREAD_NAME(1),
    ;
    private int code;

    Command(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
