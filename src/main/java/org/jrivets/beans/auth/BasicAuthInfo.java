package org.jrivets.beans.auth;

import org.jrivets.util.UID;

public final class BasicAuthInfo {

    private final UID entityId;
    
    private final Object entity;
    
    private final String salt;
    
    private final String hash;

    public BasicAuthInfo(UID entityId, Object entity, String salt, String hash) {
        this.entityId = entityId;
        this.entity = entity;
        this.salt = salt;
        this.hash = hash;
    }

    public UID getEntityId() {
        return entityId;
    }

    public String getSalt() {
        return salt;
    }

    public String getHash() {
        return hash;
    }
    
    public Object getEntity() {
        return entity;
    }

    @Override
    public String toString() {
        return "{entityId=" + entityId + ", entity=" + entity + ", salt=" + salt + ", hash=" + hash + "}";
    }
    
}
