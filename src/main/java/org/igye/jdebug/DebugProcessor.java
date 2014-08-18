package org.igye.jdebug;

public class DebugProcessor implements Runnable {
    private MessageReader msgReader;
    private MessageWriter msgWriter;

    public DebugProcessor(MessageReader msgReader, MessageWriter msgWriter) {
        this.msgReader = msgReader;
        this.msgWriter = msgWriter;
    }

    @Override
    public void run() {

    }
}
