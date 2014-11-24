package org.jrivets.mq;

import java.io.IOException;

import org.jrivets.journal.Journal;
import org.jrivets.log.Logger;
import org.jrivets.log.LoggerFactory;
import org.jrivets.util.CloseableLock;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

final class PersistenceQueue {

    private final static int DEFAULT_BUFFER_SIZE = 1024;
    
    private final static int RESET_BUFFER_SIZE_THRESHOLD = 100*DEFAULT_BUFFER_SIZE;
    
    private final CloseableLock writeLock = new CloseableLock();

    private final CloseableLock readLock = new CloseableLock();
    
    private final Output writeBuf = new Output(DEFAULT_BUFFER_SIZE, -1);
    
    private final Input inputBuf = new Input(DEFAULT_BUFFER_SIZE);
    
    private final byte[] metaBuf = new byte[4]; 
    
    private final Kryo kryo = new Kryo();
    
    private final Logger logger;
    
    private final long maxCapacity;
    
    private final Journal journal;

    PersistenceQueue(String name, long maxCapacity, Journal journal) {
        this.logger = LoggerFactory.getLogger(PersistenceQueue.class + "(" + name + ")", null, null);
        this.maxCapacity = maxCapacity;
        this.journal = journal;
    }
    
    <T> void put(T value) throws OverflowException, IOException {
        if (value == null) {
            throw new NullPointerException();
        }
        try (CloseableLock l = writeLock.autounlock()) {
            writeBuf.setPosition(4);
            kryo.writeClassAndObject(writeBuf, value);
            int size = writeBuf.position();
            long available = journal.available();
            if (size + available > maxCapacity) {
                String msg = "Queue overflow, request to write " + size + " bytes, but only available " + (maxCapacity - available);
                logger.warn(msg);
                throw new OverflowException(msg);
            }
            writeBuf.setPosition(0);
            writeBuf.writeInt(size - 4);
            writeBuf.setPosition(size);
            
            logger.debug("Writing value with size=", size, ", value=", (size > 256 ? "(too long...)" : value));
            journal.getOutputStream().write(writeBuf.getBuffer(), 0, size);
            journal.getOutputStream().flush();
        } finally {
            if (writeBuf.position() > RESET_BUFFER_SIZE_THRESHOLD) {
                writeBuf.setBuffer(new byte[DEFAULT_BUFFER_SIZE], -1);
            }
        }
    }

    Object get(long timeoutMs) throws IOException {
        long stopTime = System.currentTimeMillis() + timeoutMs;
        boolean resetPos = true;
        readLock.lock();
        try {
            journal.getInputStream().mark(Integer.MAX_VALUE);
            int size = readSize(stopTime);
            
            if (size < 0 || size > journal.getInputStream().available()) {
                return null;
            }
            if (size > inputBuf.getBuffer().length) {
                inputBuf.setBuffer(new byte[size]);
            }
            int len = journal.getInputStream().read(inputBuf.getBuffer(), 0, size, stopTime - System.currentTimeMillis());
            if (len < size) {
                logger.debug("Cannot read ", size, " bytes, just ", len, " were read");
                return null;
            }
            Object result = kryo.readClassAndObject(inputBuf);
            journal.getInputStream().mark(-1);
            resetPos = false;
            logger.debug("Read object=", (size > 256 ? "(too long...)" : result), ", with size=", size);
            return result;
        } finally {
            if (resetPos) {
                journal.getInputStream().reset();
            }
            if (inputBuf.getBuffer().length > RESET_BUFFER_SIZE_THRESHOLD) {
                inputBuf.setBuffer(new byte[DEFAULT_BUFFER_SIZE]);
            }
            readLock.unlock();
        }
    }

    void terminate() {
        try (CloseableLock l1 = writeLock.autounlock(); CloseableLock l2 = readLock.autounlock()) {
            journal.close();
        }
    }
    
    private int readSize(long stopTime) throws IOException {
        int len = journal.getInputStream().read(metaBuf, 0, 4, stopTime - System.currentTimeMillis());
        if (len < 4) {
            return -1;
        }
        byte[] buf = inputBuf.getBuffer();
        inputBuf.setBuffer(metaBuf);
        int size = inputBuf.readInt();
        inputBuf.setBuffer(buf);
        return size;
    }
}
