package org.igye.jdebug.messages.constants;

public enum TypeTag {
    CLASS(1),
    INTERFACE(2),
    ARRAY(3),
    ;
    private int code;

    TypeTag(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
