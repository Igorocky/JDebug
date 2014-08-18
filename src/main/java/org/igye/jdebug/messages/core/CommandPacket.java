package org.igye.jdebug.messages.core;

import org.apache.commons.codec.binary.Hex;
import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.JdwpDataType;
import org.igye.jdebug.messages.HasId;
import org.igye.jdebug.messages.JdwpMessage;

public class CommandPacket extends JdwpMessage implements HasId {
    private long id;
    private int flags;
    private int commandSet;
    private int command;
    //can be null
    private byte[] data;

    public CommandPacket(long id, int commandSet, int command, byte[] data) {
        this.id = id;
        this.flags = 0;
        this.commandSet = commandSet;
        this.command = command;
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

    public int getCommandSet() {
        return commandSet;
    }

    public int getCommand() {
        return command;
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
                new byte[] {(byte) commandSet},
                new byte[] {(byte) command},
                data
        );
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("Reply{id: ").append(id)
                .append("; flags: ").append(flags)
                .append("; commandSet: ").append(commandSet)
                .append("; command: ").append(command)
                .append("; data: ").append(Hex.encodeHexString(data)).append("}")
                .toString();
    }
}
