package org.igye.jdebug;

public interface DebugProcessor extends Runnable {
    void setWriterAndReader(MessageWriter messageWriter, MessageReader messageReader);
}
