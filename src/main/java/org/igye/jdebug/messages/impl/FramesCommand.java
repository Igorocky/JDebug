package org.igye.jdebug.messages.impl;

import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.impl.ObjectId;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.Command;
import org.igye.jdebug.messages.constants.CommandSet;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.MessageIdGenerator;

public class FramesCommand extends JdwpMessage {
    public FramesCommand(ObjectId threadId, int startFrame, int length) {
        setCommandOrReplyPacket(new CommandPacket(
                MessageIdGenerator.getInstance().generateId(),
                CommandSet.THREAD_REFERENCE.getCode(),
                Command.FRAMES.getCode(),
                ByteArrays.concat(
                        threadId.toByteArray(),
                        ByteArrays.intToBigEndianByteArray(startFrame),
                        ByteArrays.intToBigEndianByteArray(length)
                )
        ));
    }
}
