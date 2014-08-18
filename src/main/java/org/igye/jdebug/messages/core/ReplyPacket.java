package org.igye.jdebug.messages.core;

import org.apache.commons.codec.binary.Hex;
import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.JdwpDataType;
import org.igye.jdebug.messages.HasId;
import org.igye.jdebug.messages.JdwpMessage;

public class ReplyPacket extends JdwpMessage implements HasId {
    private long id;
    private int flags;
    private int errorCode;
    private byte[] data;

    public ReplyPacket(long id, int errorCode, byte[] data) {
        this.id = id;
        this.flags = 0x80;
        this.errorCode = errorCode;
        this.data = data;
        setCommandOrReplyPacket(this);
    }

    @Override
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

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Reply{id: ").append(id)
                .append("; flags: ").append(flags)
                .append("; errorCode: ").append(errorCode)
                .append("; data: ").append(Hex.encodeHexString(data)).append("}")
                .toString();
    }
}
