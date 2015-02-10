package org.jrivets.beans.guice;

import javax.annotation.PostConstruct;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;

import static org.testng.Assert.*;

public class PostConstructModuleTest {

    private Injector injector;
    
    static class A  {
        
        boolean postConstruct;
        
        @PostConstruct
        public void init() {
            postConstruct = true;
        }
    }
    
    static class NA  {
        
        boolean postConstruct;
        
        public void init() {
            postConstruct = true;
        }
    }
    
    static class B  {
        
        @PostConstruct
        public void init(int val) {
        }
    }
    
    @BeforeMethod
    public void init() {
        injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(A.class);
            }        
            
        }, new PostConstructModule());
    }
    
    @Test
    public void normalTest() {
        A a = injector.getInstance(A.class);
        assertTrue(a.postConstruct);
        A a2 = injector.getInstance(A.class);
        assertTrue(a.postConstruct);
        assertNotEquals(a, a2);
    }
    
    @Test
    public void notAnnotatedTest() {
        NA a = injector.getInstance(NA.class);
        assertFalse(a.postConstruct);
    }
    
    @Test(expectedExceptions= {ProvisionException.class})
    public void wrongMethodTest() {
        injector.getInstance(B.class);
    }
    
}
