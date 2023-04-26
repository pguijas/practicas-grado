package e2;

import org.junit.Test;
import static org.junit.Assert.*;

public class PolymersTest {
    
    @Test
    public void testProcessPolymer() {
        assertEquals("rFb", Polymers.processPolymer("rFvdAaDVb"));
        assertEquals("derEReeCEcE", Polymers.processPolymer("dedDrEReeChHEcE"));
        assertEquals("ddaDaChAHc", Polymers.processPolymer("ddaDrRaChAHc"));        
        assertEquals("dabCBAcaDA", Polymers.processPolymer("dabAcCaCBAcCcaDA"));
        assertEquals("", Polymers.processPolymer("abBA"));
        assertEquals("", Polymers.processPolymer("abCDezyXxYZEdcBA"));        
        assertEquals("aabAAB", Polymers.processPolymer("aabAAB"));
        assertEquals("dbCBcD", Polymers.processPolymer("dbcCCBcCcD"));
        assertEquals("daCAcaDA", Polymers.processPolymer("daAcCaCAcCcaDA"));        
        assertEquals("daDA", Polymers.processPolymer("dabAaBAaDA"));
        assertEquals("abCBAc", Polymers.processPolymer("abAcCaCBAcCcaA"));
        assertEquals("", Polymers.processPolymer(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProcessPolymerNull() {
        assertEquals("", Polymers.processPolymer(null));
    }

    @Test
    public void testMinProcessedPolymer() {
        assertEquals('e', Polymers.minProcessedPolymer("dedaDrERaeeChAHEcEF"));
        assertEquals('c', Polymers.minProcessedPolymer("dabAcCaCBAcCcaDA"));        
        assertEquals('a', Polymers.minProcessedPolymer("caCApAPa"));    
        assertEquals('f', Polymers.minProcessedPolymer("ffffff"));  
    }    
    
    @Test(expected = IllegalArgumentException.class)
    public void testMinProcessedPolymerNull() {
        assertEquals("", Polymers.minProcessedPolymer(null));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMinProcessedPolymerEmpty() {
        assertEquals("", Polymers.minProcessedPolymer(""));
    }
}
