package org.igye.jdebug;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.igye.jdebug.debugprocessors.tracemethods.DebugProcessorTraceMethods;
import org.igye.jdebug.exceptions.DisconnectedFromRemoteJvm;
import org.igye.jdebug.exceptions.JDebugException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 */
public class JDebug {
    private static Logger log = LoggerFactory.getLogger(JDebug.class);
    private static final int BUFF_LEN = 1024;
    private static final String HANDSHAKE_ANSWER = "JDWP-Handshake";
    private static List<? extends DebugProcessor> debugProcessors =
            Arrays.asList(new DebugProcessorTraceMethods());

    public static void main(String[] args) throws InterruptedException {
        log.info("Start JDebug");
        Socket socket = null;
        Thread msgReaderThread = null;
        Thread msgWriterThread = null;
        try {
            MainParams mainParams = new MainParams();
            JCommander jc = new JCommander(mainParams);
            for (DebugProcessor debugProcessor : debugProcessors) {
                jc.addCommand(debugProcessor.getName(), debugProcessor.getParamsParser());
            }
            try {
                jc.parse(args);
            } catch (ParameterException e) {
                writeErrorToConsoleAndLog(e, e.getMessage());
                jc.usage();
                return;
            }
            if (jc.getParsedCommand() == null) {
                writeErrorToConsoleAndLog("Command not specified");
                jc.usage();
                return;
            }

            MessageReader messageReader = null;
            MessageWriter messageWriter = null;
            if (mainParams.getPort() != null) {
                socket = new Socket(mainParams.getHost(), mainParams.getPort());
                InputStream in = socket.getInputStream();
                OutputStream out = socket.getOutputStream();
                out.write(HANDSHAKE_ANSWER.getBytes());
                out.flush();
                shortPause();
                byte[] buf = new byte[BUFF_LEN];
                int bytesRead = in.read(buf, 0, HANDSHAKE_ANSWER.length());
                if (bytesRead != HANDSHAKE_ANSWER.length()) {
                    throw new JDebugException("bytesRead != HANDSHAKE_ANSWER.length()");
                }
                String ans = new String(buf, 0, HANDSHAKE_ANSWER.length());
                if (!HANDSHAKE_ANSWER.equals(ans)) {
                    throw new JDebugException("!HANDSHAKE_ANSWER.equals(ans)");
                }
                writeInfoToConsoleAndLog("Connected to remote JVM.");

                messageReader = new MessageReader(in);
                msgReaderThread = new Thread(messageReader, "MessageReader");
                msgReaderThread.start();

                messageWriter = new MessageWriter(out);
                msgWriterThread = new Thread(messageWriter, "MessageWriter");
                msgWriterThread.start();
            }

            DebugProcessor proc = null;
            for (DebugProcessor debugProcessor : debugProcessors) {
                if (debugProcessor.getName().equals(jc.getParsedCommand())) {
                    proc = debugProcessor;
                    break;
                }
            }
            if (proc == null) {
                writeErrorToConsoleAndLog("Unknown command: '" + jc.getParsedCommand() + "'");
                jc.usage();
                return;
            }
            proc.init(messageWriter, messageReader, mainParams);
            proc.run();

            if (msgReaderThread != null) {
                msgReaderThread.interrupt();
            }
            if (msgWriterThread != null) {
                msgWriterThread.interrupt();
            }
        } catch (DisconnectedFromRemoteJvm e) {
            log.info(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (msgReaderThread != null && !msgReaderThread.isInterrupted()) {
                msgReaderThread.interrupt();
            }
            if (msgWriterThread != null && !msgWriterThread.isInterrupted()) {
                msgWriterThread.interrupt();
            }
            if (socket != null) {
                try {
                    socket.close();
                    writeInfoToConsoleAndLog("Disconnected from remote JVM");
                } catch (IOException e) {
                    log.error("Error while closing socket.", e);
                }
            }
        }
        log.info("Finish JDebug");
    }

    private static void shortPause() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void writeErrorToConsoleAndLog(Exception e, String msg) {
        if (e != null) {
            log.error(e.getMessage(), e);
        }
        log.error(msg);
        System.out.println("Error: " + msg);
    }

    private static void writeErrorToConsoleAndLog(String msg) {
        writeErrorToConsoleAndLog(null, msg);
    }

    private static void writeInfoToConsoleAndLog(String msg) {
        log.info(msg);
        System.out.println(msg);
    }
}
