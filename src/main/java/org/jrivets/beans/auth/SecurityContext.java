package org.jrivets.beans.auth;

public final class SecurityContext {

    private final Session session;

    public SecurityContext(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }

    @Override
    public String toString() {
        return "{session=" + session + "}";
    }
    
}
