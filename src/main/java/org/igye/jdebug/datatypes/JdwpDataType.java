package org.igye.jdebug.datatypes;

import java.io.IOException;
import java.io.InputStream;

public interface JdwpDataType {
    public abstract byte[] toByteArray();
}
