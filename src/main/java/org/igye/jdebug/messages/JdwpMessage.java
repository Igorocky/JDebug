package org.igye.jdebug.messages;

import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.ReplyPacket;

public abstract class JdwpMessage {
    public static final int COMMAND_FLAG = 0;
    public static final int REPLY_FLAG = 0x80;

    private JdwpMessage commandOrReplyPacket;

    protected JdwpMessage getCommandOrReplyPacket() {
        return commandOrReplyPacket;
    }

    protected void setCommandOrReplyPacket(JdwpMessage commandOrReplyPacket) {
        if (commandOrReplyPacket.getClass() != CommandPacket.class
                && commandOrReplyPacket.getClass() != ReplyPacket.class) {
            throw new IllegalArgumentException("commandOrReplyPacket must be of type " +
                    "CommandPacket or ReplyPacket.");
        }
        this.commandOrReplyPacket = commandOrReplyPacket;
    }

    public byte[] toByteArray() {
        return getCommandOrReplyPacket().toByteArray();
    }
}
