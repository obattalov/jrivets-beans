package org.jrivets.beans.spi;

public interface LifeCycle {

    void init();
    
    void destroy();
    
    int getPhase();
    
}
