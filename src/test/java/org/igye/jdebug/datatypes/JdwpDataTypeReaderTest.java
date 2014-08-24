package org.igye.jdebug.datatypes;

import org.igye.jdebug.ArrayOffset;
import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.impl.ObjectId;
import org.igye.jdebug.datatypes.impl.Value;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.Tag;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.IdSizes;
import org.igye.jdebug.messages.core.ReplyPacket;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

    @Test
    public void readValue() {
        IdSizes.setFieldIDSize(8);
        IdSizes.setReferenceTypeIDSize(8);
        IdSizes.setObjectIDSize(8);
        IdSizes.setMethodIDSize(8);
        IdSizes.setFrameIDSize(8);
        byte[] in = ByteArrays.concat(
                /*ARRAY	91	'[' - an array object (objectID size).  */
                new byte[]{(byte) Tag.ARRAY.getCode()},
                new ObjectId(new byte[]{1,2,3,4,5,6,7,8}).toByteArray(),
                /*BYTE	66	'B' - a byte value (1 byte).  */
                new byte[]{(byte) Tag.BYTE.getCode()},
                new byte[] {9},
                /*CHAR	67	'C' - a character value (2 bytes).  */
                new byte[]{(byte) Tag.CHAR.getCode()},
                new byte[] {10, 11},
                /*OBJECT	76	'L' - an object (objectID size).  */
                new byte[]{(byte) Tag.OBJECT.getCode()},
                new ObjectId(new byte[]{12,13,14,15,16,17,18,19}).toByteArray(),
                /*FLOAT	70	'F' - a float value (4 bytes).  */
                new byte[]{(byte) Tag.FLOAT.getCode()},
                new byte[] {20, 21, 22, 23},
                /*DOUBLE	68	'D' - a double value (8 bytes).  */
                new byte[]{(byte) Tag.DOUBLE.getCode()},
                new byte[] {24,25,26,27,28,29,30,31},
                /*INT	73	'I' - an int value (4 bytes).  */
                new byte[]{(byte) Tag.INT.getCode()},
                new byte[] {32,33,34,35},
                /*LONG	74	'J' - a long value (8 bytes).  */
                new byte[]{(byte) Tag.LONG.getCode()},
                new byte[] {36,37,38,39,40,41,42,43},
                /*SHORT	83	'S' - a short value (2 bytes).  */
                new byte[]{(byte) Tag.SHORT.getCode()},
                new byte[] {44,45},
                /*VOID	86	'V' - a void value (no bytes).  */
                new byte[]{(byte) Tag.VOID.getCode()},
                /*BOOLEAN	90	'Z' - a boolean value (1 byte).  */
                new byte[]{(byte) Tag.BOOLEAN.getCode()},
                new byte[] {46},
                /*STRING	115	's' - a String object (objectID size).  */
                new byte[]{(byte) Tag.STRING.getCode()},
                new ObjectId(new byte[]{47,48,49,50,51,52,53,54}).toByteArray(),
                /*THREAD	116	't' - a Thread object (objectID size).  */
                new byte[]{(byte) Tag.THREAD.getCode()},
                new ObjectId(new byte[]{55,56,57,58,59,60,61,62}).toByteArray(),
                /*THREAD_GROUP	103	'g' - a ThreadGroup object (objectID size).  */
                new byte[]{(byte) Tag.THREAD_GROUP.getCode()},
                new ObjectId(new byte[]{63,64,65,66,67,68,69,70}).toByteArray(),
                /*CLASS_LOADER	108	'l' - a ClassLoader object (objectID size).  */
                new byte[]{(byte) Tag.CLASS_LOADER.getCode()},
                new ObjectId(new byte[]{71,72,73,74,75,76,77,78}).toByteArray(),
                /*CLASS_OBJECT	99	'c' - a class object object (objectID size).*/
                new byte[]{(byte) Tag.CLASS_OBJECT.getCode()},
                new ObjectId(new byte[]{79,80,81,82,83,84,85,86}).toByteArray()
        );
        ArrayOffset offset = new ArrayOffset();
        /*ARRAY	91	'[' - an array object (objectID size).  */
        //new ObjectId(new byte[]{1,2,3,4,5,6,7,8}).toByteArray(),
        assertArrayEquals(
                new byte[]{1,2,3,4,5,6,7,8},
                JdwpDataTypeReader.readValue(in, offset).getObjectId().toByteArray()
        );
        /*BYTE	66	'B' - a byte value (1 byte).  */
        //new byte[] {9},
        assertEquals(9, JdwpDataTypeReader.readValue(in, offset).getByte());
        /*CHAR	67	'C' - a character value (2 bytes).  */
        //new byte[] {10, 11},
        assertTrue('\u0a0b' == JdwpDataTypeReader.readValue(in, offset).getChar());
        /*OBJECT	76	'L' - an object (objectID size).  */
        //new ObjectId(new byte[]{12,13,14,15,16,17,18,19}).toByteArray(),
        assertArrayEquals(
                new byte[]{12,13,14,15,16,17,18,19},
                JdwpDataTypeReader.readValue(in, offset).getObjectId().toByteArray()
        );
        /*FLOAT	70	'F' - a float value (4 bytes).  */
        //new byte[] {20, 21, 22, 23},
        assertEquals(
                ByteBuffer.wrap(
                        new byte[] {20, 21, 22, 23}
                ).order(ByteOrder.BIG_ENDIAN).getFloat(),
                JdwpDataTypeReader.readValue(in, offset).getFloat(),
                1e-10
        );
        /*DOUBLE	68	'D' - a double value (8 bytes).  */
        //new byte[] {24,25,26,27,28,29,30,31},
        assertEquals(
                ByteBuffer.wrap(
                        new byte[] {24,25,26,27,28,29,30,31}
                ).order(ByteOrder.BIG_ENDIAN).getDouble(),
                JdwpDataTypeReader.readValue(in, offset).getDouble(),
                1e-20
        );
        /*INT	73	'I' - an int value (4 bytes).  */
        //new byte[] {32,33,34,35},
        assertEquals(539042339, JdwpDataTypeReader.readValue(in, offset).getInt());
        /*LONG	74	'J' - a long value (8 bytes).  */
        //new byte[] {36,37,38,39,40,41,42,43},
        assertEquals(2604529909123066411L, JdwpDataTypeReader.readValue(in, offset).getLong());
        /*SHORT	83	'S' - a short value (2 bytes).  */
        //new byte[] {44,45},
        assertEquals(11309, JdwpDataTypeReader.readValue(in, offset).getShort());
        /*VOID	86	'V' - a void value (no bytes).  */
        assertEquals(Tag.VOID, JdwpDataTypeReader.readValue(in, offset).getTag());
        /*BOOLEAN	90	'Z' - a boolean value (1 byte).  */
        //new byte[] {46},
        assertTrue(JdwpDataTypeReader.readValue(in, offset).getBoolean());
        /*STRING	115	's' - a String object (objectID size).  */
        //new ObjectId(new byte[]{47,48,49,50,51,52,53,54}).toByteArray(),
        assertArrayEquals(
                new byte[]{47,48,49,50,51,52,53,54},
                JdwpDataTypeReader.readValue(in, offset).getObjectId().toByteArray()
        );
        /*THREAD	116	't' - a Thread object (objectID size).  */
        //new ObjectId(new byte[]{55,56,57,58,59,60,61,62}).toByteArray(),
        assertArrayEquals(
                new byte[]{55,56,57,58,59,60,61,62},
                JdwpDataTypeReader.readValue(in, offset).getObjectId().toByteArray()
        );
        /*THREAD_GROUP	103	'g' - a ThreadGroup object (objectID size).  */
        //new ObjectId(new byte[]{63,64,65,66,67,68,69,70}).toByteArray(),
        assertArrayEquals(
                new byte[]{63,64,65,66,67,68,69,70},
                JdwpDataTypeReader.readValue(in, offset).getObjectId().toByteArray()
        );
        /*CLASS_LOADER	108	'l' - a ClassLoader object (objectID size).  */
        //new ObjectId(new byte[]{71,72,73,74,75,76,77,78}).toByteArray(),
        assertArrayEquals(
                new byte[]{71,72,73,74,75,76,77,78},
                JdwpDataTypeReader.readValue(in, offset).getObjectId().toByteArray()
        );
        /*CLASS_OBJECT	99	'c' - a class object object (objectID size).*/
        //new ObjectId(new byte[]{79,80,81,82,83,84,85,86}).toByteArray()
        assertArrayEquals(
                new byte[]{79,80,81,82,83,84,85,86},
                JdwpDataTypeReader.readValue(in, offset).getObjectId().toByteArray()
        );
    }
}
