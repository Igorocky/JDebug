package org.igye.jdebug.messages.impl;

import org.igye.jdebug.datatypes.impl.*;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.EventKind;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.ReplyPacket;

public class Event {
    private byte eventKind;
    private int requestId;
    private ObjectId thread;
    private Location location;
    private TaggedObjectId exception;
    private Location catchLocation;
    private byte refTypeTag;
    private ObjectId typeId;
    private String signature;
    private int status;
    private FieldId fieldId;
    private TaggedObjectId object;
    private Value valueToBe;

    private Event(byte eventKind, int requestId) {
        this.eventKind = eventKind;
        this.requestId = requestId;
    }

    public static Event createVmStartEvent(int requestId, ObjectId thread) {
        Event event = new Event((byte) EventKind.VM_START.getCode(), requestId);
        event.thread = thread;
        return event;
    }

    public static Event createSingleStepEvent(int requestId, ObjectId thread, Location location) {
        Event event = new Event((byte) EventKind.SINGLE_STEP.getCode(), requestId);
        event.thread = thread;
        event.location = location;
        return event;
    }

    public static Event createBreakPointEvent(int requestId, ObjectId thread, Location location) {
        Event event = new Event((byte) EventKind.BREAKPOINT.getCode(), requestId);
        event.thread = thread;
        event.location = location;
        return event;
    }

    public static Event createMethodEntryEvent(int requestId, ObjectId thread, Location location) {
        Event event = new Event((byte) EventKind.METHOD_ENTRY.getCode(), requestId);
        event.thread = thread;
        event.location = location;
        return event;
    }

    public static Event createMethodExitEvent(int requestId, ObjectId thread, Location location) {
        Event event = new Event((byte) EventKind.BREAKPOINT.getCode(), requestId);
        event.thread = thread;
        event.location = location;
        return event;
    }

    public static Event createExceptionEvent(int requestId, ObjectId thread, Location location,
                                             TaggedObjectId exception, Location catchLocation) {
        Event event = new Event((byte) EventKind.EXCEPTION.getCode(), requestId);
        event.thread = thread;
        event.location = location;
        event.exception = exception;
        event.catchLocation = catchLocation;
        return event;
    }

    public static Event createThreadStartEvent(int requestId, ObjectId thread) {
        Event event = new Event((byte) EventKind.THREAD_START.getCode(), requestId);
        event.thread = thread;
        return event;
    }

    public static Event createThreadDeathEvent(int requestId, ObjectId thread) {
        Event event = new Event((byte) EventKind.THREAD_DEATH.getCode(), requestId);
        event.thread = thread;
        return event;
    }

    public static Event createClassPrepareEvent(int requestId, ObjectId thread,
                                                byte refTypeTag,
                                                ObjectId typeId,
                                                String signature,
                                                int status) {
        Event event = new Event((byte) EventKind.CLASS_PREPARE.getCode(), requestId);
        event.thread = thread;
        event.refTypeTag = refTypeTag;
        event.typeId = typeId;
        event.signature = signature;
        event.status = status;
        return event;
    }

    public static Event createClassUnloadEvent(int requestId, String signature) {
        Event event = new Event((byte) EventKind.CLASS_UNLOAD.getCode(), requestId);
        event.signature = signature;
        return event;
    }

    public static Event createFieldAccessEvent(int requestId, ObjectId thread,
                                                Location location,
                                                byte refTypeTag,
                                                ObjectId typeId,
                                                FieldId fieldId,
                                                TaggedObjectId object) {
        Event event = new Event((byte) EventKind.FIELD_ACCESS.getCode(), requestId);
        event.thread = thread;
        event.location = location;
        event.refTypeTag = refTypeTag;
        event.typeId = typeId;
        event.fieldId = fieldId;
        event.object = object;
        return event;
    }

    public static Event createFieldModificationEvent(int requestId, ObjectId thread,
                                               Location location,
                                               byte refTypeTag,
                                               ObjectId typeId,
                                               FieldId fieldId,
                                               TaggedObjectId object,
                                               Value valueToBe) {
        Event event = new Event((byte) EventKind.FIELD_MODIFICATION.getCode(), requestId);
        event.thread = thread;
        event.location = location;
        event.refTypeTag = refTypeTag;
        event.typeId = typeId;
        event.fieldId = fieldId;
        event.object = object;
        event.valueToBe = valueToBe;
        return event;
    }

    public static Event createVmDeathEvent(int requestId) {
        Event event = new Event((byte) EventKind.FIELD_MODIFICATION.getCode(), requestId);
        return event;
    }

}
