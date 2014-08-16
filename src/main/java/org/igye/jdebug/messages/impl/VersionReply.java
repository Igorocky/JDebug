package org.igye.jdebug.messages.impl;

import org.igye.jdebug.ArrayOffset;
import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.JdwpDataTypeReader;
import org.igye.jdebug.datatypes.impl.JdwpString;
import org.igye.jdebug.messages.HasId;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.MessageIdGenerator;
import org.igye.jdebug.messages.core.ReplyPacket;

public class VersionReply extends JdwpMessage {
    private int errorCode;
    private String description;
    private int jdwpMajor;
    private int jdwpMinor;
    private String vmVersion;
    private String vmName;

    public VersionReply(ReplyPacket replyPacket) {
        setCommandOrReplyPacket(replyPacket);
        errorCode = replyPacket.getErrorCode();
        description = readString();
        jdwpMajor = readInt();
        jdwpMinor = readInt();
        vmVersion = readString();
        vmName = readString();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getDescription() {
        return description;
    }

    public int getJdwpMajor() {
        return jdwpMajor;
    }

    public int getJdwpMinor() {
        return jdwpMinor;
    }

    public String getVmVersion() {
        return vmVersion;
    }

    public String getVmName() {
        return vmName;
    }
}
