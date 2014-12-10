package org.jrivets.beans.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jrivets.util.UID;

public final class Session {

    final UID id;
    
    final UID entityId;
    
    final Map<String, Object> attributes = new HashMap<>();
    
    final long expirationTimeout;
    
    long lastTouchTime;

    Session(UID entityId, long expirationTimeout, long lastTouchTime) {
        this(new UID(UUID.randomUUID()), entityId, expirationTimeout, lastTouchTime);
    }
    
    Session(UID id, UID entityId, long expirationTimeout, long lastTouchTime) {
        if (id == null) {
            throw new NullPointerException("Session id cannot be null");
        }
        this.id = id;
        this.entityId = entityId;
        this.expirationTimeout = expirationTimeout;
        this.lastTouchTime = lastTouchTime;
    }

    public long getLastTouchTime() {
        return lastTouchTime;
    }

    public void setLastTouchTime(long lastTouchTime) {
        this.lastTouchTime = lastTouchTime;
    }

    public UID getId() {
        return id;
    }

    public long getExpirationTimeout() {
        return expirationTimeout;
    }
    
    public long getExpirationTime() {
        return lastTouchTime + expirationTimeout;
    }

    public void setAttribute(String name, Object attr) {
        attributes.put(name, attr);
    }
    
    public Object getAttribute(String name) {
        return attributes.get(name);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Session) {
            return id.equals(((Session) obj).id);
        }
        return false;
    }

    @Override
    public String toString() {
        return "{id=" + id + ", entityId=" + entityId + ", expirationTimeout=" + expirationTimeout
                + ", lastTouchTime=" + lastTouchTime + "}";
    }
}
