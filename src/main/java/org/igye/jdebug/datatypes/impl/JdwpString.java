package org.igye.jdebug.datatypes.impl;

import org.apache.commons.codec.binary.Hex;
import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.JdwpDataType;

import java.io.IOException;
import java.io.InputStream;

public final class JdwpString implements JdwpDataType {
    private String value;

    public JdwpString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public byte[] toByteArray() {
        return ByteArrays.concat(
                ByteArrays.intToBigEndianByteArray(value.length()),
                value.getBytes()
        );
    }

    @Override
    public String toString() {
        return value;
    }
}
