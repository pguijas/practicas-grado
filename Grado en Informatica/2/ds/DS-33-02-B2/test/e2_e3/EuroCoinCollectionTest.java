package e2_e3;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class EuroCoinCollectionTest {
    EuroCoin e1_sp1, // Juan Carlos I coin
             e1_sp2, // Felipe VI coin
             e2_sp_2002,  // Juan Carlos I coin of 2002
             e2_sp_2005,  // Juan Carlos I coin of 2005
             c50_it, 
             c20_fr,
             e1_lt,
             e1_lt2,
             c1_pt;
    EuroCoinCollection collection;
    
    
    @Before
    public void setUp() {
        collection = new EuroCoinCollection();
        
        // THIS COINS MUST BE PROPERLY CREATED IN ORDER TO THE TEST TO WORK CORRECTLY
        e1_sp1 = new EuroCoin(Coin.e1, paises.Spain, "Juan Carlos I", 0);
        e1_sp2 = new EuroCoin(Coin.e1, paises.Spain, "Felipe VI", 0);
        e2_sp_2002  = new EuroCoin(Coin.e2, paises.Spain, "Juan Carlos I", 2002);
        e2_sp_2005  = new EuroCoin(Coin.e2, paises.Spain, "Juan Carlos I", 2005);
        c50_it = new EuroCoin(Coin.c50, paises.Italy, null, 0);
        c20_fr = new EuroCoin(Coin.c20, paises.France, null, 0);
        c1_pt = new EuroCoin(Coin.c1, paises.Portugal, null, 0);
        e1_lt = new EuroCoin(Coin.e1, paises.Lithuania, "Rey a", 0);
        e1_lt2 = new EuroCoin(Coin.e1, paises.Lithuania, "Rey b", 0);
       
        assertTrue(collection.insertCoin(e1_sp1));
        assertTrue(collection.insertCoin(e1_sp2));
        assertTrue(collection.insertCoin(e2_sp_2002));
        assertFalse(collection.insertCoin(e2_sp_2005)); // Not inserted
        assertTrue(collection.insertCoin(c50_it));
        assertTrue(collection.insertCoin(c20_fr));
        assertFalse(collection.insertCoin(c50_it)); // Not inserted        
    }
    
    @Test
    public void testNumCoins() {       
        assertEquals(5, collection.numCoins());
    }

    @Test
    public void testHasCoin() {
        assertTrue(collection.hasCoin(e1_sp1));
        assertTrue(collection.hasCoin(e2_sp_2005));
        assertFalse(collection.hasCoin(c1_pt));
    }

    @Test
    public void testCollectionValue() {
        assertEquals(470, collection.value());
    }    
    
    @Test
    public void testRemoveCoin() {
        collection.removeCoin(c50_it);
        assertEquals(4, collection.numCoins());
        assertEquals(420, collection.value());
        
        collection.removeCoin(e2_sp_2005); // removes the 2002 coin
        assertEquals(3, collection.numCoins());
        assertEquals(220, collection.value());
        
        collection.removeCoin(c1_pt); // No coin removed
        assertEquals(3, collection.numCoins());
        assertEquals(220, collection.value());
    }    

    @Test
    public void testComparable_Iterable1() {
        assertTrue(collection.insertCoin(e1_lt));
        assertTrue(collection.insertCoin(e1_lt2));
        collection.sort();
        
        EuroCoinCollection collection2 = new EuroCoinCollection();
        assertTrue(collection2.insertCoin(e2_sp_2002));
        assertTrue(collection2.insertCoin(e1_lt));
        assertTrue(collection2.insertCoin(e1_lt2));
        assertTrue(collection2.insertCoin(e1_sp2));
        assertTrue(collection2.insertCoin(e1_sp1));
        assertTrue(collection2.insertCoin(c50_it));
        assertTrue(collection2.insertCoin(c20_fr));
        
        Iterator<EuroCoin> i = collection.iterator();
        Iterator<EuroCoin> j = collection2.iterator();
        while (i.hasNext() && j.hasNext()) {   
            assertEquals(i.next(), j.next());
        }
        assertTrue(i.hasNext()==j.hasNext());
    }
    
    @Test
    public void testComparable_Iterable2() {
        assertTrue(collection.insertCoin(e1_lt));
        assertTrue(collection.insertCoin(e1_lt2));
        collection.sort(new ComparatorEuroCoin());
        EuroCoinCollection collection2 = new EuroCoinCollection();
        
        assertTrue(collection2.insertCoin(c20_fr));
        assertTrue(collection2.insertCoin(c50_it));
        assertTrue(collection2.insertCoin(e1_lt));
        assertTrue(collection2.insertCoin(e1_lt2));
        assertTrue(collection2.insertCoin(e2_sp_2002));
        assertTrue(collection2.insertCoin(e1_sp2));
        assertTrue(collection2.insertCoin(e1_sp1));
        
        Iterator<EuroCoin> i = collection2.iterator();
        Iterator<EuroCoin> j = collection2.iterator();
        while (i.hasNext() && j.hasNext()) {  
            assertEquals(i.next(), j.next());
        }
        assertTrue(i.hasNext()==j.hasNext());
    }
    

    @Test(expected = IllegalStateException.class)
    public void testIterator() {
        Iterator<EuroCoin> i = collection.iterator();
        assertEquals(i.next(),e1_sp1);
        assertEquals(i.next(),e1_sp2);
        assertEquals(i.next(),e2_sp_2002);
        assertEquals(i.next(),c50_it);
        i.remove();         //excepción
        assertEquals(collection.numCoins(),5);       
        assertNotEquals(i.next(),e1_sp1);
        assertFalse(i.hasNext()); 
        i.remove();
        assertEquals(collection.numCoins(),4);
    }
    
    @Test
    public void testIterator_pais() {
        collection.setPais_it(paises.Austria);
        Iterator<EuroCoin> i = collection.iterator();
        assertFalse(i.hasNext());
        int a=0;
        collection.setPais_it(paises.Spain);
        for (EuroCoin e : collection) {
            a++;
        }
        assertEquals(a, 3);       
        
        i = collection.iterator();
        a=0;
        collection.setPais_it(paises.Italy);
        for (EuroCoin e : collection) {
            a++;
        }
        assertEquals(a, 1);
    } 
  
    @Test(expected = ConcurrentModificationException.class)
    public void testDoThingsWhileIterating1() {
        for (EuroCoin e : collection) {
            collection.insertCoin(c1_pt);
        }
    }   
    
    @Test(expected = ConcurrentModificationException.class)
    public void testDoThingsWhileIterating2() {
        for (EuroCoin e : collection) {
            collection.sort();
        }
    }   
    
    @Test(expected = NoSuchElementException.class)
    public void testIteratorNoSuchElements() {
        Iterator<EuroCoin> i = collection.iterator();
        while (i.hasNext()) { 
            i.next();
        }
        i.next();
    }  

}