package org.jrivets.beans.guice.web;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;

import org.jrivets.beans.guice.LifeCycleController;

import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * Extends {@link GuiceServletContextListener} functionality to control
 * (start/stop) life-cycle components
 * 
 * @author Dmitry Spasibenko
 *
 */
public abstract class JRivetsGuiceServletContextListener extends GuiceServletContextListener {

    @Inject
    private LifeCycleController lcController;

    @Override
    protected final Injector getInjector() {
        Injector injector = getInjector2();
        injector.injectMembers(this);
        return injector;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);
        lcController.init();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        super.contextDestroyed(sce);
        lcController.destroy();
    }

    protected abstract Injector getInjector2();

}
