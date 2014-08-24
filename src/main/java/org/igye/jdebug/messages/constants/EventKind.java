package org.igye.jdebug.messages.constants;

public enum EventKind {
    VM_DISCONNECTED(100),
    VM_START(90),
    THREAD_DEATH(7),
    SINGLE_STEP(1),
    BREAKPOINT(2),
    FRAME_POP(3),
    EXCEPTION(4),
    USER_DEFINED(5),
    THREAD_START(6),
    THREAD_END(7),
    CLASS_PREPARE(8),
    CLASS_UNLOAD(9),
    CLASS_LOAD(10),
    FIELD_ACCESS(20),
    FIELD_MODIFICATION(21),
    EXCEPTION_CATCH(30),
    METHOD_ENTRY(40),
    METHOD_EXIT(41),
    VM_INIT(90),
    VM_DEATH(99),
    ;
    private int code;

    EventKind(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static EventKind getEventKindByCode(int code) {
        for (EventKind eventKind : values()) {
            if (eventKind.getCode() == code) {
                return eventKind;
            }
        }
        return null;
    }
}
