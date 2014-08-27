package org.igye.jdebug.debugprocessors.tracemethods;

import org.igye.jdebug.*;
import org.igye.jdebug.datatypes.impl.Location;
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
import org.igye.jdebug.messages.eventmodifiers.ClassExclude;
import org.igye.jdebug.messages.eventmodifiers.ClassMatch;
import org.igye.jdebug.messages.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
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

    private PrintStream rawOutputFile;
    private String rawOutputFileName = "raw.txt";
    private PrintStream threadNamesFile;
    private String threadNamesFileName = "thread_names.txt";
    private PrintStream classNamesFile;
    private String classNamesFileName = "class_names.txt";
    private PrintStream methodNamesFile;
    private String methodNamesFileName = "method_names.txt";

    @Override
    public void run() {
        try {
            initIdSizes();
            openFiles();

//            EventModifier[] methodEnterExitModifiers = new EventModifier[] {
//                    new ClassMatch("org.igye*")
//            };
            EventModifier[] methodEnterExitModifiers = createModifiers();
            SuspendPolicy allEventsSuspendPolicy = SuspendPolicy.EVENT_THREAD;
            int threadStartRequestId = new SetReply(getReplyById(
                    msgWriter.putMessage(new SetCommand(
                            EventKind.THREAD_START,
                            allEventsSuspendPolicy,
                            null
                    ))
            )).getRequestId();
            log.debug("threadStartRequestId = {}", threadStartRequestId);
            int threadDeathRequestId = new SetReply(getReplyById(
                    msgWriter.putMessage(new SetCommand(
                            EventKind.THREAD_DEATH,
                            allEventsSuspendPolicy,
                            null
                    ))
            )).getRequestId();
            log.debug("threadDeathRequestId = {}", threadDeathRequestId);
            int methodEntryRequestId = new SetReply(getReplyById(
                    msgWriter.putMessage(new SetCommand(
                            EventKind.METHOD_ENTRY,
                            allEventsSuspendPolicy,
                            methodEnterExitModifiers
                    ))
            )).getRequestId();
            log.debug("methodEntryRequestId = {}", methodEntryRequestId);
            int methodExitRequestId = new SetReply(getReplyById(
                    msgWriter.putMessage(new SetCommand(
                            EventKind.METHOD_EXIT,
                            allEventsSuspendPolicy,
                            methodEnterExitModifiers
                    ))
            )).getRequestId();
            log.debug("methodExitRequestId = {}", methodExitRequestId);

            msgWriter.putMessage(new ResumeCommand());

            while (true) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
                Date date = new Date();
                log.debug("-------------------------------");
                CompositeCommand cmd = convertToCompositeCommand(getCommand());
                if (log.isDebugEnabled()) {
                    log.debug("cmd.getSuspendPolicy() = " + cmd.getSuspendPolicy());
                    log.debug("cmd.getEvents().length = " + cmd.getEvents().length);
                }
                for (Event event : cmd.getEvents()) {
                    if (log.isDebugEnabled()) {
                        EventKind ek = EventKind.getEventKindByCode(event.getEventKind());
                        log.debug("event.getEventKind() = " + ek);
                        log.debug("event.getRequestId() = " + event.getRequestId());
                    }
                    long requestId = event.getRequestId();
                    if (requestId == methodEntryRequestId || requestId == methodExitRequestId) {
                        resumeThread(event.getThread());

                        Location location = event.getLocation();
                        if (log.isDebugEnabled()) {
                            log.debug("event.getThread() = " + event.getThread());
                            log.debug("tread name = " + getThreadName(event.getThread()));
                            log.debug("event.getLocation() = " + event.getLocation());
                            log.debug("class = " + getClassSignature(location.getClassID()));
                            log.debug("method = " + getMethodNameAndSignature(location.getClassID(), location.getMethodID()));
                        }
                        getThreadName(event.getThread());
                        getClassSignature(location.getClassID());
                        getMethodNameAndSignature(location.getClassID(), location.getMethodID());
                        long codeIndex = ByteArrays.byteArrayToLong(location.getIndex(), 0, 8);
                        int lineNumber = getLineNumber(location.getClassID(), location.getMethodID(), codeIndex);
                        if (log.isDebugEnabled()) {
                            log.debug("line = " + lineNumber);
                        }

                        date.setTime(System.currentTimeMillis());
                        rawOutputFile.println(
                                SequentialNumberGenerator.getInstance().next() + " " +
                                        simpleDateFormat.format(date) + " " +
                                        (requestId == methodEntryRequestId ? EventKind.METHOD_ENTRY.getCode() : EventKind.METHOD_EXIT.getCode()) + " " +
                                        event.getThread() + " " +
                                        location.getClassID() + " " +
                                        location.getMethodID() + " " +
                                        codeIndex + " " +
                                        lineNumber
                        );
                    } else if (requestId == threadStartRequestId || requestId == threadDeathRequestId) {
                        getThreadName(event.getThread());

                        if (log.isDebugEnabled()) {
                            log.debug("event.getThread() = " + event.getThread());
                            log.debug("tread name = " + getThreadName(event.getThread()));
                        }

                        rawOutputFile.println(
                                SequentialNumberGenerator.getInstance().next() + " " +
                                        simpleDateFormat.format(date) + " " +
                                        event.getEventKind() + " " +
                                        event.getThread()
                        );
                        shortPause(500);
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
        } finally {
            if (rawOutputFile != null) {
                rawOutputFile.close();
            }
            if (threadNamesFile != null) {
                threadNamesFile.close();
            }
            if (classNamesFile != null) {
                classNamesFile.close();
            }
            if (methodNamesFile != null) {
                methodNamesFile.close();
            }
        }
    }

    private EventModifier[] createModifiers() {
        List<EventModifier> modifiersList = new ArrayList<>();
        if (paramsParser.getClassMatch() != null) {
            String[] patterns = paramsParser.getClassMatch().split(",");
            for (String pattern : patterns) {
                modifiersList.add(new ClassMatch(pattern));
            }
        }
        if (paramsParser.getClassExclude() != null) {
            String[] patterns = paramsParser.getClassExclude().split(",");
            for (String pattern : patterns) {
                modifiersList.add(new ClassExclude(pattern));
            }
        }
        EventModifier[] res = new EventModifier[modifiersList.size()];
        for (int i = 0; i < modifiersList.size(); i++) {
            res[i] = modifiersList.get(i);
        }
        return res;
    }

    private void openFiles() throws FileNotFoundException {
        String outputDirStr = "./" + paramsParser.getDirToStoreResultsTo();
        if (paramsParser.isAppendHostPort()) {
            outputDirStr += "_" + mainParams.getHost() + "_" + mainParams.getPort();
        }
        if (paramsParser.isAppendDateTime()) {
            outputDirStr += "__" + new SimpleDateFormat("yyyy-MM-dd__HH_mm_ss").format(new Date());
        }
        File outputDir = new File(outputDirStr);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        rawOutputFile = new PrintStream(new File(outputDirStr + "/" + rawOutputFileName));
        threadNamesFile = new PrintStream(new File(outputDirStr + "/" + threadNamesFileName));
        classNamesFile = new PrintStream(new File(outputDirStr + "/" + classNamesFileName));
        methodNamesFile = new PrintStream(new File(outputDirStr + "/" + methodNamesFileName));
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

        log.debug("IdSizes.getFieldIDSize() = " + IdSizes.getFieldIDSize());
        log.debug("IdSizes.getFrameIDSize() = " + IdSizes.getFrameIDSize());
        log.debug("IdSizes.getMethodIDSize() = " + IdSizes.getMethodIDSize());
        log.debug("IdSizes.getObjectIDSize() = " + IdSizes.getObjectIDSize());
        log.debug("IdSizes.getReferenceTypeIDSize() = " + IdSizes.getReferenceTypeIDSize());
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
            threadNamesFile.println(threadId + " " + res);
            threadNamesFile.flush();
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
            classNamesFile.println(classId + " " + res);
            classNamesFile.flush();
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
                String methodNameAndSignature = methodInfo.getName() + methodInfo.getSignature();
                methodsNames.put(
                        classId.toString() + methodInfo.getMethodId().toString(),
                        methodNameAndSignature
                );
                methodNamesFile.println(classId + " " + methodInfo.getMethodId() + " " + methodNameAndSignature);
                methodNamesFile.flush();
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
            if (lineCodeIndex == 0xffffffffffffffffL) {
                lineNumbers.put(key, -2);
            } else {
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

    private void shortPause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
