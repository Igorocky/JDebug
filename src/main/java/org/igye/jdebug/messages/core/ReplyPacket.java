package org.igye.jdebug.messages.core;

public class ReplyPacket {
    private long id;
    private int flags;
    private int errorCode;
    private byte[] data;

    public ReplyPacket(long id, int flags, int errorCode, byte[] data) {
        this.id = id;
        this.flags = flags;
        this.errorCode = errorCode;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public int getFlags() {
        return flags;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public byte[] getData() {
        return data;
    }
}
