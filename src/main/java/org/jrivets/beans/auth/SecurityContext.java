package org.jrivets.beans.auth;

public final class SecurityContext {

    private Session session;

    /**
     * the details could be set in context of the security context invocation.
     * Client cares about details associated with the session for this
     * particular call
     */
    private Object details;

    private boolean authRequired;
    
    public SecurityContext() {
    }

    public Session getSession() {
        return session;
    }
    
    public void setSession(Session session) {
        this.session = session;
    }
    
    @SuppressWarnings("unchecked")
    public <D> D getDetails() {
        return (D) details;
    }

    public <D> void setDetails(D details) {
        this.details = details;
    }
    
    public boolean isAuthRequired() {
        return authRequired;
    }

    public void setAuthRequired(boolean authRequired) {
        this.authRequired = authRequired;
    }

    @Override
    public String toString() {
        return "{session=" + session + ", authRequired=" + authRequired + "}";
    }

}
