package org.igye.jdebug.datatypes.impl;

import org.apache.commons.codec.binary.Hex;
import org.igye.jdebug.datatypes.JdwpDataType;
import org.igye.jdebug.messages.core.IdSizes;

import java.util.Arrays;

public class FieldId implements JdwpDataType {
    private byte[] id;
    private String toStringResult;
    private boolean hasHashCodeResult = false;
    private int hashCodeResult;

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

    @Override
    public String toString() {
        if (toStringResult == null) {
            toStringResult = Hex.encodeHexString(id);
        }
        return toStringResult;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ObjectId)) {
            return false;
        }
        FieldId other = (FieldId) obj;
        if (this.id.length != other.id.length) {
            return false;
        }
        for (int i = 0; i < id.length; i++) {
            if (this.id[i] != other.id[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (!hasHashCodeResult) {
            hashCodeResult = Arrays.hashCode(id);
            hasHashCodeResult = true;
        }
        return hashCodeResult;
    }
}
