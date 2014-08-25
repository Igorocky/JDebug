package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.JdwpError;
import org.igye.jdebug.messages.core.ReplyPacket;

public class LineTableReply extends JdwpMessage {
    private int errorCode;
    private long start;
    private long end;
    private LineTableEntry[] lineTable;

    public LineTableReply(ReplyPacket replyPacket) {
        setCommandOrReplyPacket(replyPacket);
        errorCode = replyPacket.getErrorCode();
        if (errorCode == JdwpError.NONE.getCode()) {
            start = readLong();
            end = readLong();
            lineTable = new LineTableEntry[readInt()];
            for (int i = 0; i < lineTable.length; i++) {
                lineTable[i] = new LineTableEntry(readLong(), readInt());
            }
        }
    }

    public int getErrorCode() {
        return errorCode;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public LineTableEntry[] getLineTable() {
        return lineTable;
    }
}
