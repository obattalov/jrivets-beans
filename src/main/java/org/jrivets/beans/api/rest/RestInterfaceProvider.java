package org.jrivets.beans.api.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jrivets.beans.api.model.Attribute;
import org.jrivets.beans.api.model.MethodInvoker;
import org.jrivets.beans.api.model.PropertyAttribute;
import org.jrivets.beans.api.model.Model;
import org.jrivets.beans.api.model.OperationAttribute;
import org.jrivets.beans.api.model.TypeDetails;
import org.jrivets.log.Logger;
import org.jrivets.log.LoggerFactory;
import org.jrivets.util.JsonSerializer;

import spark.Request;
import spark.Response;
import spark.ResponseTransformer;
import static spark.Spark.*;

public final class RestInterfaceProvider {

    final Logger logger = LoggerFactory.getLogger(RestInterfaceProvider.class);

    final Model model;
    
    final String resPrefix;

    final Lock lock = new ReentrantLock();

    final Map<Class<?>, Resource> resources = new HashMap<>();

    final Map<String, RootResource> rootResources = new HashMap<>();

    private Set<Class<?>> resourceStack = new HashSet<>();

    private final static JsonTransformer JSON_TRANSFORMER = new JsonTransformer();

    private final static String CT_APP_JSON = "application/json";
    
    private final static int STATUS_OK = 200;
    
    private final static int STATUS_BAD_REQUEST = 400;
    
    private final static int STATUS_NOT_FOUND = 404;
    
    public final static int DEFAULT_PORT = 9009;
    
    public final static String RES_PREFIX = "/management";

    private static class RootResource {

        final Resource resource;

        final Object o;

        RootResource(Resource resource, Object o) {
            this.resource = resource;
            this.o = o;
        }
    }

    private static class EndpointHolder {

        final RootResource rootResource;

        final Endpoint rootEndpoint;

        int argIdx;

        String path;

        EndpointHolder(RootResource rootResource, Endpoint ep, String path) {
            this.rootResource = rootResource;
            this.rootEndpoint = ep;
            this.path = path;
        }
    }

    private static class JsonTransformer implements ResponseTransformer {

        @Override
        public String render(Object model) {
            return JsonSerializer.toJson(model);
        }

    }

    public RestInterfaceProvider(Model model) {
        this(model, DEFAULT_PORT, RES_PREFIX);
    }
    
    public RestInterfaceProvider(Model model, int port, String resourcePrefix) {
        this.model = model;
        this.resPrefix = correctResourcePrefix(resourcePrefix);
        setPort(port);
    }

    public <T> boolean register(T object, String name) {
        lock.lock();
        try {
            name = name.toLowerCase().trim();
            if (rootResources.containsKey(name)) {
                onError("The name \"" + name + "\" has already registered resource.");
            }
            Resource resource = buildResource(object.getClass());
            if (resource == null) {
                return false;
            }
            RootResource rr = new RootResource(resource, object);
            rootResources.put(name, rr);
            registerIntfcForRR(resPrefix + name, rr);
        } finally {
            lock.unlock();
        }
        return true;
    }

    private Resource buildResource(Class<?> clazz) {
        TypeDetails td = model.modelInterface().get(clazz);
        if (td == null) {
            return null;
        }

        Resource resource = resources.get(clazz);
        if (resource != null) {
            return resource;
        }

        if (resourceStack.contains(clazz)) {
            onError("Cyclic dependency detected, a resource refers to another one, the class chain is: "
                    + resourceStack);
        }
        try {
            resourceStack.add(clazz);
            List<Endpoint> endpoints = new ArrayList<Endpoint>(td.getInterfaceAttributes().size());
            for (Map.Entry<String, Attribute> e : td.getInterfaceAttributes().entrySet()) {
                buildEndpoints(e.getKey(), e.getValue(), endpoints);
            }
            resource = new Resource(endpoints);
            resources.put(clazz, resource);
        } finally {
            resourceStack.remove(clazz);
        }
        return resource;
    }

    private void buildEndpoints(String name, Attribute a, List<Endpoint> endpoints) {
        if (a instanceof PropertyAttribute) {
            buildPropEndpoints((PropertyAttribute) a, endpoints);
            return;
        }

        OperationAttribute oa = (OperationAttribute) a;
        Resource retResource = buildResource(oa.getMethod().getReturnType());
        if (retResource == null) {
            endpoints.add(new Endpoint(Method.POST, oa));
            return;
        }
        for (Endpoint chldEndpoint : retResource.endpoints) {
            endpoints.add(new Endpoint(Method.RESOURCE, oa, chldEndpoint));
        }
    }

    private void buildPropEndpoints(PropertyAttribute a, List<Endpoint> endpoints) {
        endpoints.add(new Endpoint(Method.GET, a));
        endpoints.add(new Endpoint(Method.PUT, a));
    }

    private void onError(String msg) {
        logger.error(msg);
        throw new IllegalArgumentException(msg);
    }

    private void registerIntfcForRR(String rootPath, RootResource rr) {
        for (Endpoint ep : rr.resource.endpoints) {
            registerIntfcForEP(new EndpointHolder(rr, ep, rootPath), ep);
        }
    }

    private void registerIntfcForEP(EndpointHolder eph, Endpoint ep) {
        eph.path += "/" + ep.attribute.getName().toLowerCase();
        switch (ep.method) {
        case GET:
            logger.info("register GET ", eph.path);
            get(eph.path, CT_APP_JSON, (req, res) -> invoke(req, res, eph.rootResource, eph.rootEndpoint),
                    JSON_TRANSFORMER);
            break;
        case PUT:
            logger.info("register PUT ", eph.path);
            put(eph.path, CT_APP_JSON, (req, res) -> invoke(req, res, eph.rootResource, eph.rootEndpoint),
                    JSON_TRANSFORMER);
            break;
        case POST:
            logger.info("register POST ", eph.path);
            post(eph.path, CT_APP_JSON, (req, res) -> invoke(req, res, eph.rootResource, eph.rootEndpoint),
                    JSON_TRANSFORMER);
            break;
        case RESOURCE:
            java.lang.reflect.Method m = ((OperationAttribute) ep.attribute).getMethod();
            for (int idx = 0; idx < m.getParameterCount(); idx++) {
                eph.path += "/:" + getArgName(idx + eph.argIdx);
            }
            eph.argIdx += m.getParameterCount();
            registerIntfcForEP(eph, ep.nextEndpoint);
            break;
        }
    }

    private String getArgName(int idx) {
        return "arg" + idx;
    }
  
    private Object invoke(Request req, Response res, RootResource rootResource, Endpoint rootEndpoint) {
        logger.info(req.requestMethod(), " to ", req.url());
        logger.debug("body=", req.body());
        int argIdx = 0;
        Object o = rootResource.o;
        while (!rootEndpoint.isTerminal()) {
            java.lang.reflect.Method m = ((OperationAttribute) rootEndpoint.attribute).getMethod();
            String[] params = getPathParams(req, m.getParameterCount(), argIdx);
            argIdx += m.getParameterCount();
            o = MethodInvoker.invoke(o, m, params);
            if (o == null) {
                logger.warn("No object in the chain when calling ", m.getName(), "() with params ", Arrays.toString(params));
                halt(STATUS_NOT_FOUND, "Empty object when invoking method " + m.getName() + "() with args=" + Arrays.toString(params));
            }
            rootEndpoint = rootEndpoint.nextEndpoint;
        }

        switch (rootEndpoint.method) {
        case GET:
            return ((PropertyAttribute) rootEndpoint.attribute).get(o);
        case PUT:
            ((PropertyAttribute) rootEndpoint.attribute).set(o, req.body());
            res.status(STATUS_OK);
            return "";
        case POST: 
            java.lang.reflect.Method m = ((OperationAttribute) rootEndpoint.attribute).getMethod();
            String[] params = getPostParams(req.body());
            o = MethodInvoker.invoke(o, m, params);
            return o;
        default: break;
        }
        return null;
    }

    private String[] getPathParams(Request req, int count, int startIdx) {
        String[] params = new String[count];
        try {
            for (int idx = 0; idx < count; idx++) {
                params[idx] = req.params(getArgName(startIdx++));
            }
        } catch (Exception e) {
            halt(STATUS_BAD_REQUEST, "Cannot parse path parameters " + e);
        }
        return params;
    }
    
    private String[] getPostParams(String body) {
        try {
            String[] result = JsonSerializer.fromJson(body, String[].class);
            return result == null ? new String[0] : result;
        } catch (Exception e) {
            halt(STATUS_BAD_REQUEST, "Cannot parse POST parameters " + e);
        }
        return null;
    }

    private String correctResourcePrefix(String resourcePrefix) {
        if (resourcePrefix == null) {
            resourcePrefix = "";
        }
        return resourcePrefix + "/";
    }
}
