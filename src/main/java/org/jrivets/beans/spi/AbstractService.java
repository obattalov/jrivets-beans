package org.jrivets.beans.spi;

public abstract class AbstractService implements Service {
    
    protected boolean started;

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }
    
    @Override
    public boolean isAutoStartup() {
        return false;
    }
    
    @Override
    public abstract void start();
    
    @Override
    public abstract void stop();
    
    @Override
    public boolean isStarted() {
        return started;
    }
    
    @Override
    public int getPhase() {
        return Integer.MIN_VALUE;
    }    
}
