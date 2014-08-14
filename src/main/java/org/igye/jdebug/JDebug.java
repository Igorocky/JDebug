package org.igye.jdebug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class JDebug {
    private static Logger log = LoggerFactory.getLogger(JDebug.class);

    public static void main(String[] args) {
        log.info("Start JDebug");
        System.out.println("Hello!");
        log.info("Finish JDebug");
    }
}
