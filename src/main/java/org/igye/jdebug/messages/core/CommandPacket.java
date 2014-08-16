package org.igye.jdebug.messages.core;

import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.JdwpDataType;

public class CommandPacket implements JdwpDataType {
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
    }

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
}
