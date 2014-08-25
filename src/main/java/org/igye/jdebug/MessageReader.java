package org.igye.jdebug;

import org.igye.jdebug.datatypes.JdwpDataTypeReader;
import org.igye.jdebug.exceptions.EndOfStreamException;
import org.igye.jdebug.exceptions.JDebugException;
import org.igye.jdebug.exceptions.JDebugRuntimeException;
import org.igye.jdebug.messages.JdwpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageReader implements Runnable {
    private Logger log = LoggerFactory.getLogger(MessageReader.class);

    private volatile boolean isAlive;

    private InputStream in;
    private BlockingQueue<JdwpMessage> inMessages;

    public MessageReader(InputStream in) {
        this.in = in;
        this.inMessages = new LinkedBlockingQueue<>();
    }

    @Override
    public void run() {
        isAlive = true;
        try {
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
        } catch (Exception e) {
            log.error("Exception:", e);
        }
        isAlive = false;
    }

    public JdwpMessage takeMessage() throws JDebugRuntimeException {
        while (true) {
            if (!isAlive && inMessages.isEmpty()) {
                throw new JDebugRuntimeException("!isAlive && inMessages.isEmpty()");
            }
            try {
                JdwpMessage res = inMessages.poll(1, TimeUnit.SECONDS);
                if (res != null) {
                    return res;
                }
            } catch (InterruptedException e) {
                throw new JDebugRuntimeException(e);
            }
        }
    }
}
