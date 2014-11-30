package org.jrivets.beans.guice.web;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jrivets.beans.guice.LifeCycleController;
import org.jrivets.log.Logger;
import org.jrivets.log.LoggerFactory;

import com.google.inject.Singleton;

/**
 * The filter intends to provide init-destroy cycle for
 * {@link LifeCycleController singleton} instance in case it is used in a
 * web-application container. So as the filter is integrated with Guice, the
 * typical use case should be to use it in ServletContextListener (not in
 * web.xml):
 * <p>
 * <code>filter("probably-neve-involved").through(LifeCycleControlFilter.class);</code>.
 * 
 * @author Dmitry Spasibenko
 */
@Singleton
public class LifeCycleControlFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(LifeCycleControlFilter.class);

    @Inject
    private LifeCycleController lcController;

    public void setLifeCycleController(LifeCycleController lcController) {
        this.lcController = lcController;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (lcController == null) {
            logger.error("The filter is not initialized properly: no LifeCycleController instance.", new Exception());
            throw new AssertionError(
                    "The Guice injector is not properly configured, LifeCycleController instance should be available.");
        }
        lcController.init();
    }

    @Override
    public void destroy() {
        lcController.destroy();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
    }
}
