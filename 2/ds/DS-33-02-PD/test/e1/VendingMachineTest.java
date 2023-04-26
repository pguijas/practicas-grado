package e1;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author pguijas
 */
public class VendingMachineTest {
    VendingMachine maquina;
    
    EuroCoin e1_sp1, // Juan Carlos I coin
        e2_sp_2005,  // Juan Carlos I coin of 2005
        c50_it, 
        c20_fr,
        c1_pt;
    
    @Before
    public void before() {
        e1_sp1 = new EuroCoin(Coin.e1, paises.Spain, "Juan Carlos I", 0);
        e2_sp_2005  = new EuroCoin(Coin.e2, paises.Spain, "Juan Carlos I", 2005);
        c50_it = new EuroCoin(Coin.c50, paises.Italy, null, 0);
        c20_fr = new EuroCoin(Coin.c20, paises.France, null, 0);
        c1_pt = new EuroCoin(Coin.c1, paises.Portugal, null, 0);
        
        
        maquina = new VendingMachine();
        maquina.insertProduct("coca", 129);
        maquina.insertCoin(e1_sp1);
        maquina.insertCoin(e1_sp1);
        maquina.insertCoin(e1_sp1);
    } 
    
    @Test
    public void testCambioSimple() {
        List<EuroCoin> cambio_supuesto = new ArrayList<>();
        cambio_supuesto.add(e1_sp1);
        
        maquina.setAlgoritmo(new cambioSimple());
        assertEquals(cambio_supuesto, maquina.buy("coca"));
        
    } 
    
    @Test
    public void testCambioDeposito() {
        List<EuroCoin> cambio_supuesto = new ArrayList<>();
        cambio_supuesto.add(e1_sp1);
        cambio_supuesto.add(c50_it);
        cambio_supuesto.add(c20_fr);
        cambio_supuesto.add(c1_pt);
        
        maquina.setAlgoritmo(new cambioDeposito());
        assertEquals(cambio_supuesto, maquina.buy("coca"));
    } 
    
    @Test
    public void testCancel() {
        List<EuroCoin> cambio_supuesto = new ArrayList<>();
        cambio_supuesto.add(e1_sp1);
        cambio_supuesto.add(e1_sp1);
        cambio_supuesto.add(e1_sp1);
       
        
        assertEquals(cambio_supuesto, maquina.cancel());
    } 
    
}
