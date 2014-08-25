package org.igye.jdebug.datatypes.impl;

import org.igye.jdebug.datatypes.JdwpDataType;
import org.igye.jdebug.messages.core.IdSizes;

public class ObjectId implements JdwpDataType {
    private byte[] id;

    public ObjectId(byte[] id) {
        if (id.length != IdSizes.getObjectIDSize()) {
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