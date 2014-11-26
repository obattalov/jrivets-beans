package org.jrivets.mq;

import java.nio.BufferOverflowException;
import java.nio.InvalidMarkException;

final class ByteBuffer {

    private final int capacity;
    
    private int mark;
    
    private int readPos;
    
    private int writePos;
    
    private int limit;
    
    private byte[] buffer;
    
    public ByteBuffer(byte[] buffer) {
        this.capacity = buffer.length;
        this.buffer = buffer;
        clear();
    }
    
    public int available() {
        return writePos - readPos;
    }
    
    public int remaining() {
        return limit - writePos;
    }
    
    public void clear() {
        clear(capacity);
    }
    
    public void clear(int limit) {
        if (limit < 0 || limit > capacity) {
            throw new IllegalArgumentException("The limit should be in between 0 and " + capacity);
        }
        readPos = 0;
        writePos = 0;
        mark = -1;
        this.limit = limit;
    }
    
    public int getMarkPos() {
        return mark;
    }
    
    public int getReadPos() {
        return readPos;
    }
    
    public void reset() {
        if (mark < 0) {
            throw new InvalidMarkException();
        }
        readPos = mark;
    }
    
    public void put(ByteBuffer bb) {
        if (bb.available() > remaining()) {
            throw new BufferOverflowException();
        }
        int len = bb.available();
        System.arraycopy(bb.buffer, bb.readPos, buffer, writePos, len);
        bb.readPos += len;
        writePos += len;
    }
    
    public int putRemaining(ByteBuffer bb) {
        int len = Math.min(bb.available(), remaining());
        System.arraycopy(bb.buffer, bb.readPos, buffer, writePos, len);
        bb.readPos += len;
        writePos += len;
        return len;
    }
}
