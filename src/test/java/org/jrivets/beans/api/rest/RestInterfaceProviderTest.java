package org.jrivets.beans.api.rest;

import org.jrivets.beans.api.model.TypeScanner;
import org.jrivets.beans.spi.Operation;
import org.jrivets.beans.spi.Property;
import org.testng.annotations.Test;

public class RestInterfaceProviderTest {

    static class Master {
        @Property int i;
        
        private Detail d;
        
        @Operation Detail getDetails(int a) {
            if (a < 0) {
                return null;
            }
            return d == null ? (d = new Detail()) : d;
        }
        @Operation Details2 getDetails2(String b, int a) {
            return new Details2();
        }
        
        @Operation String getData(int i, String s, Detail d) {
            return i + s + d.name;
        }
        
        @Operation String noArgs() {
            return "hello";
        }
    }
    
    static class Detail {
        @Property String name;
        @Operation 
        int setInt(int i, double j) { return 1;}
    }
    
    static class Details2 {
        @Operation Detail getDetails(int a) {
            return new Detail();
        }
    }
    
    @Test
    public void masterDetailTest() {
        TypeScanner ts = new TypeScanner();
        ts.scan(Master.class);
        ts.scan(Detail.class);
        ts.scan(Details2.class);
        RestInterfaceProvider rp = new RestInterfaceProvider(ts.getModel());
        rp.register(new Master(), "master");
        ts.scan(Details2.class);
    }
}
