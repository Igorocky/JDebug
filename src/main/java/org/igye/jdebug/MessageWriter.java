package org.igye.jdebug;

import org.igye.jdebug.datatypes.JdwpDataTypeReader;
import org.igye.jdebug.exceptions.JDebugRuntimeException;
import org.igye.jdebug.messages.JdwpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageWriter implements Runnable {
    private Logger log = LoggerFactory.getLogger(MessageWriter.class);

    private OutputStream out;
    private BlockingQueue<JdwpMessage> outMessages;

    public MessageWriter(OutputStream out) {
        this.out = out;
        this.outMessages = new LinkedBlockingQueue<>();;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                try {
                    synchronized (out) {
                        JdwpMessage msg = outMessages.poll(1, TimeUnit.SECONDS);
                        if (msg != null) {
                            log.debug("About to write msg: {}", msg);
                            out.write(msg.toByteArray());
                        }
                    }
                } catch (InterruptedException e) {
                    log.info("Interrupted while outMessages.poll().");
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    throw new JDebugRuntimeException("Exception while writing message.", e);
                }
            }
        } catch (Exception e) {
            log.error("Exception:", e);
        }
    }

    public long putMessage(JdwpMessage msg) throws InterruptedException {
        long res = msg.getId();
        outMessages.put(msg);
        return res;
    }
}
