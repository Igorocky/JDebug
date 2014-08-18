package org.igye.jdebug.messages.constants;

public enum StepSize {
    MIN(0, "Step by the minimum possible amount (often a bytecode instruction)."),
    LINE(1, "Step to the next source line unless there is no line number information in which case a MIN step is done instead."),
    ;
    private int code;
    private String description;

    StepSize(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }
}
