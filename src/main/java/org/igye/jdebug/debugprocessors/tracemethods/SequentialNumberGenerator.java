package org.igye.jdebug.debugprocessors.tracemethods;

public class SequentialNumberGenerator {
    private static SequentialNumberGenerator instance;
    private long nextNumber;

    private SequentialNumberGenerator() {
    }

    public static SequentialNumberGenerator getInstance() {
        if (instance == null) {
            instance = new SequentialNumberGenerator();
        }
        return instance;
    }

    public synchronized long next() {
        return nextNumber++;
    }
}
