package org.igye.jdebug.datatypes;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.ReplyPacket;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JdwpDataTypeReaderTest {
    @Test
    public void readString() {
        byte[] in = new byte[] {0,0,0,7,'s','u','c','c','e','s','s'};
        Assert.assertEquals("success", JdwpDataTypeReader.readString(in, 0));
    }

    @Test
    public void readMessageCommand() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(
                new byte[] {
                        0,0,0,16,
                        0,0,0,10,
                        JdwpMessage.COMMAND_FLAG,
                        45,
                        36,
                        1,2,3,4,5
                }
        );
        JdwpMessage msg = JdwpDataTypeReader.readMessage(in);
        assertTrue(msg.getClass() == CommandPacket.class);
        CommandPacket commandPacket = (CommandPacket) msg;
        assertEquals(10, commandPacket.getId());
        assertEquals(JdwpMessage.COMMAND_FLAG, commandPacket.getFlags());
        assertEquals(45, commandPacket.getCommandSet());
        assertEquals(36, commandPacket.getCommand());
        assertArrayEquals(new byte[] {1,2,3,4,5}, commandPacket.getData());
    }

    @Test
    public void readMessageReply() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(
                new byte[] {
                        0,0,0,16,
                        0,0,0,10,
                        (byte) JdwpMessage.REPLY_FLAG,
                        1,1,
                        1,2,3,4,5
                }
        );
        JdwpMessage msg = JdwpDataTypeReader.readMessage(in);
        assertTrue(msg.getClass() == ReplyPacket.class);
        ReplyPacket replyPacket = (ReplyPacket) msg;
        assertEquals(10, replyPacket.getId());
        assertEquals(JdwpMessage.REPLY_FLAG, replyPacket.getFlags());
        assertEquals(257, replyPacket.getErrorCode());
        assertArrayEquals(new byte[] {1,2,3,4,5}, replyPacket.getData());
    }
}
