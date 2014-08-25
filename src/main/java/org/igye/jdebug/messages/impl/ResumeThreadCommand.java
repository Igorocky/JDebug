package org.igye.jdebug.messages.impl;

import org.igye.jdebug.datatypes.impl.ObjectId;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.Command;
import org.igye.jdebug.messages.constants.CommandSet;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.MessageIdGenerator;

public class ResumeThreadCommand extends JdwpMessage {
    public ResumeThreadCommand(ObjectId threadID) {
        setCommandOrReplyPacket(new CommandPacket(
                MessageIdGenerator.getInstance().generateId(),
                CommandSet.THREAD_REFERENCE.getCode(),
                Command.RESUME_THREAD.getCode(),
                threadID.toByteArray()
        ));
    }
}
