package org.igye.jdebug.messages.constants;

public enum CommandSet {
    VIRTUAL_MACHINE(1),
    REFERENCE_TYPE(2),
    CLASS_TYPE(3),
    ARRAY_TYPE(4),
    INTERFACE_TYPE(5),
    METHOD(6),
    FIELD(8),
    OBJECT_REFERENCE(9),
    STRING_REFERENCE(10),
    THREAD_REFERENCE(11),
    THREAD_GROUP_REFERENCE(12),
    ARRAY_REFERENCE(13),
    CLASS_LOADER_REFERENCE(14),
    EVENT_REQUEST(15),
    STACK_FRAME(16),
    CLASS_OBJECT_REFERENCE(17),
    EVENT(64)
    ;
    private int code;

    CommandSet(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
