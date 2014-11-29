package org.jrivets.beans;

public interface Service extends LifeCycle {
    
    boolean isAutoStartup();
    
    void start();
    
    void stop();
    
    boolean isStarted();
}
