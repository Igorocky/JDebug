package org.igye.jdebug.messages.impl;

public class LineTableEntry {
    private long lineCodeIndex;
    private int lineNumber;

    public LineTableEntry(long lineCodeIndex, int lineNumber) {
        this.lineCodeIndex = lineCodeIndex;
        this.lineNumber = lineNumber;
    }

    public long getLineCodeIndex() {
        return lineCodeIndex;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
