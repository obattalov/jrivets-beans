package org.jrivets.beans.guice;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

import org.jrivets.beans.spi.LifeCycle;
import org.jrivets.collection.SortedArray;
import org.jrivets.log.Logger;
import org.jrivets.log.LoggerFactory;
import org.jrivets.util.StaticSingleton;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;

public final class LifeCycleController extends StaticSingleton {
    
    private static Logger logger = LoggerFactory.getLogger(LifeCycleController.class);

    private static Injector injector;
    
    private static SortedArray<LifeCycle> lifeCycles;
    
    public static synchronized void setInjector(Injector injector) {
        if (LifeCycleController.injector != null) {
            throw new AssertionError("Injector can be initialized only once.");
        }
        LifeCycleController.injector = injector;
    }
    
    public static synchronized void init() {
        if (lifeCycles != null) {
            throw new AssertionError("LifeCycleController can be initialized only once.");
        }
        Map<Key<?>, Binding<?>> bindings = injector.getAllBindings();
        lifeCycles = getLifeCycleInstances(bindings.values());
        logger.info("Initializing ", lifeCycles.size(), " instances.");
        for (LifeCycle lc: lifeCycles) {
            logger.info("init phase=", lc.getPhase());
            lc.init();
        }
    }
    
    public static synchronized void destroy() {
        logger.info("Destroying ", lifeCycles.size(), " instances.");
        for (int idx = lifeCycles.size() - 1; idx >= 0; idx--) {
            LifeCycle lc = lifeCycles.get(idx);
            logger.info("destroy phase=", lc.getPhase());
            lc.destroy();
        }
        lifeCycles = null;
    }
    
    static void clear() {
        lifeCycles = null;
        injector = null;
    }
    
    private static SortedArray<LifeCycle> getLifeCycleInstances(Collection<Binding<?>> bindings) {
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

