package e1;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class DependientesTest {
    Dependientes d1, d2, d3, d4;
    
    @Before
    public void setUp() {
        d1 = new Dependientes ("Roque", "Mayo", "45961296M", "Avenida de Labañou", 123456789, 12345, 1000, Turno.noche, "carniceria");
        d2 = new Dependientes ("Roque", "Mayo", "45961296M", "Avenida de Labañou", 123456789, 12345, 1000, Turno.tarde, "carniceria");
        d3 = new Dependientes ("Roque", "Mayo", "45961296M", 12345, 1000, Turno.tarde, "carniceria");
        d4 = new Dependientes ("Roque", "Mayo", "45961296M", 12345, 1000, Turno.noche, "carniceria");
    }

    @Test public void testToString() {
        assertEquals("O nome do dependiente e " + "Roque" + " " + "Mayo" + " con DNI " + "45961296M" + ", direccion: " + "Avenida de Labañou" + ", tlf: " + 123456789 + ", numero ss: " + 12345 + ", salario: " + 1150 + ", turno de " + "noche" + " e especialidade: " + "carniceria", d1.toString());
        assertEquals("O nome do dependiente e " + "Roque" + " " + "Mayo" + " con DNI " + "45961296M" + ", direccion: " + "Avenida de Labañou" + ", tlf: " + 123456789 + ", numero ss: " + 12345 + ", salario: " + 1000 + ", turno de " + "tarde" + " e especialidade: " + "carniceria", d2.toString());
        assertEquals("O nome do dependiente e " + "Roque" + " " + "Mayo" + " con DNI " + "45961296M"+ ", numero ss: " + 12345 + ", salario: " + 1000 + ", turno de " + "tarde" + " e especialidade: " + "carniceria", d3.toString());
        assertEquals("O nome do dependiente e " + "Roque" + " " + "Mayo" + " con DNI " + "45961296M"+ ", numero ss: " + 12345 + ", salario: " + 1150 + ", turno de " + "noche" + " e especialidade: " + "carniceria", d4.toString());
    } 
}
