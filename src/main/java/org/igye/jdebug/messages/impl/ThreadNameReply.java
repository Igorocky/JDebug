package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.JdwpError;
import org.igye.jdebug.messages.core.ReplyPacket;

public class ThreadNameReply extends JdwpMessage {
    private int errorCode;
    private String name;

    public ThreadNameReply(ReplyPacket replyPacket) {
        setCommandOrReplyPacket(replyPacket);
        errorCode = replyPacket.getErrorCode();
        if (errorCode == JdwpError.NONE.getCode()) {
            name = readString();
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getName() {
        return name;
    }
}
