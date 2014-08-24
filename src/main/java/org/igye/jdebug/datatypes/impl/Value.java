package org.igye.jdebug.datatypes.impl;

import org.igye.jdebug.messages.constants.Tag;

public class Value {
    private Tag tag;
    private ObjectId objectId;
    private byte aByte;
    private char aChar;
    private float aFloat;
    private double aDouble;
    private int anInt;
    private long aLong;
    private short aShort;
    private boolean aBoolean;
    private boolean initialized;

    public Value(Tag tag) {
        this.tag = tag;
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public ObjectId getObjectId() {
        if (!initialized) {
            new IllegalStateException("!initialized");
        }
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
        this.objectId = objectId;
        initialized = true;
    }

    public byte getByte() {
        if (!initialized) {
            new IllegalStateException("!initialized");
        }
        return aByte;
    }

    public void setByte(byte aByte) {
        this.aByte = aByte;
        initialized = true;
    }

    public char getChar() {
        if (!initialized) {
            new IllegalStateException("!initialized");
        }
        return aChar;
    }

    public void setChar(char aChar) {
        this.aChar = aChar;
        initialized = true;
    }

    public float getFloat() {
        if (!initialized) {
            new IllegalStateException("!initialized");
        }
        return aFloat;
    }

    public void setFloat(float aFloat) {
        this.aFloat = aFloat;
        initialized = true;
    }

    public double getDouble() {
        if (!initialized) {
            new IllegalStateException("!initialized");
        }
        return aDouble;
    }

    public void setDouble(double aDouble) {
        this.aDouble = aDouble;
        initialized = true;
    }

    public int getInt() {
        if (!initialized) {
            new IllegalStateException("!initialized");
        }
        return anInt;
    }

    public void setInt(int anInt) {
        this.anInt = anInt;
        initialized = true;
    }

    public long getLong() {
        if (!initialized) {
            new IllegalStateException("!initialized");
        }
        return aLong;
    }

    public void setLong(long aLong) {
        this.aLong = aLong;
        initialized = true;
    }

    public short getShort() {
        if (!initialized) {
            new IllegalStateException("!initialized");
        }
        return aShort;
    }

    public void setShort(short aShort) {
        this.aShort = aShort;
        initialized = true;
    }

    public boolean getBoolean() {
        if (!initialized) {
            new IllegalStateException("!initialized");
        }
        return aBoolean;
    }

    public void setBoolean(boolean aBoolean) {
        this.aBoolean = aBoolean;
        initialized = true;
    }
}
