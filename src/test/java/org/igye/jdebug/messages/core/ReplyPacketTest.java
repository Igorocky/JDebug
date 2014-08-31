package org.igye.jdebug.messages.core;

import org.igye.jdebug.messages.JdwpMessage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReplyPacketTest {
    @Test
    public void toByteArray() {
        /*
        Reply Packet

        Header
            length (4 bytes)
            id (4 bytes)
            flags (1 byte)
            error code (2 bytes)
        data (Variable)
         */
        long id = 45;
        int errorCode = 7;
        byte[] data = new byte[] {1,2,3,4,5};
        ReplyPacket replyPacket = new ReplyPacket(id, errorCode, data, false);
        byte[] res = replyPacket.toByteArray();

        assertEquals(16, res.length);
        assertEquals(res[0], (byte) 0);
        assertEquals(res[1], (byte) 0);
        assertEquals(res[2], (byte) 0);
        assertEquals(res[3], (byte) 16);
        assertEquals(res[4], (byte) 0);
        assertEquals(res[5], (byte) 0);
        assertEquals(res[6], (byte) 0);
        assertEquals(res[7], (byte) 45);
        assertEquals(res[8], (byte) JdwpMessage.REPLY_FLAG);
        assertEquals(res[9], (byte) 0);
        assertEquals(res[10], (byte) 7);
        assertEquals(res[11], data[0]);
        assertEquals(res[12], data[1]);
        assertEquals(res[13], data[2]);
        assertEquals(res[14], data[3]);
        assertEquals(res[15], data[4]);

        replyPacket = new ReplyPacket(id, errorCode, null, false);
        res = replyPacket.toByteArray();

        assertEquals(11, res.length);
        assertEquals(res[0], (byte) 0);
        assertEquals(res[1], (byte) 0);
        assertEquals(res[2], (byte) 0);
        assertEquals(res[3], (byte) 11);
        assertEquals(res[4], (byte) 0);
        assertEquals(res[5], (byte) 0);
        assertEquals(res[6], (byte) 0);
        assertEquals(res[7], (byte) 45);
        assertEquals(res[8], (byte) JdwpMessage.REPLY_FLAG);
        assertEquals(res[9], (byte) 0);
        assertEquals(res[10], (byte) 7);
    }
}
