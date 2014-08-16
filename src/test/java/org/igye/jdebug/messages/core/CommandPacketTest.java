package org.igye.jdebug.messages.core;

import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.messages.JdwpMessage;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommandPacketTest {
    @Test
    public void toByteArray() {
        /*
        Command Packet

        Header
            length (4 bytes)
            id (4 bytes)
            flags (1 byte)
            command set (1 byte)
            command (1 byte)
        data (Variable)
         */
        long id = 45;
        int commandSet = 9;
        int command = 23;
        byte[] data = new byte[] {1,2,3,4,5};
        CommandPacket commandPacket = new CommandPacket(id, commandSet, command, data);
        byte[] res = commandPacket.toByteArray();

        assertEquals(16, res.length);
        assertEquals(res[0], (byte) 0);
        assertEquals(res[1], (byte) 0);
        assertEquals(res[2], (byte) 0);
        assertEquals(res[3], (byte) 16);
        assertEquals(res[4], (byte) 0);
        assertEquals(res[5], (byte) 0);
        assertEquals(res[6], (byte) 0);
        assertEquals(res[7], (byte) 45);
        assertEquals(res[8], (byte) JdwpMessage.COMMAND_FLAG);
        assertEquals(res[9], (byte) commandSet);
        assertEquals(res[10], (byte) command);
        assertEquals(res[11], data[0]);
        assertEquals(res[12], data[1]);
        assertEquals(res[13], data[2]);
        assertEquals(res[14], data[3]);
        assertEquals(res[15], data[4]);

        commandPacket = new CommandPacket(id, commandSet, command, null);
        res = commandPacket.toByteArray();

        assertEquals(11, res.length);
        assertEquals(res[0], (byte) 0);
        assertEquals(res[1], (byte) 0);
        assertEquals(res[2], (byte) 0);
        assertEquals(res[3], (byte) 11);
        assertEquals(res[4], (byte) 0);
        assertEquals(res[5], (byte) 0);
        assertEquals(res[6], (byte) 0);
        assertEquals(res[7], (byte) 45);
        assertEquals(res[8], (byte) JdwpMessage.COMMAND_FLAG);
        assertEquals(res[9], (byte) commandSet);
        assertEquals(res[10], (byte) command);
    }
}
