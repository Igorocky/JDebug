package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.Command;
import org.igye.jdebug.messages.constants.CommandSet;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.MessageIdGenerator;

public class ClearAllBreakpointsCommand extends JdwpMessage {
    public ClearAllBreakpointsCommand() {
        setCommandOrReplyPacket(new CommandPacket(
                MessageIdGenerator.getInstance().generateId(),
                CommandSet.EVENT_REQUEST.getCode(),
                Command.CLEAR_ALL_BREAKPOINTS.getCode(),
                null
        ));
    }
}
