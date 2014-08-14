package org.igye.jdebug;

import org.igye.jdebug.exceptions.JDebugException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Hello world!
 */
public class JDebug {
    private static Logger log = LoggerFactory.getLogger(JDebug.class);
    private static final int BUFF_LEN = 1024;
    private static final String HANDSHAKE_ANSWER = "JDWP-Handshake";

    public static void main(String[] args) {
        log.info("Start JDebug");
        Socket socket = null;
        try {
            socket = new Socket(args[0], Integer.parseInt(args[1]));
            InputStreamReader in = new InputStreamReader(socket.getInputStream());
            OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream());
            out.write("JDWP-Handshake");
            out.flush();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            char[] buf = new char[BUFF_LEN];
            int bytesRead = in.read(buf, 0, HANDSHAKE_ANSWER.length());
            if (bytesRead != HANDSHAKE_ANSWER.length()) {
                throw new JDebugException("bytesRead != HANDSHAKE_ANSWER.length()");
            }
            String ans = new String(buf, 0, HANDSHAKE_ANSWER.length());
            if (!HANDSHAKE_ANSWER.equals(ans)) {
                throw new JDebugException("!HANDSHAKE_ANSWER.equals(ans)");
            }
            System.out.println("connected!");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (JDebugException e) {
            log.error(e.getMessage(), e);
        } finally {
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
}
