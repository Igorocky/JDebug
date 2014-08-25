package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.JdwpError;
import org.igye.jdebug.messages.core.ReplyPacket;

public class ClearAllBreakpointsReply extends JdwpMessage {
    private int errorCode;

    public ClearAllBreakpointsReply(ReplyPacket replyPacket) {
        setCommandOrReplyPacket(replyPacket);
        errorCode = replyPacket.getErrorCode();
    }

    public int getErrorCode() {
        return errorCode;
    }
}
