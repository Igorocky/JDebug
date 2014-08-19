package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.JdwpError;
import org.igye.jdebug.messages.core.ReplyPacket;

public class IdSizesReply extends JdwpMessage {
    private int errorCode;
    private int fieldIDSize;
    private int methodIDSize;
    private int objectIDSize;
    private int referenceTypeIDSize;
    private int frameIDSize;

    public IdSizesReply(ReplyPacket replyPacket) {
        setCommandOrReplyPacket(replyPacket);
        errorCode = replyPacket.getErrorCode();
        if (errorCode == JdwpError.NONE.getCode()) {
            fieldIDSize = readInt();
            methodIDSize = readInt();
            objectIDSize = readInt();
            referenceTypeIDSize = readInt();
            frameIDSize = readInt();
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public int getFieldIDSize() {
        return fieldIDSize;
    }

    public int getMethodIDSize() {
        return methodIDSize;
    }

    public int getObjectIDSize() {
        return objectIDSize;
    }

    public int getReferenceTypeIDSize() {
        return referenceTypeIDSize;
    }

    public int getFrameIDSize() {
        return frameIDSize;
    }
}
