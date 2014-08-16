package org.igye.jdebug.messages.core;

public class MessageIdGenerator {
    private static MessageIdGenerator instance;
    private long id;

    private MessageIdGenerator() {
    }

    public static MessageIdGenerator getInstance() {
        if (instance == null) {
            instance = new MessageIdGenerator();
        }
        return instance;
    }

    public synchronized long generateId() {
        return id++;
    }
}
