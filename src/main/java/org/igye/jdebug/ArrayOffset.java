package org.igye.jdebug;

public class ArrayOffset {
    private int offset;

    public ArrayOffset() {
    }

    public ArrayOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public void increase(int delta) {
        offset += delta;
    }
}
