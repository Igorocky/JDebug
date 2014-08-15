package org.igye.jdebug.messages;

public class CommandPacket {
    private long id;
    private int flags;
    private int commandSet;
    private int command;
    private byte[] data;

    public CommandPacket(long id, int flags, int commandSet, int command, byte[] data) {
        this.id = id;
        this.flags = flags;
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
}
