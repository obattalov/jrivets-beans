package org.jrivets.beans.spi;

public abstract class Service implements LifeCycle {
    
    protected boolean started;

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }
    
    public boolean isAutoStartup() {
        return false;
    }
    
    public abstract void start();
    
    public abstract void stop();
    
    public boolean isStarted() {
        return started;
    }
    
    @Override
    public int getPhase() {
        return Integer.MIN_VALUE;
    }    
}
