package org.igye.jdebug.debugprocessors.tracemethods;

import org.igye.jdebug.*;
import org.igye.jdebug.datatypes.impl.MethodId;
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

    private MainParams mainParams;
    private TraceMethodsParamsParser paramsParser = new TraceMethodsParamsParser();

    private List<CommandPacket> commandsBuf = new ArrayList<>();

    private Map<ObjectId, String> threadNames = new HashMap<>();
    private Map<ObjectId, String> classNames = new HashMap<>();
    private Map<String, String> methodsNames = new HashMap<>();
    private Map<String, Integer> lineNumbers = new HashMap<>();

    @Override
    public void run() {
        try {
            initIdSizes();

            EventModifier[] methodEnterExitModifiers = new EventModifier[] {
                    new ClassMatch("org.igye*")
            };
            SuspendPolicy methodEnterExitSuspendPolicy = SuspendPolicy.EVENT_THREAD;
            int methodEntryRequestId = new SetReply(getReplyById(
                    msgWriter.putMessage(new SetCommand(
                            EventKind.METHOD_ENTRY,
                            methodEnterExitSuspendPolicy,
                            methodEnterExitModifiers
                    ))
            )).getRequestId();
            System.out.println("methodEntryRequestId = " + methodEntryRequestId);
            int methodExitRequestId = new SetReply(getReplyById(
                    msgWriter.putMessage(new SetCommand(
                            EventKind.METHOD_EXIT,
                            methodEnterExitSuspendPolicy,
                            methodEnterExitModifiers
                    ))
            )).getRequestId();
            System.out.println("methodExitRequestId = " + methodExitRequestId);

            msgWriter.putMessage(new ResumeCommand());

            while (true) {
                System.out.println("-------------------------------");
                CompositeCommand cmd = convertToCompositeCommand(getCommand());
                System.out.println("cmd.getSuspendPolicy() = " + cmd.getSuspendPolicy());
                System.out.println("cmd.getEvents().length = " + cmd.getEvents().length);
                boolean needResume = false;
                for (Event event : cmd.getEvents()) {
                    EventKind ek = EventKind.getEventKindByCode(event.getEventKind());
                    System.out.println("event.getEventKind() = " + ek);
                    System.out.println("event.getRequestId() = " + event.getRequestId());
                    if (event.getRequestId() == methodEntryRequestId
                            || event.getRequestId() == methodExitRequestId) {
                        System.out.println("event.getThread() = " + event.getThread());
                        System.out.println("tread name = " + getThreadName(event.getThread()));
                        System.out.println("event.getLocation() = " + event.getLocation());
                        System.out.println("class = " + getClassSignature(event.getLocation().getClassID()));
                        System.out.println("method = " + getMethodNameAndSignature(event.getLocation().getClassID(), event.getLocation().getMethodID()));
                        long codeIndex = ByteArrays.byteArrayToLong(event.getLocation().getIndex(), 0, 8);
                        System.out.println("line = " + getLineNumber(event.getLocation().getClassID(), event.getLocation().getMethodID(), codeIndex));
                        resumeThread(event.getThread());
                    }
                }
            }


        } catch (JDebugRuntimeException e) {
            throw e;
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

    private void clearAllBreakpoints() throws InterruptedException {
        new ClearAllBreakpointsReply(
                getReplyById(
                        msgWriter.putMessage(
                                new ClearAllBreakpointsCommand()
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

    private String getClassSignature(ObjectId classId) throws InterruptedException {
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

    private String getMethodNameAndSignature(ObjectId classId, MethodId methodId) throws InterruptedException {
        String key = classId.toString() + methodId.toString();
        String res = methodsNames.get(key);
        if (res == null) {
            MethodsReply mr = new MethodsReply(
                    getReplyById(msgWriter.putMessage(new MethodsCommand(classId)))
            );
            for (MethodInfo methodInfo : mr.getMethods()) {
                methodsNames.put(
                        classId.toString() + methodInfo.getMethodId().toString(),
                        methodInfo.getName() + methodInfo.getSignature()
                );
            }
            res = methodsNames.get(key);
        }
        if (res == null) {
            throw new JDebugRuntimeException("Can't determine method signature.");
        }
        return res;
    }

    private int getLineNumber(ObjectId classId, MethodId methodId, long lineCodeIndex) throws InterruptedException {
        String leftPartOfKey = classId.toString() + methodId.toString();
        String key = leftPartOfKey + "_" + lineCodeIndex;
        Integer res = lineNumbers.get(key);
        if (res == null) {
            LineTableReply ltr = new LineTableReply(
                    getReplyById(
                            msgWriter.putMessage(new LineTableCommand(
                                    classId, methodId
                            ))
                    )
            );
            for (LineTableEntry entry : ltr.getLineTable()) {
                String kk = leftPartOfKey + "_" + entry.getLineCodeIndex();
                lineNumbers.put(
                        leftPartOfKey + "_" + entry.getLineCodeIndex(),
                        entry.getLineNumber()
                );
            }
        }
        res = lineNumbers.get(key);
        if (res == null) {
            /*throw new JDebugRuntimeException("Can't determine line number. " +
                    " classId = " + classId +
                    " methodId = " + methodId +
                    " lineCodeIndex = " + lineCodeIndex);*/
            return -1;
        }
        return res;
    }

    @Override
    public String getName() {
        return "trace-methods";
    }

    @Override
    public Object getParamsParser() {
        return paramsParser;
    }

    @Override
    public void init(MessageWriter messageWriter, MessageReader messageReader,
                     MainParams mainParams) {
        this.msgWriter = messageWriter;
        this.msgReader = messageReader;
        this.mainParams = mainParams;
    }
}
