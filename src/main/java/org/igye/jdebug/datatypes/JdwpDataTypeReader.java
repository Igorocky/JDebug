package org.igye.jdebug.datatypes;

import org.igye.jdebug.ArrayOffset;
import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.impl.*;
import org.igye.jdebug.exceptions.EndOfStreamException;
import org.igye.jdebug.exceptions.JDebugRuntimeException;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.EventKind;
import org.igye.jdebug.messages.constants.Tag;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.IdSizes;
import org.igye.jdebug.messages.core.ReplyPacket;
import org.igye.jdebug.messages.impl.ClassInfo;
import org.igye.jdebug.messages.impl.Event;
import org.igye.jdebug.messages.impl.FrameInfo;
import org.igye.jdebug.messages.impl.MethodInfo;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class JdwpDataTypeReader {
    public static JdwpMessage readMessage(DataInputStream in) throws IOException {
        byte[] lengthArr = new byte[4];
        in.readFully(lengthArr);
        int length = (int) ByteArrays.fourByteArrayToLong(lengthArr);
        byte[] bytes = new byte[length - lengthArr.length];
        in.readFully(bytes);
        byte[] data = length == 11 ? null : new byte[length - 11];
        if (data != null) {
            System.arraycopy(bytes, 7, data, 0, data.length);
        }
        long id = ByteArrays.byteArrayToLong(bytes, 0, 4);
        int flags = (int) ByteArrays.byteArrayToLong(bytes, 4, 1);
        if (flags == JdwpMessage.COMMAND_FLAG) {
            int commandSet = (int) ByteArrays.byteArrayToLong(bytes, 5, 1);
            int command = (int) ByteArrays.byteArrayToLong(bytes, 6, 1);
            return new CommandPacket(id, commandSet, command, data);
        } else if (flags == JdwpMessage.REPLY_FLAG) {
            int errorCode = (int) ByteArrays.byteArrayToLong(bytes, 5, 2);
            return new ReplyPacket(id, errorCode, data);
        } else {
            throw new JDebugRuntimeException("Unknown value in flags (" + flags + ")");
        }
    }

    public static String readString(byte[] in, int offset) {
        int length = (int) ByteArrays.byteArrayToLong(in, offset, 4);
        byte[] stringArr = new byte[length];
        System.arraycopy(in, offset + 4, stringArr, 0, length);
        return new String(stringArr);
    }

    public static String readString(byte[] in, ArrayOffset offset) {
        String res = readString(in, offset.getOffset());
        offset.increase(4 + res.length());
        return res;
    }

    public static int readInt(byte[] in, int offset) {
        return (int) ByteArrays.byteArrayToLong(in, offset, 4);
    }

    public static int readInt(byte[] in, ArrayOffset offset) {
        int res = readInt(in, offset.getOffset());
        offset.increase(4);
        return res;
    }

    public static long readLong(byte[] in, int offset) {
        return ByteArrays.byteArrayToLong(in, offset, 8);
    }

    public static long readLong(byte[] in, ArrayOffset offset) {
        long res = readLong(in, offset.getOffset());
        offset.increase(8);
        return res;
    }

    public static byte readByte(byte[] in, ArrayOffset offset) {
        return (byte) ByteArrays.byteArrayToLong(in, offset, 1);
    }

    public static ObjectId readObjectId(byte[] in, ArrayOffset offset) {
        return new ObjectId(ByteArrays.byteArrayToByteArray(in, offset, IdSizes.getObjectIDSize()));
    }

    public static FrameId readFrameId(byte[] in, ArrayOffset offset) {
        return new FrameId(ByteArrays.byteArrayToByteArray(in, offset, IdSizes.getFrameIDSize()));
    }

    public static TaggedObjectId readTaggedObjectId(byte[] in, ArrayOffset offset) {
        return new TaggedObjectId(
                ByteArrays.byteArrayToByteArray(in, offset, IdSizes.getObjectIDSize()),
                (byte)ByteArrays.byteArrayToLong(in, offset, 1)
        );
    }

    public static MethodId readMethodId(byte[] in, ArrayOffset offset) {
        return new MethodId(ByteArrays.byteArrayToByteArray(in, offset, IdSizes.getObjectIDSize()));
    }

    public static FieldId readFieldId(byte[] in, ArrayOffset offset) {
        return new FieldId(ByteArrays.byteArrayToByteArray(in, offset, IdSizes.getObjectIDSize()));
    }

    public static Location readLocation(byte[] in, ArrayOffset offset) {
        return new Location(
                (byte)ByteArrays.byteArrayToLong(in, offset, 1),
                readObjectId(in, offset),
                readMethodId(in, offset),
                ByteArrays.byteArrayToByteArray(in, offset, 8)
        );
    }

    public static Event readEvent(byte[] in, ArrayOffset offset) {
        EventKind eventKind = EventKind.getEventKindByCode(
                (int) ByteArrays.byteArrayToLong(in, offset, 1)
        );
        switch (eventKind) {
            case VM_START:
            case VM_INIT:
                return Event.createVmStartEvent(
                        readInt(in, offset),
                        readObjectId(in, offset)
                );
            case THREAD_DEATH:
                return Event.createThreadDeathEvent(
                        readInt(in, offset),
                        readObjectId(in, offset)
                );
            case SINGLE_STEP:
                return Event.createSingleStepEvent(
                        readInt(in, offset),
                        readObjectId(in, offset),
                        readLocation(in, offset)
                );
            case BREAKPOINT:
                return Event.createBreakPointEvent(
                        readInt(in, offset),
                        readObjectId(in, offset),
                        readLocation(in, offset)
                );
            case EXCEPTION:
                return Event.createExceptionEvent(
                        readInt(in, offset),
                        readObjectId(in, offset),
                        readLocation(in, offset),
                        readTaggedObjectId(in, offset),
                        readLocation(in, offset)
                );
            case THREAD_START:
                return Event.createThreadStartEvent(
                        readInt(in, offset),
                        readObjectId(in, offset)
                );
            case THREAD_END:
                return Event.createThreadDeathEvent(
                        readInt(in, offset),
                        readObjectId(in, offset)
                );
            case CLASS_PREPARE:
                return Event.createClassPrepareEvent(
                        readInt(in, offset),
                        readObjectId(in, offset),
                        (byte) ByteArrays.byteArrayToLong(in, offset, 1),
                        readObjectId(in, offset),
                        readString(in, offset),
                        readInt(in, offset)
                );
            case CLASS_UNLOAD:
                return Event.createClassUnloadEvent(
                        readInt(in, offset),
                        readString(in, offset)
                );
            case FIELD_ACCESS:
                return Event.createFieldAccessEvent(
                        readInt(in, offset),
                        readObjectId(in, offset),
                        readLocation(in, offset),
                        (byte) ByteArrays.byteArrayToLong(in, offset, 1),
                        readObjectId(in, offset),
                        readFieldId(in, offset),
                        readTaggedObjectId(in, offset)
                );
            case FIELD_MODIFICATION:
                return Event.createFieldModificationEvent(
                        readInt(in, offset),
                        readObjectId(in, offset),
                        readLocation(in, offset),
                        (byte) ByteArrays.byteArrayToLong(in, offset, 1),
                        readObjectId(in, offset),
                        readFieldId(in, offset),
                        readTaggedObjectId(in, offset),
                        readValue(in, offset)
                );
            case EXCEPTION_CATCH:
                return Event.createExceptionEvent(
                        readInt(in, offset),
                        readObjectId(in, offset),
                        readLocation(in, offset),
                        readTaggedObjectId(in, offset),
                        readLocation(in, offset)
                );
            case METHOD_ENTRY:
                return Event.createMethodEntryEvent(
                        readInt(in, offset),
                        readObjectId(in, offset),
                        readLocation(in, offset)
                );
            case METHOD_EXIT:
                return Event.createMethodExitEvent(
                        readInt(in, offset),
                        readObjectId(in, offset),
                        readLocation(in, offset)
                );
            case VM_DEATH:
                return Event.createVmDeathEvent(
                        readInt(in, offset)
                );
        }
        throw new JDebugRuntimeException("Can't create event for event kind " + eventKind);
    }

    public static Value readValue(byte[] in, ArrayOffset offset) {
        Tag tag = Tag.getTagByCode((int) ByteArrays.byteArrayToLong(in, offset, 1));
        Value res = new Value(tag);
        switch (tag) {
            case ARRAY:
                res.setObjectId(readObjectId(in, offset));
                break;
            case BYTE:
                res.setByte((byte) ByteArrays.byteArrayToLong(in, offset, 1));
                break;
            case CHAR:
                res.setChar((char) ByteArrays.byteArrayToLong(in, offset, 2));
                break;
            case OBJECT:
                res.setObjectId(readObjectId(in, offset));
                break;
            case FLOAT:
                res.setFloat(
                        ByteBuffer.wrap(
                            ByteArrays.byteArrayToByteArray(in, offset, 4)
                        ).order(ByteOrder.BIG_ENDIAN).getFloat()
                );
                break;
            case DOUBLE:
                res.setDouble(
                        ByteBuffer.wrap(
                                ByteArrays.byteArrayToByteArray(in, offset, 8)
                        ).order(ByteOrder.BIG_ENDIAN).getDouble()
                );
                break;
            case INT:
                res.setInt(readInt(in, offset));
                break;
            case LONG:
                res.setLong(readLong(in, offset));
                break;
            case SHORT:
                res.setShort((short) ByteArrays.byteArrayToLong(in, offset, 2));
                break;
            case VOID:
                break;
            case BOOLEAN:
                res.setBoolean(ByteArrays.byteArrayToLong(in, offset, 1) != 0);
                break;
            case STRING:
                res.setObjectId(readObjectId(in, offset));
                break;
            case THREAD:
                res.setObjectId(readObjectId(in, offset));
                break;
            case THREAD_GROUP:
                res.setObjectId(readObjectId(in, offset));
                break;
            case CLASS_LOADER:
                res.setObjectId(readObjectId(in, offset));
                break;
            case CLASS_OBJECT:
                res.setObjectId(readObjectId(in, offset));
                break;
        }
        return res;
    }

    public static ClassInfo readClassInfo(byte[] in, ArrayOffset offset) {
        return new ClassInfo(
                readByte(in, offset),
                readObjectId(in, offset),
                readString(in, offset),
                readInt(in, offset)
        );
    }

    public static MethodInfo readMethodInfo(byte[] in, ArrayOffset offset) {
        return new MethodInfo(
                readMethodId(in, offset),
                readString(in, offset),
                readString(in, offset),
                readInt(in, offset)
        );
    }

    public static FrameInfo readFrameInfo(byte[] in, ArrayOffset offset) {
        return new FrameInfo(
                readFrameId(in, offset),
                readLocation(in, offset)
        );
    }
}
