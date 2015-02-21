package org.jrivets.beans.auth;

public final class SecurityContext {

    private final Session session;

    /**
     * the details could be set in context of the security context invocation.
     * Client cares about details associated with the session for this
     * particular call
     */
    private Object details;

    public SecurityContext(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return session;
    }
    
    @SuppressWarnings("unchecked")
    public <D> D getDetails() {
        return (D) details;
    }

    public <D> void setDetails(D details) {
        this.details = details;
    }
    
    @Override
    public String toString() {
        return "{session=" + session + "}";
    }

}
