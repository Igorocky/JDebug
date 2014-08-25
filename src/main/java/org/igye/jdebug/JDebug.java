package org.igye.jdebug;

import org.igye.jdebug.debugprocessors.DebugProcessorTraceMethods;
import org.igye.jdebug.exceptions.JDebugException;
import org.igye.jdebug.messages.JdwpMessage;
import org.igye.jdebug.messages.core.ReplyPacket;
import org.igye.jdebug.messages.impl.VersionCommand;
import org.igye.jdebug.messages.impl.VersionReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * Hello world!
 */
public class JDebug {
    private static Logger log = LoggerFactory.getLogger(JDebug.class);
    private static final int BUFF_LEN = 1024;
    private static final String HANDSHAKE_ANSWER = "JDWP-Handshake";

    public static void main(String[] args) throws InterruptedException {
        log.info("Start JDebug");

//        int i = 500;
//        System.out.println(Integer.toHexString(i));
//        for (byte b : ByteArrays.intToBigEndianByteArray(i)) {
//            System.out.format("0x%x ", b);
//
//        }
//        System.out.println(ByteArrays.fourByteArrayToLong(ByteArrays.intToBigEndianByteArray(4895)));
//        System.out.println("====================================");
//        System.out.println(StringUtils.leftPad(Integer.toBinaryString(10), 32, "0"));
//        for (int i = 0; i < 34; i++) {
//            System.out.println(StringUtils.leftPad(i + "", 3, ' ') + ": " + StringUtils.leftPad(Integer.toBinaryString(-1 << i), 32, "0"));
//        }

//        int v = 0b11111111111111111111111111111111;
//        System.out.println( v + " = " + StringUtils.leftPad(Integer.toBinaryString(v), 8, "0"));
//        System.out.println( ((int)((byte)v)) + " = " + StringUtils.leftPad(Integer.toBinaryString(((int)((byte)v))), 8, "0"));

//        System.out.println("====================================");


        Socket socket = null;
        Thread msgReaderThread = null;
        Thread msgWriterThread = null;
        try {
            socket = new Socket(args[0], Integer.parseInt(args[1]));
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            out.write("JDWP-Handshake".getBytes());
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
            System.out.println("Connected.");

            MessageReader messageReader = new MessageReader(in);
            msgReaderThread = new Thread(messageReader);
            msgReaderThread.start();

            MessageWriter messageWriter = new MessageWriter(out);
            msgWriterThread = new Thread(messageWriter);
            msgWriterThread.start();

            DebugProcessor proc = new DebugProcessorTraceMethods();
            proc.setWriterAndReader(messageWriter, messageReader);
            proc.run();

            msgReaderThread.interrupt();
            msgWriterThread.interrupt();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (JDebugException e) {
            log.error(e.getMessage(), e);
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
}
