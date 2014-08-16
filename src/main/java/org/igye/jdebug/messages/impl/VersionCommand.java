package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.MessageIdGenerator;

public class VersionCommand extends JdwpMessage {
    public VersionCommand() {
        setCommandOrReplyPacket(new CommandPacket(
                        MessageIdGenerator.getInstance().generateId(), 1, 1, null
        ));
    }
}
