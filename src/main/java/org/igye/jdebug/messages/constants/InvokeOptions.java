package org.igye.jdebug.messages.constants;

public enum InvokeOptions {
    INVOKE_SINGLE_THREADED(1, "otherwise, all threads started."),
    INVOKE_NONVIRTUAL(2, "otherwise, normal virtual invoke (instance methods only)"),
    ;
    private int code;
    private String description;

    InvokeOptions(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }
}
