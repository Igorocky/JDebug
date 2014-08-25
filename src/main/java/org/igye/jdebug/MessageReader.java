package org.igye.jdebug;

import org.igye.jdebug.datatypes.JdwpDataTypeReader;
import org.igye.jdebug.exceptions.EndOfStreamException;
import org.igye.jdebug.exceptions.JDebugRuntimeException;
import org.igye.jdebug.messages.JdwpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageReader implements Runnable {
    private Logger log = LoggerFactory.getLogger(MessageReader.class);

    private InputStream in;
    private BlockingQueue<JdwpMessage> inMessages;

    public MessageReader(InputStream in) {
        this.in = in;
        this.inMessages = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            JdwpMessage msg;
            try {
                synchronized (in) {
                    msg = JdwpDataTypeReader.readMessage(in);
                    log.debug("Message read: {}", msg);
                }
            } catch (Exception e) {
                throw new JDebugRuntimeException("Error while JdwpDataTypeReader.readMessage(in).", e);
            }
            try {
                inMessages.put(msg);
            } catch (InterruptedException e) {
                log.info("Interrupted while inMessages.put(msg).");
                return;
            }
        }
    }

    public JdwpMessage takeMessage() throws InterruptedException {
        return inMessages.take();
    }
}
