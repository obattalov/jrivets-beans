package org.jrivets.beans.guice;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jrivets.beans.AbstractService;
import org.jrivets.beans.LifeCycle;
import org.jrivets.collection.SortedArray;
import org.jrivets.log.Logger;
import org.jrivets.log.LoggerFactory;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;

@Singleton
public final class LifeCycleController {
        
    private final Logger logger = LoggerFactory.getLogger(LifeCycleController.class);

    private final Injector injector;
    
    private SortedArray<LifeCycle> lifeCycles;
    
    @Inject
    public LifeCycleController(Injector injector) {
        logger.info("New instance.");
        this.injector = injector;
    }
    
    public synchronized void init() {
        if (lifeCycles != null) {
            throw new AssertionError("LifeCycleController can be initialized only once.");
        }
        Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();
        lifeCycles = getLifeCycleInstances(bindings.values());
        initLifeCycles();
        startServices();
    }
    
    public synchronized void destroy() {
        stopServices();
        destroyLifeCycles();
        lifeCycles = null;
    }

    private void initLifeCycles() {
        logger.info("Initializing ", lifeCycles.size(), " instances.");
        for (LifeCycle lc: lifeCycles) {
            logger.info("init phase=", lc.getPhase());
            lc.init();
        }
    }
    
    private void startServices() {
        logger.info("Starting services.");
        for (LifeCycle lc: lifeCycles) {
            if (lc instanceof AbstractService) {
                AbstractService s = (AbstractService) lc;
                if (s.isAutoStartup()) {
                    s.start();
                }
            }
        }
    }
    
    private void stopServices() {
        logger.info("Stop services");
        for (int idx = lifeCycles.size() - 1; idx >= 0; idx--) {
            LifeCycle lc = lifeCycles.get(idx);
            if (lc instanceof AbstractService) {
                AbstractService s = (AbstractService) lc;
                if (s.isStarted()) {
                    s.stop();
                }
            }
        }
    }
    
    private void destroyLifeCycles() {
        logger.info("Destroying ", lifeCycles.size(), " instances.");
        for (int idx = lifeCycles.size() - 1; idx >= 0; idx--) {
            LifeCycle lc = lifeCycles.get(idx);
            logger.info("destroy phase=", lc.getPhase());
            lc.destroy();
        }
    }
    
    private SortedArray<LifeCycle> getLifeCycleInstances(Collection<Binding<?>> bindings) {
        IsSingletonBindingScopingVisitor singletonVisitor = new IsSingletonBindingScopingVisitor();
        IsSpecificTypeTargetVisitor lifeCycleVisitor = new IsSpecificTypeTargetVisitor(LifeCycle.class, injector);
        SortedArray<LifeCycle> result = new SortedArray<LifeCycle>(new Comparator<LifeCycle>() {
            @Override
            public int compare(LifeCycle o1, LifeCycle o2) {
                return o1.getPhase() - o2.getPhase();
            }
        }, 10);
        for (Binding<?> binding: bindings) {
            if (binding.acceptScopingVisitor(singletonVisitor) && binding.acceptTargetVisitor(lifeCycleVisitor)) {
                result.add((LifeCycle) injector.getInstance(binding.getKey()));
            }
        }
        return result;
    }
}

