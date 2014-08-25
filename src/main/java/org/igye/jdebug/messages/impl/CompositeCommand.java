package org.igye.jdebug.messages.impl;

import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.core.CommandPacket;

public class CompositeCommand extends JdwpMessage {
    private byte suspendPolicy;
    private Event[] events;

    public CompositeCommand(CommandPacket commandPacket) {
        setCommandOrReplyPacket(commandPacket);
        suspendPolicy = readByte();
        events = new Event[readInt()];
        for (int i = 0; i < events.length; i++) {
            events[i] = readEvent();
        }
    }

    public byte getSuspendPolicy() {
        return suspendPolicy;
    }

    public Event[] getEvents() {
        return events;
    }
}
