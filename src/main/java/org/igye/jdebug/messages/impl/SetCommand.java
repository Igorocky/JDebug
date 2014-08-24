package org.igye.jdebug.messages.impl;

import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.messages.EventModifier;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.Command;
import org.igye.jdebug.messages.constants.CommandSet;
import org.igye.jdebug.messages.constants.EventKind;
import org.igye.jdebug.messages.constants.SuspendPolicy;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.MessageIdGenerator;

public class SetCommand extends JdwpMessage {
    public SetCommand(EventKind eventKind, SuspendPolicy suspendPolicy, EventModifier[] modifiers) {
        setCommandOrReplyPacket(
                new CommandPacket(
                        MessageIdGenerator.getInstance().generateId(),
                        CommandSet.EVENT_REQUEST.getCode(),
                        Command.SET.getCode(),
                        ByteArrays.concat(
                                new byte[]{(byte) eventKind.getCode()},
                                new byte[]{(byte) suspendPolicy.getCode()},
                                ByteArrays.intToBigEndianByteArray(
                                        modifiers == null ? 0 : modifiers.length
                                ),
                                ByteArrays.toByteArray(modifiers)
                        )
                )
        );
    }
}
