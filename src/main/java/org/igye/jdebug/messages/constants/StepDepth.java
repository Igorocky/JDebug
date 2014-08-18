package org.igye.jdebug.messages.constants;

public enum StepDepth {
    INTO(0, "Step into any method calls that occur before the end of the step."),
    OVER(1, "Step over any method calls that occur before the end of the step."),
    OUT(2, "Step out of the current method."),
    ;
    private int code;
    private String description;

    StepDepth(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }
}
