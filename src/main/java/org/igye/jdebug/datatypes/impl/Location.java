package org.igye.jdebug.datatypes.impl;

import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.JdwpDataType;

public class Location implements JdwpDataType {
    private byte tagType;
    private ObjectId classID;
    private MethodId methodID;
    private byte[] index;

    public Location(byte tagType, ObjectId classID, MethodId methodID, byte[] index) {
        this.tagType = tagType;
        this.classID = classID;
        this.methodID = methodID;
        if (index.length != 8) {
            throw new IllegalArgumentException("index.length != 8");
        }
        this.index = index;
    }

    public byte getTagType() {
        return tagType;
    }

    public ObjectId getClassID() {
        return classID;
    }

    public MethodId getMethodID() {
        return methodID;
    }

    public byte[] getIndex() {
        return index;
    }

    @Override
    public byte[] toByteArray() {
        return ByteArrays.concat(
                new byte[]{tagType},
                classID.toByteArray(),
                methodID.toByteArray(),
                index
        );
    }
}
