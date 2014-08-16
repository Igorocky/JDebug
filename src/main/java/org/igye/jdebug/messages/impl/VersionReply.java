package org.igye.jdebug.messages.impl;

import org.igye.jdebug.datatypes.impl.JdwpString;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.MessageIdGenerator;
import org.igye.jdebug.messages.core.ReplyPacket;

public class VersionReply extends JdwpMessage {
    private String description;
    private int jdwpMajor;
    private int jdwpMinor;
    private String vmVersion;
    private String vmName;

    public VersionReply(ReplyPacket replyPacket) {
        setCommandOrReplyPacket(replyPacket);

    }
}
