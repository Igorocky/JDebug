package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.JdwpError;
import org.igye.jdebug.messages.core.ReplyPacket;

public class ResumeThreadReply extends JdwpMessage {
    private int errorCode;

    public ResumeThreadReply(ReplyPacket replyPacket) {
        setCommandOrReplyPacket(replyPacket);
        errorCode = replyPacket.getErrorCode();
    }

    public int getErrorCode() {
        return errorCode;
    }
}
