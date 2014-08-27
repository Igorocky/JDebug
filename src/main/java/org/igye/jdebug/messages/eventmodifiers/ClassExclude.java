package org.igye.jdebug.messages.eventmodifiers;

import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.impl.JdwpString;
import org.igye.jdebug.messages.EventModifier;
import org.igye.jdebug.messages.constants.ModifierKind;

public class ClassExclude implements EventModifier {
    private byte modKind = (byte) ModifierKind.CLASS_EXCLUDE.getCode();
    private JdwpString classPattern;

    public ClassExclude(String classPattern) {
        this.classPattern = new JdwpString(classPattern);
    }

    @Override
    public byte[] toByteArray() {
        return ByteArrays.concat(
                new byte[]{modKind},
                classPattern.toByteArray()
        );
    }
}
