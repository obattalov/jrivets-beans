package org.jrivets.beans.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jrivets.util.UID;

public final class Session {

    final UID id;
    
    final UID entityId;
    
    final Map<String, Object> attributes = new HashMap<>();

    Session(UID entityId) {
        this(new UID(UUID.randomUUID()), entityId);
    }
    
    Session(UID id, UID entityId) {
        if (id == null) {
            throw new NullPointerException("Session id cannot be null");
        }
        this.id = id;
        this.entityId = entityId;
    }

    public UID getId() {
        return id;
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
        return "{id=" + id + ", entityId=" + entityId + "}";
    }
}
