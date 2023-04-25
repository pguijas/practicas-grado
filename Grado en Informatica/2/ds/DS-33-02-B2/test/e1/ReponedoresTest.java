package e1;

import java.util.ConcurrentModificationException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ReponedoresTest {
    Reponedores r1, r2, r3, r4;
    
    @Before
    public void setUp() {
        r2 = new Reponedores ("Roque", "Mayo", "45961296M", "Avenida de Labañou", 123456789, 12345, 1000, Turno.tarde, "Larsa");
        r4 = new Reponedores ("Roque", "Mayo", "45961296M", 12345, 1000, Turno.tarde, "Larsa");
    }
    
    @Test public void testToString() {
        assertEquals("O nome do dependiente e " + "Roque" + " " + "Mayo" + " con DNI " + "45961296M" + ", direccion: " + "Avenida de Labañou" + ", tlf: " + 123456789 + ", numero ss: " + 12345 + ", salario: " + 1000 + ", turno de " + "tarde" + " e da empresa: " + "Larsa", r2.toString());
        assertEquals("O nome do dependiente e " + "Roque" + " " + "Mayo" + " con DNI " + "45961296M"+ ", numero ss: " + 12345 + ", salario: " + 1000 + ", turno de " + "tarde" + " e da empresa: " + "Larsa", r4.toString());
    } 
    
     @Test(expected = IllegalArgumentException.class) public void testNoche(){
        r1 = new Reponedores ("Roque", "Mayo", "45961296M", "Avenida de Labañou", 123456789, 12345, 1000, Turno.noche, "Larsa");
    }
     
}
