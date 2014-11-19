package org.jrivets.beans.api.rest;

import org.jrivets.beans.api.model.Model;

enum Method {
    /**
     * Generates GET for PropertyAttribute get() operation
     */
    GET,

    /**
     * Generates PUT for PropertyAttribute set() operation, value should be
     * passed in JSON format in the request body
     */
    PUT,

    /**
     * Generates POST for OperationAttribute method invocation. Method params
     * are passed in body as JSON array of strings. complex types should be in
     * JSON format, but escaped to strings, for example:
     * 
     * <pre>
     * [1234, \"ads\", '{\"abc\":123, \"def\":\"str\"}']
     * </pre>
     */
    POST,

    /**
     * Indicates that OperationAttribute method invocation will return an
     * annotated class from the {@link Model}
     */
    RESOURCE
}
