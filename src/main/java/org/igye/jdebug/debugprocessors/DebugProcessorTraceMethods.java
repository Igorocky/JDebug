package org.igye.jdebug.debugprocessors;

import org.igye.jdebug.DebugProcessor;
import org.igye.jdebug.MessageReader;
import org.igye.jdebug.MessageWriter;
import org.igye.jdebug.datatypes.impl.ObjectId;
import org.igye.jdebug.exceptions.JDebugRuntimeException;
import org.igye.jdebug.messages.EventModifier;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.constants.Command;
import org.igye.jdebug.messages.constants.CommandSet;
import org.igye.jdebug.messages.constants.EventKind;
import org.igye.jdebug.messages.constants.SuspendPolicy;
import org.igye.jdebug.messages.core.CommandPacket;
import org.igye.jdebug.messages.core.IdSizes;
import org.igye.jdebug.messages.core.ReplyPacket;
import org.igye.jdebug.messages.eventmodifiers.ClassMatch;
import org.igye.jdebug.messages.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DebugProcessorTraceMethods implements DebugProcessor {
    private static Logger log = LoggerFactory.getLogger(DebugProcessorTraceMethods.class);

    private MessageReader msgReader;
    private MessageWriter msgWriter;

    private List<CommandPacket> commandsBuf = new ArrayList<>();

    private Map<ObjectId, String> threadNames = new HashMap<>();
    private Map<ObjectId, String> classNames = new HashMap<>();

    @Override
    public void run() {
        try {
            initIdSizes();

            long id = msgWriter.putMessage(new SetCommand(
                    EventKind.METHOD_ENTRY,
                    SuspendPolicy.EVENT_THREAD,
                    new EventModifier[] {new ClassMatch("org.igye*")}
            ));
            SetReply setReply = new SetReply(getReplyById(id));
            int requestId = setReply.getRequestId();
            System.out.println("setReply.getErrorCode() = " + setReply.getErrorCode());
            System.out.println("setReply.getRequestId() = " + setReply.getRequestId());

            msgWriter.putMessage(new ResumeCommand());

            while (true) {
                System.out.println("-------------------------------");
                CompositeCommand cmd = convertToCompositeCommand(getCommand());
                System.out.println("cmd.getEvents().length = " + cmd.getEvents().length);
                boolean needResume = false;
                for (Event event : cmd.getEvents()) {
                    EventKind ek = EventKind.getEventKindByCode(event.getEventKind());
                    System.out.println("event.getEventKind() = " + ek);
                    System.out.println("event.getRequestId() = " + event.getRequestId());
                    if (ek == EventKind.METHOD_ENTRY) {
                        System.out.println("event.getThread() = " + event.getThread());
                        System.out.println("tread name = " + getThreadName(event.getThread()));
                        System.out.println("event.getLocation() = " + event.getLocation());
                        System.out.println("class = " + getClassName(event.getLocation().getClassID()));
                        resumeThread(event.getThread());
                    }
                }
            }


        } catch (InterruptedException e) {
            log.error("InterruptedException in run().", e);
        } catch (Exception e) {
            log.error("Exception in run().", e);
        }
    }

    private void resumeThread(ObjectId threadId) throws InterruptedException {
        new ResumeThreadReply(
                getReplyById(
                        msgWriter.putMessage(
                                new ResumeThreadCommand(threadId)
                        )
                )
        );
    }

    private void initIdSizes() throws InterruptedException {
        long id = msgWriter.putMessage(new IdSizesCommand());
        IdSizesReply idSizesReply = new IdSizesReply(getReplyById(id));
        IdSizes.setFieldIDSize(idSizesReply.getFieldIDSize());
        IdSizes.setFrameIDSize(idSizesReply.getFrameIDSize());
        IdSizes.setMethodIDSize(idSizesReply.getMethodIDSize());
        IdSizes.setObjectIDSize(idSizesReply.getObjectIDSize());
        IdSizes.setReferenceTypeIDSize(idSizesReply.getReferenceTypeIDSize());

        System.out.println("IdSizes.getFieldIDSize() = " + IdSizes.getFieldIDSize());
        System.out.println("IdSizes.getFrameIDSize() = " + IdSizes.getFrameIDSize());
        System.out.println("IdSizes.getMethodIDSize() = " + IdSizes.getMethodIDSize());
        System.out.println("IdSizes.getObjectIDSize() = " + IdSizes.getObjectIDSize());
        System.out.println("IdSizes.getReferenceTypeIDSize() = " + IdSizes.getReferenceTypeIDSize());
    }

    private ReplyPacket getReplyById(long id) throws InterruptedException {
        JdwpMessage msg = null;
        while (true) {
            msg = msgReader.takeMessage();
            if (msg.getFlags() == JdwpMessage.REPLY_FLAG && id == msg.getId()) {
                return (ReplyPacket) msg;
            } else if (msg.getFlags() == JdwpMessage.COMMAND_FLAG) {
                commandsBuf.add((CommandPacket) msg);
            } else {
                log.info("getReplyById: Skipping msg: {}", msg);
            }
        }
    }

    private CommandPacket getCommand() throws InterruptedException {
        if (!commandsBuf.isEmpty()) {
            return commandsBuf.remove(0);
        } else {
            while (true) {
                JdwpMessage msg = msgReader.takeMessage();
                if (msg.getFlags() == JdwpMessage.COMMAND_FLAG) {
                    return (CommandPacket) msg;
                } else {
                    log.info("getCommand: Skipping msg: {}", msg);
                }
            }
        }
    }

    private CompositeCommand convertToCompositeCommand(CommandPacket commandPacket) {
        if (commandPacket.getCommandSet() == CommandSet.EVENT.getCode()
                && commandPacket.getCommand() == Command.COMPOSITE_COMMAND.getCode()) {
            return new CompositeCommand(commandPacket);
        } else {
            throw new IllegalArgumentException("convertToCompositeCommand: " +
                    "commandPacket is not a composite command. " +
                    commandPacket);
        }
    }

    private String getThreadName(ObjectId threadId) throws InterruptedException {
        String res = threadNames.get(threadId);
        if (res == null) {
            res = new ThreadNameReply(
                    getReplyById(
                            msgWriter.putMessage(
                                    new ThreadNameCommand(threadId)
                            )
                    )
            ).getName();
            threadNames.put(threadId, res);
        }
        return res;
    }

    private String getClassName(ObjectId classId) throws InterruptedException {
        String res = classNames.get(classId);
        if (res == null) {
            res= new SignatureReply(
                    getReplyById(
                            msgWriter.putMessage(
                                    new SignatureCommand(classId)
                            )
                    )
            ).getSignature();
            classNames.put(classId, res);
        }
        return res;
    }

    @Override
    public void setWriterAndReader(MessageWriter messageWriter, MessageReader messageReader) {
        this.msgWriter = messageWriter;
        this.msgReader = messageReader;
    }
}
