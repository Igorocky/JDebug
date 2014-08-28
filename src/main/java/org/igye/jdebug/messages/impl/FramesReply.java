package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.JdwpError;
import org.igye.jdebug.messages.core.ReplyPacket;

public class FramesReply extends JdwpMessage {
    private int errorCode;
    private FrameInfo[] frames;

    public FramesReply(ReplyPacket replyPacket) {
        setCommandOrReplyPacket(replyPacket);
        errorCode = replyPacket.getErrorCode();
        if (errorCode == JdwpError.NONE.getCode()) {
            frames = new FrameInfo[readInt()];
            for (int i = 0; i < frames.length; i++) {
                frames[i] = readFrameInfo();
            }
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public FrameInfo[] getFrames() {
        return frames;
    }
}
