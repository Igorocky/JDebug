package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.JdwpError;
import org.igye.jdebug.messages.core.ReplyPacket;

public class SignatureReply extends JdwpMessage {
    private int errorCode;
    private String signature;

    public SignatureReply(ReplyPacket replyPacket) {
        setCommandOrReplyPacket(replyPacket);
        errorCode = replyPacket.getErrorCode();
        if (errorCode == JdwpError.NONE.getCode()) {
            signature = readString();
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getSignature() {
        return signature;
    }
}
