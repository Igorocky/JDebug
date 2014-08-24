package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.JdwpError;
import org.igye.jdebug.messages.core.ReplyPacket;

public class SetReply extends JdwpMessage {
    private int errorCode;
    private int requestId;

    public SetReply(ReplyPacket replyPacket) {
        setCommandOrReplyPacket(replyPacket);
        errorCode = replyPacket.getErrorCode();
        if (errorCode == JdwpError.NONE.getCode()) {
            requestId = readInt();
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public int getRequestId() {
        return requestId;
    }
}
