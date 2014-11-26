package org.jrivets.mq;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jrivets.util.CloseableLock;

final class StorageBuffer implements Storage {

    private final Storage storage;
    
    private ByteBuffer buffer;
    
    private CloseableLock lock = new CloseableLock();
    
    StorageBuffer(int size, Storage storage) {
        this.storage = storage;
        buffer = new ByteBuffer(new byte[size]);
        storage.reset();
        storage.get(buffer, 0L);
    }
    
    @Override
    public void put(ByteBuffer bb) {
        try (CloseableLock l = lock.autounlock()) {
            if (bb.available() > buffer.remaining()) {
                flush();
            }
    
            if (bb.available() > buffer.remaining()) {
                storage.put(bb);
                return;
            }
            buffer.put(bb);
        }
    }

    @Override
    public int get(ByteBuffer buf, long timeout) {
        try (CloseableLock l = lock.autounlock()) {
            //TODO: handle timeout here...
            if (storage.available() + buffer.available() < buf.remaining()) {
                return 0;
            }
            
            int len = Math.min(buf.remaining(), storage.available());
            
            if (storage.available() > 0) {
                storage.get(buf, 0);
            }
            if (buf.remaining() > 0) {
                
            }
            return 0;
        }
    }

    @Override
    public void mark() {
        // TODO Auto-generated method stub

    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        storage.close();
    }
    
    private void flush() {
        if (buffer.getMarkPos() > 0) {
            int distanceToMark = buffer.getReadPos() - buffer.getMarkPos();
            buffer.reset();
            storage.put(buffer);
            buffer.clear(distanceToMark);
            while (buffer.remaining() > 0) {
                storage.get(buffer, 0L);
            }
            buffer.clear();
            return;
        }
        storage.put(buffer);
        buffer.clear();
    }

    @Override
    public long available() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long totalAvailable() {
        // TODO Auto-generated method stub
        return 0;
    }
}
