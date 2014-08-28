package org.igye.jdebug.messages.constants;

public enum Command {
    //VIRTUAL_MACHINE(1),
    VERSION(1),
    ALL_CLASSES(3),
    ID_SIZES(7),
    RESUME(9),

    //REFERENCE_TYPE(2),
    SIGNATURE(1),
    METHODS(5),

    //CLASS_TYPE(3),

    //ARRAY_TYPE(4),

    //INTERFACE_TYPE(5),

    //METHOD(6),
    LINE_TABLE(1),

    //FIELD(8),

    //OBJECT_REFERENCE(9),

    //STRING_REFERENCE(10),

    //THREAD_REFERENCE(11),
    THREAD_NAME(1),
    RESUME_THREAD(3),
    FRAMES(6),

    //THREAD_GROUP_REFERENCE(12),

    //ARRAY_REFERENCE(13),

    //CLASS_LOADER_REFERENCE(14),

    //EVENT_REQUEST(15),
    SET(1),
    CLEAR_ALL_BREAKPOINTS(3),

    //STACK_FRAME(16),

    //CLASS_OBJECT_REFERENCE(17),

    //EVENT(64)
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
