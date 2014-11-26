package org.jrivets.beans.guice;

import javax.inject.Singleton;

import org.jrivets.beans.spi.LifeCycle;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

import static org.testng.Assert.*;

public class LifeCycleControllerTest {
    
    private static int phase = 0;
    
    private static int inits = 0;
    
    private static int destroys = 0;
    
    private Injector injector;

    interface C1 {};
    
    @Singleton
    static class C1Impl implements C1, LifeCycle {
        @Override
        public void init() {
            assertEquals(0, LifeCycleControllerTest.phase);
            LifeCycleControllerTest.phase = 1;
            LifeCycleControllerTest.inits++;
        }

        @Override
        public void destroy() {
            assertEquals(1, LifeCycleControllerTest.phase);
            LifeCycleControllerTest.phase = 0;
            LifeCycleControllerTest.destroys++;
        }

        @Override
        public int getPhase() {
            return 1;
        }
    }
    
    interface C2 {};
    
    static class C2Impl implements C2, LifeCycle {
        @Override
        public void init() {
            fail();
        }

        @Override
        public void destroy() {
            fail();
        }

        @Override
        public int getPhase() {
            return 121;
        }
    }
    
    interface C3 {};
    
    static class C3Impl implements C3, LifeCycle {
        @Override
        public void init() {
            assertEquals(1, LifeCycleControllerTest.phase);
            LifeCycleControllerTest.phase = 2;
            LifeCycleControllerTest.inits++;
        }

        @Override
        public void destroy() {
            assertEquals(2, LifeCycleControllerTest.phase);
            LifeCycleControllerTest.phase = 1;
            LifeCycleControllerTest.destroys++;
        }

        @Override
        public int getPhase() {
            return 2;
        }
    }

    interface C4 {};
    
    static class C4Impl implements C4, LifeCycle {
        @Override
        public void init() {
            assertEquals(2, LifeCycleControllerTest.phase);
            LifeCycleControllerTest.phase = 3;
            LifeCycleControllerTest.inits++;
        }

        @Override
        public void destroy() {
            assertEquals(3, LifeCycleControllerTest.phase);
            LifeCycleControllerTest.phase = 2;
            LifeCycleControllerTest.destroys++;
        }

        @Override
        public int getPhase() {
            return 3;
        }
    }

    
    @BeforeMethod
    public void init() {
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(C1.class).to(C1Impl.class);
                bind(C2.class).to(C2Impl.class);
                bind(C3Impl.class).in(Singleton.class);
                bind(C3.class).to(C3Impl.class);
            }        
            
            @Provides 
            @Singleton
            C4 getC4Instance() {
                return new C4Impl();
            }
            
        });
        LifeCycleControllerTest.destroys = 0;
        LifeCycleControllerTest.inits = 0;
        LifeCycleControllerTest.phase = 0;
    }
    
    @AfterMethod
    public void clean() {
        LifeCycleController.clear();
    }
    
    @Test(expectedExceptions={NullPointerException.class})
    public void noInjector() {
        LifeCycleController.init();
    }
    
    @Test(expectedExceptions={AssertionError.class})
    public void doubleInjector() {
        LifeCycleController.setInjector(injector);
        LifeCycleController.setInjector(injector);
    }
    
    @Test(expectedExceptions={AssertionError.class})
    public void doubleInit() {
        LifeCycleController.setInjector(injector);
        LifeCycleController.init();
        LifeCycleController.init();
    }
    
    @Test
    public void commonCycle() {
        LifeCycleController.setInjector(injector);
        LifeCycleController.init();
        assertEquals(phase, 3);
        LifeCycleController.destroy();
        assertEquals(destroys, 3);
        assertEquals(inits, 3);
    }
}
