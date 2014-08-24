package org.igye.jdebug.datatypes.impl;

import org.igye.jdebug.datatypes.JdwpDataType;
import org.igye.jdebug.messages.core.IdSizes;

public class FieldId implements JdwpDataType {
    private byte[] id;

    public FieldId(byte[] id) {
        if (id.length != IdSizes.getFieldIDSize()) {
            throw new IllegalArgumentException("id.length != IdSizes.getObjectIDSize()");
        }
        this.id = id;
    }

    public byte[] getId() {
        return id;
    }

    @Override
    public byte[] toByteArray() {
        return id;
    }
}
