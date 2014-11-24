package org.jrivets.mq;

import java.io.IOException;

public interface MessageQueue {

    <T> void put(T value) throws OverflowException, IOException;

    Object get(long timeoutMs) throws IOException;

    /**
     * Terminates the queue content and release all resources borrowed for the
     * queue purposes
     */
    void terminate();
}
