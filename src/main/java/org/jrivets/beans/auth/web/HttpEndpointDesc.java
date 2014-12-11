package org.jrivets.beans.auth.web;

import java.util.regex.Pattern;

public class HttpEndpointDesc {

    String method;
    
    Pattern uriRegExp;
    
    public HttpEndpointDesc uriRegExp(String uri) {
        this.uriRegExp = Pattern.compile(uri);
        return this;
    }
    
    public HttpEndpointDesc method(String method) {
        this.method = method.trim().toLowerCase();
        return this;
    }
    
    public boolean matchUri(String uri) {
        return uriRegExp.matcher(uri).matches();
    }
    
    public boolean matchMethod(String method) {
        return this.method.equals("*") || this.method.equals(method);
    }

    @Override
    public String toString() {
        return "{method=" + method + ", uriRegExp=" + uriRegExp + "}";
    }
    
}
