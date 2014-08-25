package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.JdwpError;
import org.igye.jdebug.messages.core.ReplyPacket;

public class AllClassesReply extends JdwpMessage {
    private int errorCode;
    private ClassInfo[] classes;

    public AllClassesReply(ReplyPacket replyPacket) {
        setCommandOrReplyPacket(replyPacket);
        errorCode = replyPacket.getErrorCode();

        if (errorCode == JdwpError.NONE.getCode()) {
            classes = new ClassInfo[readInt()];
            for (int i = 0; i < classes.length; i++) {
                classes[i] = readClassInfo();
            }
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public ClassInfo[] getClasses() {
        return classes;
    }
}
