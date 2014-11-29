package org.jrivets.beans.guice.web;

import javax.inject.Inject;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.jrivets.beans.Service;
import org.jrivets.beans.guice.LifeCycleController;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;

/**
 * Extends {@link GuiceFilter} functionality in order to provide support
 * {@link LifeCycle} and {@link Service} interfaces in web-based applications.
 * <p>
 * Please add this filter in web.xml to make {@link LifeCycle} and
 * {@link Service} instances handled properly.
 * 
 * @author Dmitry Spasibenko
 */
public class JbGuiceFilter extends GuiceFilter {

    @Inject
    private Injector injector;

    private LifeCycleController lcController;

    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        lcController = new LifeCycleController(injector);
        lcController.init();
    }

    public void destroy() {
        super.destroy();
        lcController.destroy();
    }
}
