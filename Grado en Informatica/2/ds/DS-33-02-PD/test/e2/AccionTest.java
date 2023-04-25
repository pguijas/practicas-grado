package e2;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pguijas
 */
public class AccionTest {
    Accion churreria_chema;
    
    ClienteDetallado ali_josue;
    ClienteSencillo la_patri;
    
    @Test
    public void testClientesDetallado() {
        churreria_chema = new Accion("chch",33,66,11,10000);
        ali_josue = new ClienteDetallado("Ali G");
        
        churreria_chema.addObserver(ali_josue);
        churreria_chema.setCierre(30);
        assertEquals(ali_josue.getUpdates(), 1);
        churreria_chema.setMin(10);
        assertEquals(ali_josue.getUpdates(), 2);
        churreria_chema.setMax(77);
        assertEquals(ali_josue.getUpdates(), 3);
        churreria_chema.setVolumen(10000);
        assertEquals(ali_josue.getUpdates(), 3);
        churreria_chema.setVolumen(10001);
        assertEquals(ali_josue.getUpdates(), 4);
    } 
    
    @Test
    public void testClientesSencillo() {
        churreria_chema = new Accion("chch",33,66,11,10000);
        la_patri = new ClienteSencillo("La Patri");
        
        churreria_chema.addObserver(la_patri);
        churreria_chema.setMin(10);
        assertEquals(la_patri.getUpdates(), 0);
        churreria_chema.setMax(77);
        assertEquals(la_patri.getUpdates(), 0);
        churreria_chema.setCierre(30);
        assertEquals(la_patri.getUpdates(), 1);        
        churreria_chema.setCierre(30);
        assertEquals(la_patri.getUpdates(), 1);
        churreria_chema.setVolumen(10001);
        assertEquals(la_patri.getUpdates(), 1);      
        churreria_chema.setCierre(33);
        assertEquals(la_patri.getUpdates(), 2);
    } 
    
}
