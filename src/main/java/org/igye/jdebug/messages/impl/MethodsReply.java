package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.JdwpError;
import org.igye.jdebug.messages.core.ReplyPacket;

public class MethodsReply extends JdwpMessage {
    private int errorCode;
    private MethodInfo[] methods;

    public MethodsReply(ReplyPacket replyPacket) {
        setCommandOrReplyPacket(replyPacket);
        errorCode = replyPacket.getErrorCode();
        if (errorCode == JdwpError.NONE.getCode()) {
            methods = new MethodInfo[readInt()];
            for (int i = 0; i < methods.length; i++) {
                methods[i] = readMethodInfo();
            }
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public MethodInfo[] getMethods() {
        return methods;
    }
}
