package org.jrivets.beans.auth;

import org.jrivets.util.UID;

public final class BasicAuthInfo {

    private final UID entityId;
    
    private final String salt;
    
    private final String hash;

    public BasicAuthInfo(UID entityId, String salt, String hash) {
        this.entityId = entityId;
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

    @Override
    public String toString() {
        return "{entityId=" + entityId + ", salt=" + salt + ", hash=" + hash + "}";
    }
    
}
