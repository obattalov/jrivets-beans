package org.jrivets.beans.api.rest;

import org.jrivets.beans.api.model.Attribute;

final class Endpoint {

    final Method method;
    
    final Attribute attribute;
    
    final Endpoint nextEndpoint;

    Endpoint(Method method, Attribute attribute) {
        this(method, attribute, null);
    }
    
    Endpoint(Method method, Attribute attribute, Endpoint nextEndpoint) {
        this.method = method;
        this.attribute = attribute;
        this.nextEndpoint = nextEndpoint;
    }
    
    boolean isTerminal() {
        return method != Method.RESOURCE;
    }
    
}
