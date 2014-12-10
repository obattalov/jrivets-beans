package org.jrivets.beans.auth;

public final class SecurityContext {

    private final Session session;

    public SecurityContext(Session session) {
        super();
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
    
}
