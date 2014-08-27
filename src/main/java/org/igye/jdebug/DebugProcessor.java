package org.igye.jdebug;

public interface DebugProcessor extends Runnable {
    String getName();
    Object getParamsParser();
    void init(MessageWriter messageWriter, MessageReader messageReader,
              MainParams mainParams);
}
