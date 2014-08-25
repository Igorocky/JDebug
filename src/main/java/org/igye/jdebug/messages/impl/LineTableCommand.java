package org.igye.jdebug.messages.impl;

import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.impl.MethodId;
import org.igye.jdebug.datatypes.impl.ObjectId;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.Command;
import org.igye.jdebug.messages.constants.CommandSet;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.MessageIdGenerator;

public class LineTableCommand extends JdwpMessage {
    public LineTableCommand(ObjectId referenceTypeID, MethodId methodId) {
        setCommandOrReplyPacket(new CommandPacket(
                MessageIdGenerator.getInstance().generateId(),
                CommandSet.METHOD.getCode(),
                Command.LINE_TABLE.getCode(),
                ByteArrays.concat(
                        referenceTypeID.toByteArray(),
                        methodId.toByteArray()
                )
        ));
    }
}
