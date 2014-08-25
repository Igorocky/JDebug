package org.igye.jdebug.messages.constants;

public enum Command {
    //VirtualMachine
    VERSION(1),
    ID_SIZES(7),
    RESUME(9),

    //EventRequest
    SET(1),

    //Event Command Set
    COMPOSITE_COMMAND(100),
    ;
    private int code;

    Command(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
