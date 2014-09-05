package org.igye.jdebug.debugprocessors.tracemethods;

import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.igye.jdebug.*;
import org.igye.jdebug.datatypes.impl.Location;
import org.igye.jdebug.datatypes.impl.MethodId;
import org.igye.jdebug.datatypes.impl.ObjectId;
import org.igye.jdebug.exceptions.JDebugException;
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DebugProcessorTraceMethods implements DebugProcessor {
    private static Logger log = LoggerFactory.getLogger(DebugProcessorTraceMethods.class);

    private MessageReader msgReader;
    private MessageWriter msgWriter;

    private MainParams mainParams;
    private TraceMethodsParamsParser paramsParser = new TraceMethodsParamsParser();

    private List<CommandPacket> commandsBuf = new ArrayList<>();

    private Date date = new Date();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
    private List<ObjectId> knownThreads = new ArrayList<>();
    private Map<ObjectId, String> threadNames = new HashMap<>();
    private Map<ObjectId, String> classNames = new HashMap<>();
    private Map<String, String> methodsNames = new HashMap<>();
    private Map<String, Integer> lineNumbers = new HashMap<>();

    private String outputDirStr;
    private PrintStream rawOutputFile;
    private String rawOutputFileName = "raw.txt";
    private PrintStream threadNamesFile;
    private String threadNamesFileName = "thread_names.txt";
    private PrintStream classNamesFile;
    private String classNamesFileName = "class_names.txt";
    private PrintStream methodNamesFile;
    private String methodNamesFileName = "method_names.txt";
    private String threadsFileName = "threads.txt";

    private Pattern arrayPat = Pattern.compile("^L(.+);$");

    @Override
    public void run() {
        if (paramsParser.doDebug()) {
            doDebug();
        } else {
            doFormatOutput();
        }
    }

    private void doFormatOutput() {
        try {
            createFormattedOutput();
        } catch (JDebugException | IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doDebug() {
        try {
            initIdSizes();
            openFiles();

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
                        if (!knownThreads.contains(event.getThread())) {
                            FrameInfo[] frames = getFrames(event.getThread());
                            knownThreads.add(event.getThread());
                            for (int i = frames.length - 1; i >= (requestId == methodEntryRequestId ? 1 : 0); i--) {
                                FrameInfo frameInfo = frames[i];
                                writeInfoAboutMethodEntryExitLocation(
                                        EventKind.METHOD_ENTRY,
                                        event.getThread(),
                                        frameInfo.getLocation(),
                                        true
                                );
                            }
                        }
                        resumeThread(event.getThread());

                        writeInfoAboutMethodEntryExitLocation(
                                (requestId == methodEntryRequestId ? EventKind.METHOD_ENTRY : EventKind.METHOD_EXIT),
                                event.getThread(),
                                event.getLocation(),
                                false
                        );
                    } else if (requestId == threadStartRequestId || requestId == threadDeathRequestId) {
                        if (!knownThreads.contains(event.getThread())) {
                            knownThreads.add(event.getThread());
                        }
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

    private void writeInfoAboutMethodEntryExitLocation(
            EventKind eventKind, ObjectId threadId, Location location,
            boolean isFromFrames) throws InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug("event.getThread() = " + threadId);
            log.debug("tread name = " + getThreadName(threadId));
            log.debug("event.getLocation() = " + location);
            log.debug("class = " + getClassSignature(location.getClassID()));
            log.debug("method = " + getMethodNameAndSignature(location.getClassID(), location.getMethodID()));
        }
        getThreadName(threadId);
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
                        simpleDateFormat.format(date) + (isFromFrames ? "*" : "") + " " +
                        eventKind.getCode() + " " +
                        threadId + " " +
                        location.getClassID() + " " +
                        location.getMethodID() + " " +
                        codeIndex + " " +
                        lineNumber
        );
    }

    private FrameInfo[] getFrames(ObjectId thread) throws InterruptedException {
        return new FramesReply(
                getReplyById(
                        msgWriter.putMessage(
                                new FramesCommand(thread, 0, -1)
                        )
                )
        ).getFrames();
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
        outputDirStr = "./" + paramsParser.getDirToStoreResultsTo();
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

    private void createFormattedOutput() throws IOException, JDebugException {
        outputDirStr = paramsParser.getDirToStoreResultsTo();
        PrintStream msgFile = null;
        PrintStream threadsFile = null;
        Map<String, ThreadData> threadsData = new HashMap<>();
        Float firstTime = null;
        try {
            msgFile = new PrintStream(new File(outputDirStr + "/msg.txt"));
            Map<String, String> threadNames = loadMap(threadNamesFileName, Pattern.compile("^([\\w\\d]+) (.+)$"));
            Map<String, String> classNames = loadMap(classNamesFileName, Pattern.compile("^([\\w\\d]+) (.+)$"));
            Map<String, String> methodNames = loadMap(methodNamesFileName, Pattern.compile("^([\\w\\d]+ [\\w\\d]+) (.+)$"));
            threadsFile = new PrintStream(new File(outputDirStr + "/" + threadsFileName));
            LineIterator lineIter = new LineIterator(new FileReader(outputDirStr + "/" + rawOutputFileName));
            Pattern pMethod = Pattern.compile("^(\\d+) (\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d\\*?) (\\d+) ([\\w\\d]+) ([\\w\\d]+) ([\\w\\d]+) ([-\\d]+) ([-\\d]+)$");
            Pattern pThread = Pattern.compile("^(\\d+) (\\d\\d:\\d\\d:\\d\\d\\.\\d\\d\\d) (\\d+) ([\\w\\d]+)$");
            Pattern pTime = Pattern.compile("^(\\d\\d):(\\d\\d):(\\d\\d\\.\\d\\d\\d)\\*?$");
            long lineNumber = 0;
            while (lineIter.hasNext()) {
                lineNumber++;
                String line = lineIter.nextLine();
                Matcher m = pMethod.matcher(line);
                if (m.matches() && m.groupCount() == 8) {
                    String eventNumber = m.group(1);
                    String time = m.group(2);
                    String eventKindStr = m.group(3);
                    String threadId = m.group(4);
                    String classId = m.group(5);
                    String className = convertClassNameFromJniToNormal(classNames.get(classId));
                    String methodId = m.group(6);
                    String codeIndex = m.group(7);
                    String lineNumberStr = m.group(8);
                    String message = null;
                    EventKind eventKind = EventKind.getEventKindByCode(Integer.parseInt(eventKindStr));
                    if (eventKind == EventKind.METHOD_ENTRY) {
                        createThreadData(
                                threadsData,
                                threadId,
                                threadNames,
                                msgFile,
                                null
                        );
                        message = StringUtils.leftPad("", getStackSize(threadsData, threadId)*4) +
                                "{> " +
                                eventNumber + " " +
                                time + " " +
                                className + ":" +
                                lineNumberStr + " " +
                                methodNames.get(classId + " " + methodId) + " " +
                                classId + "_" + methodId + " " +
                                codeIndex;
                        writeForThread(
                                threadsData,
                                threadId,
                                message
                        );
                        push(threadsData, threadId, classId, methodId);
                        Matcher timeMatcher = pTime.matcher(time);
                        timeMatcher.matches();
                        Float timeFloat = Float.parseFloat(timeMatcher.group(1))*60*60 +
                                Float.parseFloat(timeMatcher.group(2))*60 +
                                Float.parseFloat(timeMatcher.group(3));
                        if (firstTime == null) {
                            firstTime = timeFloat;
                        }
                        threadsData.get(threadId).getEventTimes().add(timeFloat - firstTime);
                    } else {
                        createThreadData(
                                threadsData,
                                threadId,
                                threadNames,
                                msgFile,
                                null
                        );
                        while (!(classId + methodId).equals(pop(threadsData, threadId))) {
                            message = StringUtils.leftPad(" ", (getStackSize(threadsData, threadId))*4) +
                                    "< ??? }";
                            writeForThread(
                                    threadsData,
                                    threadId,
                                    message
                            );
                        }
                        message = StringUtils.leftPad("", (getStackSize(threadsData, threadId))*4) +
                                "< " +
                                eventNumber + " " +
                                time + " " +
                                className + ":" + lineNumberStr + " " +
                                methodNames.get(classId + " " + methodId) + " " +
                                classId + "_" + methodId + " " +
                                codeIndex + " }";
                        writeForThread(
                                threadsData,
                                threadId,
                                message
                        );
                        Matcher timeMatcher = pTime.matcher(time);
                        timeMatcher.matches();
                        Float timeFloat = Float.parseFloat(timeMatcher.group(1))*60*60 +
                                Float.parseFloat(timeMatcher.group(2))*60 +
                                Float.parseFloat(timeMatcher.group(3));
                        if (firstTime == null) {
                            firstTime = timeFloat;
                        }
                        threadsData.get(threadId).getEventTimes().add(timeFloat - firstTime);
                    }
                } else {
                    m = pThread.matcher(line);
                    if (m.matches() && m.groupCount() == 4) {
                        String eventNumber = m.group(1);
                        String time = m.group(2);
                        String eventKindStr = m.group(3);
                        String threadId = m.group(4);
                        threadsFile.println(
                                eventNumber + " " +
                                        time + " " +
                                        EventKind.getEventKindByCode(Integer.parseInt(eventKindStr)) + " " +
                                        threadNames.get(threadId)
                        );
                        createThreadData(
                                threadsData,
                                threadId,
                                threadNames,
                                msgFile,
                                " [" + eventNumber + "] " + time + " " + EventKind.getEventKindByCode(Integer.parseInt(eventKindStr)).toString()
                        );
                    } else {
                        msgFile.println("Error: can't match line '" + line + "' ln = " + lineNumber);
                        return;
                    }
                }
            }
        } catch (JDebugException e) {
            log.error(e.getMessage(), e);
            msgFile.println("Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            msgFile.println("Error: " + e.getMessage());
            throw e;
        } finally {
            if (msgFile != null) {
                msgFile.close();
            }
            if (threadsFile != null) {
                threadsFile.close();
            }
            for (ThreadData threadData : threadsData.values()) {
                threadData.getPrintStream().close();
            }
        }

        XYSeriesCollection data = new XYSeriesCollection();
        int threadNumber = 0;
        for (ThreadData threadData : threadsData.values()) {
            threadNumber++;
            XYSeries threadSeries = new XYSeries(threadData.getName() +
                    "[" + threadData.getId() + "]");
            for (Float time : threadData.getEventTimes()) {
                threadSeries.add(time.doubleValue(), threadNumber);
            }
            data.addSeries(threadSeries);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Threads dynamic",
                "time, s",
                "thread",
                data,
                PlotOrientation.VERTICAL,
                true,
                true,
                true
        );
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer =
                new XYLineAndShapeRenderer(true, true);
        plot.setRenderer(renderer);
        renderer.setBaseShapesVisible(true);
        renderer.setBaseShapesFilled(true);
        ImageIO.write(
                chart.createBufferedImage(
                        paramsParser.getChartWidth(), paramsParser.getChartHeight()
                ),
                "png",
                new File(outputDirStr + "/chart.png")
        );
    }

    private Map<String, String> loadMap(String fileName, Pattern twoGroupPattern) throws FileNotFoundException, JDebugException {
        Map<String, String> res = new HashMap<>();
        LineIterator lineIter = new LineIterator(new FileReader(outputDirStr + "/" + fileName));
        long lineNumber = 0;
        while (lineIter.hasNext()) {
            lineNumber++;
            String line = lineIter.nextLine();
            Matcher m = twoGroupPattern.matcher(line);
            if (m.matches() && m.groupCount() == 2) {
                res.put(m.group(1), m.group(2));
            } else {
                throw new JDebugException("!m.matches() && m.groupCount() == 2. file = " + fileName + " line = '" + line + "' ln = " + lineNumber);
            }
        }
        return res;
    }

    private void createThreadData(Map<String, ThreadData> threadsData,
                                String threadId, Map<String, String> threadNames,
                                PrintStream msgFile,
                                String threadEventKind) throws FileNotFoundException {
        ThreadData threadData = threadsData.get(threadId);
        if (threadData == null) {
            int fileNumber = threadsData.size() + 1;
            String fileName = fileNumber + ".txt";
            threadData = new ThreadData(threadId, threadNames.get(threadId),
                    new PrintStream(new File(outputDirStr + "/" + fileName)));
            threadsData.put(threadId, threadData);
            msgFile.println("Create file " + fileName + " for thread " + threadData.getName() +
                            (threadEventKind != null ? threadEventKind : "")
            );
        }
    }

    private void writeForThread(Map<String, ThreadData> threadsData,
                                String threadId,
                                String msg) throws FileNotFoundException {
        threadsData.get(threadId).getPrintStream().println(msg);
    }

    private void push(
            Map<String, ThreadData> threadsData,
            String threadId,
            String classId, String methodId) {
        threadsData.get(threadId).getStack().push(classId + methodId);
    }

    private String pop(Map<String, ThreadData> threadsData, String threadId) {
        return threadsData.get(threadId).getStack().pop();
    }

    private int getStackSize(Map<String, ThreadData> threadsData, String threadId) {
        Stack<String> stack = threadsData.get(threadId.toString()).getStack();
        if (stack == null) {
            return 0;
        }
        return stack.size();
    }

    protected String convertClassNameFromJniToNormal(String jniClassName) {
        Matcher m = arrayPat.matcher(jniClassName);
        if (!m.matches() || m.groupCount() != 1) {
            log.error("!m.matches() || m.groupCount() != 1");
        }
        return m.group(1).replaceAll("/", ".");
    }
}
