package e1;


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ClientesTest {
    Clientes c1, c2;
 
    @Before
    public void setUp() {
        c1 = new Clientes ("Roque", "Mayo", "45961296M", "Avenida de Labañou", 123456789, 123, 100);
        c2 = new Clientes ("Roque", "Mayo", "45961296M", 123, 350);
    
    }
    
    @Test
    public void testToString() {
        assertEquals("O nome do cliente e " + "Roque" + " " + "Mayo" + " con DNI " + "45961296M" + 
                ", direccion: " + "Avenida de Labañou" + ", tlf: " + 123456789 + ", codigo: " + 123 + 
                " ,numero de compras: " + 100 + " e un descuento de " + 1 + "%", c1.toString());
        assertEquals("O nome do cliente e " + "Roque" + " " + "Mayo" + " con DNI " + "45961296M" + 
                ", codigo: " + 123 + " ,numero de compras: " + 350 + " e un descuento de " + 3 + "%", c2.toString());

    } 
}
