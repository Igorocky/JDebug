package org.igye.jdebug.messages.impl;

import org.igye.jdebug.datatypes.impl.ObjectId;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.Command;
import org.igye.jdebug.messages.constants.CommandSet;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.MessageIdGenerator;

public class SignatureCommand extends JdwpMessage {
    public SignatureCommand(ObjectId referenceTypeID) {
        setCommandOrReplyPacket(new CommandPacket(
                MessageIdGenerator.getInstance().generateId(),
                CommandSet.REFERENCE_TYPE.getCode(),
                Command.SIGNATURE.getCode(),
                referenceTypeID.toByteArray()
        ));
    }
}
