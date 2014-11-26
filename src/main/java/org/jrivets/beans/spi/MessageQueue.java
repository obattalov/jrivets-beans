package org.jrivets.beans.spi;

import org.jrivets.beans.mq.OverflowException;

public interface MessageQueue {

    <T> void put(T value) throws OverflowException;

    Object get(long timeoutMs);

    /**
     * Terminates the queue content and release all resources borrowed for the
     * queue purposes
     */
    void terminate();
}
