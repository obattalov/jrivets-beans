package org.jrivets.beans;

public interface LifeCycle {

    void init();
    
    void destroy();
    
    int getPhase();
    
}
