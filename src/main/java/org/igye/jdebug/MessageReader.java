package org.igye.jdebug;

import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

public class MessageReader implements Runnable {
    private InputStreamReader in;
    private BlockingQueue inMessages;

    public MessageReader(InputStreamReader in, BlockingQueue inMessages) {
        this.in = in;
        this.inMessages = inMessages;
    }

    @Override
    public void run() {

    }
}
