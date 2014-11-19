package org.jrivets.beans.api.rest;

import org.jrivets.beans.Operation;
import org.jrivets.beans.Property;
import org.jrivets.beans.api.model.TypeScanner;
import org.testng.annotations.Test;

public class RestInterfaceProviderTest {

    static class Master {
        @Property int i;
        @Operation Detail getDetails(int a) {
            return new Detail();
        }
        @Operation Details2 getDetails2(String b, int a) {
            return new Details2();
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
