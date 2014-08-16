package org.igye.jdebug.datatypes;

import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.impl.JdwpString;
import org.igye.jdebug.exceptions.JDebugRuntimeException;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.ReplyPacket;
import sun.security.util.Length;

import java.io.IOException;
import java.io.InputStream;

public class JdwpDataTypeReader {
    public static JdwpMessage readMessage(InputStream in) throws IOException {
        byte[] lengthArr = new byte[4];
        int bytesRead = in.read(lengthArr);
        if (bytesRead != lengthArr.length) {
            throw new IOException("bytesRead != lengthArr.length");
        }
        int length = (int) ByteArrays.fourByteArrayToLong(lengthArr);
        byte[] bytes = new byte[length - lengthArr.length];
        bytesRead = in.read(bytes);
        if (bytesRead != bytes.length) {
            throw new IOException("bytesRead != bytes.length");
        }
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
}
