package org.jrivets.beans.api.model;

public class MethodInvocationException extends RuntimeException {

    private static final long serialVersionUID = -4926378266534853064L;
    
    MethodInvocationException(String message) {
        super(message);
    }

    MethodInvocationException(String message, Throwable causedBy) {
        super(message, causedBy);
    }
}
