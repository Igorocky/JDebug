package org.igye.jdebug.messages;

import org.igye.jdebug.ArrayOffset;
import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.JdwpDataTypeReader;
import org.igye.jdebug.exceptions.JDebugRuntimeException;
import org.igye.jdebug.messages.constants.JdwpError;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.ReplyPacket;
import org.igye.jdebug.messages.impl.ClassInfo;
import org.igye.jdebug.messages.impl.Event;
import org.igye.jdebug.messages.impl.FrameInfo;
import org.igye.jdebug.messages.impl.MethodInfo;

public abstract class JdwpMessage implements HasId, RepresentableAsArrayOfBytes {
    public static final int COMMAND_FLAG = 0;
    public static final int REPLY_FLAG = 0x80;

    private JdwpMessage commandOrReplyPacket;
    private byte[] data;
    private ArrayOffset offset;

    protected JdwpMessage getCommandOrReplyPacket() {
        return commandOrReplyPacket;
    }

    protected void setCommandOrReplyPacket(JdwpMessage commandOrReplyPacket) {
        setCommandOrReplyPacket(commandOrReplyPacket, true);
    }

    protected void setCommandOrReplyPacket(JdwpMessage commandOrReplyPacket,
                                           boolean throwExceptionOnNonZeroErrorCode) {
        if (commandOrReplyPacket.getClass() == CommandPacket.class) {
            this.commandOrReplyPacket = commandOrReplyPacket;
            data = ((CommandPacket)commandOrReplyPacket).getData();
        } else if (commandOrReplyPacket.getClass() == ReplyPacket.class) {
            if (
                    throwExceptionOnNonZeroErrorCode
                    && ((ReplyPacket)commandOrReplyPacket).getErrorCode() != JdwpError.NONE.getCode()
               ) {
                throw new JDebugRuntimeException("ReplyPacket.getErrorCode() != NONE, " + commandOrReplyPacket);
            }
            this.commandOrReplyPacket = commandOrReplyPacket;
            data = ((ReplyPacket)commandOrReplyPacket).getData();
        } else {
            throw new IllegalArgumentException("commandOrReplyPacket must be of type " +
                    "CommandPacket or ReplyPacket.");
        }
    }

    @Override
    public byte[] toByteArray() {
        return getCommandOrReplyPacket().toByteArray();
    }

    @Override
    public long getId() {
        return getCommandOrReplyPacket().getId();
    }

    public int getFlags() {
        return getCommandOrReplyPacket().getFlags();
    }

    protected String readString() {
        /*if (data == null) {
            return null;
        }*/
        if (offset == null) {
            offset = new ArrayOffset();
        }
        return JdwpDataTypeReader.readString(data, offset);
    }

    protected int readInt() {
        /*if (data == null) {
            return 0;
        }*/
        if (offset == null) {
            offset = new ArrayOffset();
        }
        return JdwpDataTypeReader.readInt(data, offset);
    }

    protected byte readByte() {
        if (offset == null) {
            offset = new ArrayOffset();
        }
        return JdwpDataTypeReader.readByte(data, offset);
    }

    protected long readLong() {
        if (offset == null) {
            offset = new ArrayOffset();
        }
        return JdwpDataTypeReader.readLong(data, offset);
    }

    protected Event readEvent() {
        if (offset == null) {
            offset = new ArrayOffset();
        }
        return JdwpDataTypeReader.readEvent(data, offset);
    }

    protected ClassInfo readClassInfo() {
        if (offset == null) {
            offset = new ArrayOffset();
        }
        return JdwpDataTypeReader.readClassInfo(data, offset);
    }

    protected MethodInfo readMethodInfo() {
        if (offset == null) {
            offset = new ArrayOffset();
        }
        return JdwpDataTypeReader.readMethodInfo(data, offset);
    }

    protected FrameInfo readFrameInfo() {
        if (offset == null) {
            offset = new ArrayOffset();
        }
        return JdwpDataTypeReader.readFrameInfo(data, offset);
    }

    @Override
    public String toString() {
        return getCommandOrReplyPacket().toString();
    }
}
