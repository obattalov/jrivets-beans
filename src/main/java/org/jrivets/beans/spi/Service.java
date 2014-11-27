package org.jrivets.beans.spi;

public interface Service extends LifeCycle {
    
    boolean isAutoStartup();
    
    void start();
    
    void stop();
    
    boolean isStarted();
}
