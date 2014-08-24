package org.igye.jdebug.messages.constants;

public enum Tag {
    ARRAY(91, "'[' - an array object (objectID size)."),
    BYTE(66, "'B' - a byte value (1 byte)."),
    CHAR(67, "'C' - a character value (2 bytes)."),
    OBJECT(76, "'L' - an object (objectID size)."),
    FLOAT(70, "'F' - a float value (4 bytes)."),
    DOUBLE(68, "'D' - a double value (8 bytes)."),
    INT(73, "'I' - an int value (4 bytes)."),
    LONG(74, "'J' - a long value (8 bytes)."),
    SHORT(83, "'S' - a short value (2 bytes)."),
    VOID(86, "'V' - a void value (no bytes)."),
    BOOLEAN(90, "'Z' - a boolean value (1 byte)."),
    STRING(115, "'s' - a String object (objectID size)."),
    THREAD(116, "'t' - a Thread object (objectID size)."),
    THREAD_GROUP(103, "'g' - a ThreadGroup object (objectID size)."),
    CLASS_LOADER(108, "'l' - a ClassLoader object (objectID size)."),
    CLASS_OBJECT(99, "'c' - a class object object (objectID size)."),
    ;
    private int code;
    private String description;

    Tag(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public static Tag getTagByCode(int code) {
        for (Tag tag : values()) {
            if (tag.getCode() == code) {
                return tag;
            }
        }
        return null;
    }
}
