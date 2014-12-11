package org.jrivets.beans.auth.web;

import java.util.ArrayList;
import java.util.List;

import org.jrivets.log.Logger;
import org.jrivets.log.LoggerFactory;

public final class HttpEndpointChecker {

    private final Logger logger = LoggerFactory.getLogger(HttpEndpointChecker.class);
    
    private List<HttpEndpointDesc> endpoints = new ArrayList<>();
    
    HttpEndpointChecker(String endpoints) {
        parse(endpoints);
    }

    public HttpEndpointDesc match(String method, String uri) {
        if (endpoints.size() == 0) {
            return null;
        }
        method = method.trim().toLowerCase();
        for (HttpEndpointDesc hed: endpoints) {
            if (hed.matchMethod(method) && hed.matchUri(uri)) {
                return hed;
            }
        }
        return null;
    }
    
    private void parse(String endpoints) {
        String[] eps = endpoints.split(",");
        for (String ep: eps) {
            String[] tokens = ep.split(":");
            if (tokens.length != 2) {
                throw new IllegalArgumentException("Wrong endpoint: \"" + ep + "\", expected in format <method>:<url reg exp>");
            }
            HttpEndpointDesc hed = new HttpEndpointDesc().method(tokens[0]).uriRegExp(tokens[1]);
            this.endpoints.add(hed);
            logger.info("New HttpEndpoint: ", hed);
        }
    }
}
