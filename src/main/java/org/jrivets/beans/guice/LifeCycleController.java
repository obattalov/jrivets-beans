package org.jrivets.beans.guice;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import org.jrivets.beans.spi.LifeCycle;
import org.jrivets.beans.spi.AbstractService;
import org.jrivets.collection.SortedArray;
import org.jrivets.log.Logger;
import org.jrivets.log.LoggerFactory;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;

public final class LifeCycleController {
    
    private static class LifeCycleControllerHolder {
        
        final static LifeCycleController lcContoller = new LifeCycleController();
        
    }
    
    private final Logger logger = LoggerFactory.getLogger(LifeCycleController.class);

    private Injector injector;
    
    private SortedArray<LifeCycle> lifeCycles;
    
    private LifeCycleController() {
        
    }
    
    public static synchronized void setInjector(Injector injector) {
        if (inst().injector != null) {
            throw new AssertionError("Injector can be initialized only once.");
        }
        inst().injector = injector;
    }
    
    public static void init() {
        inst().onInit();
    }
    
    public static void destroy() {
        inst().onDestroy();
    }

    static LifeCycleController inst() {
        return LifeCycleControllerHolder.lcContoller;
    }
    
    static void clear() {
        inst().lifeCycles = null;
        inst().injector = null;
    }
    
    private synchronized void onInit() {
        if (lifeCycles != null) {
            throw new AssertionError("LifeCycleController can be initialized only once.");
        }
        Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();
        lifeCycles = getLifeCycleInstances(bindings.values());
        initLifeCycles();
        startServices();
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
    
    private synchronized void onDestroy() {
        stopServices();
        destroyLifeCycles();
        lifeCycles = null;
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

