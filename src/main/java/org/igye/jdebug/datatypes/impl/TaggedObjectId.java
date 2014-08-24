package org.igye.jdebug.datatypes.impl;

import org.igye.jdebug.datatypes.JdwpDataType;
import org.igye.jdebug.messages.core.IdSizes;

public class TaggedObjectId implements JdwpDataType {
    private byte[] id;
    private byte tag;

    public TaggedObjectId(byte[] id, byte tag) {
        if (id.length != IdSizes.getObjectIDSize()) {
            throw new IllegalArgumentException("id.length != IdSizes.getObjectIDSize()");
        }
        this.id = id;
        this.tag = tag;
    }

    public byte[] getId() {
        return id;
    }

    public byte getTag() {
        return tag;
    }

    @Override
    public byte[] toByteArray() {
        return id;
    }
}
