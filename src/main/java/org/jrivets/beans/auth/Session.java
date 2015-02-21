package org.jrivets.beans.auth;

import java.util.UUID;

import org.jrivets.util.UID;

public final class Session {

    final UID id;
    
    final UID entityId;

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

    public UID getEntityId() {
        return entityId;
    }
    
    public UID getId() {
        return id;
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
