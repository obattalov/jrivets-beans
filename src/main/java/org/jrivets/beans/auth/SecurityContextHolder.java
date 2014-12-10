package org.jrivets.beans.auth;

public class SecurityContextHolder {

    private static final ThreadLocal<SecurityContext> threadLocal = new ThreadLocal<SecurityContext>();

    public static SecurityContext getContext() {
        return threadLocal.get();
    }

    public static void setContext(SecurityContext context) {
        threadLocal.set(context);
    }
}