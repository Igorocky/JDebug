package org.igye.jdebug.messages.impl;

import org.igye.jdebug.datatypes.JdwpDataType;
import org.igye.jdebug.datatypes.impl.ObjectId;

public class ClassInfo {
    private byte refTypeTag;
    private ObjectId typeId;
    private String signature;
    private int status;

    public ClassInfo(byte refTypeTag, ObjectId typeId, String signature, int status) {
        this.refTypeTag = refTypeTag;
        this.typeId = typeId;
        this.signature = signature;
        this.status = status;
    }

    public byte getRefTypeTag() {
        return refTypeTag;
    }

    public ObjectId getTypeId() {
        return typeId;
    }

    public String getSignature() {
        return signature;
    }

    public int getStatus() {
        return status;
    }
}
