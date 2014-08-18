package org.igye.jdebug.messages.constants;

public enum Command {
    //VirtualMachine
    VERSION(1),
    ;
    private int code;

    Command(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
