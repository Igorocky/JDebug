package org.igye.jdebug.messages.constants;

public enum ModifierKind {
    COUNT(1),
    CONDITIONAL(2),
    THREAD_ONLY(3),
    CLASS_ONLY(4),
    CLASS_MATCH(5),
    CLASS_EXCLUDE(6),
    LOCATION_ONLY(7),
    EXCEPTION_ONLY(8),
    FIELD_ONLY(9),
    STEP(10),
    INSTANCE_ONLY(11),
    ;
    private int code;

    ModifierKind(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
