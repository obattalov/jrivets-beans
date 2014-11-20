package org.jrivets.beans.api.rest;

import java.util.List;

final class Resource {
    
    final List<Endpoint> endpoints;

    Resource(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }
    
}
