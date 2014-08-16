package org.igye.jdebug.messages.core;

import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.JdwpDataType;
import org.igye.jdebug.messages.JdwpMessage;

public class ReplyPacket implements JdwpMessage {
    private long id;
    private int flags;
    private int errorCode;
    private byte[] data;

    public ReplyPacket(long id, int errorCode, byte[] data) {
        this.id = id;
        this.flags = 0x80;
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

    @Override
    public byte[] toByteArray() {
        return ByteArrays.concat(
                ByteArrays.intToBigEndianByteArray(11 + (data != null ? data.length : 0)),
                ByteArrays.intToBigEndianByteArray((int) id),
                new byte[] {(byte) flags},
                new byte[] {(byte) (errorCode >>> 8), (byte) errorCode},
                data
        );
    }
}
