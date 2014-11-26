package org.jrivets.mq;

interface Storage {

    void put(ByteBuffer bb);
    
    int get(ByteBuffer bb, long timeout);
    
    long available(); // for read
    
    long totalAvailable(); // marked included
    
    void mark();
    
    void reset();
    
    void close();
    
}
