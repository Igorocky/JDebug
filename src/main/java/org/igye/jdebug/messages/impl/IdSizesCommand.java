package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.Command;
import org.igye.jdebug.messages.constants.CommandSet;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.MessageIdGenerator;

public class IdSizesCommand extends JdwpMessage {
    public IdSizesCommand() {
        setCommandOrReplyPacket(new CommandPacket(
                MessageIdGenerator.getInstance().generateId(),
                CommandSet.VIRTUAL_MACHINE.getCode(),
                Command.ID_SIZES.getCode(),
                null
        ));
    }
}
