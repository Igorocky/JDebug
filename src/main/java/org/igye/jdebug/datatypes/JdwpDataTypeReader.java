package org.igye.jdebug.datatypes;

import org.igye.jdebug.ByteArrays;
import org.igye.jdebug.datatypes.impl.JdwpString;

import java.io.IOException;
import java.io.InputStream;

public class JdwpDataTypeReader {
    public static JdwpString readJdwpString(InputStream in) throws IOException {
        byte[] lengthArr = new byte[4];
        int bytesRead = in.read(lengthArr);
        if (bytesRead != 4) {
            throw new IOException("bytesRead != 4");
        }
        int lenght = (int) ByteArrays.fourByteArrayToLong(lengthArr);
        byte[] bytes = new byte[lenght];
        bytesRead = in.read(bytes);
        if (bytesRead != lenght) {
            throw new IOException("bytesRead != lenght");
        }
        return new JdwpString(new String(bytes));
    }
}
