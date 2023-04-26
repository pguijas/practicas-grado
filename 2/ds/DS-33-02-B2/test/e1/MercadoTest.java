
package e1;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.ArrayList;

public class MercadoTest {

    Mercado m1;
    Clientes c1, c2;
    Dependientes d1, d2;
    Reponedores r1, r2;

    @Before
    public void setUp() {
        m1 = new Mercado();
        c1 = new Clientes("Roque", "Mayo", "45961296M", "Avenida de Labañou", 123456789, 123, 100);
        c2 = new Clientes("Roque", "Mayo", "45961296M", 123, 100);
        d1 = new Dependientes("Roque", "Mayo", "45961296M", "Avenida de Labañou", 123456789, 12345, 1000, Turno.noche, "carniceria");
        d2 = new Dependientes("Roque", "Mayo", "45961296M", 12345, 1000, Turno.tarde, "carniceria");
        r1 = new Reponedores("Roque", "Mayo", "45961296M", "Avenida de Labañou", 123456789, 12345, 1000, Turno.tarde, "Larsa");
        r2 = new Reponedores("Roque", "Mayo", "45961296M", 12345, 1000, Turno.tarde, "Larsa");

    }

    @Test
    public void testAgregarCliente() {
        m1.agregarCliente(c1);
        m1.agregarCliente(c2);
        assertEquals(2, m1.listaClientes.size());
    }

    @Test
    public void testAgregarEmpleado() {
        m1.agregarEmpleado(d1);
        m1.agregarEmpleado(d2);
        m1.agregarEmpleado(r1);
        m1.agregarEmpleado(r2);
        assertEquals(4, m1.listaEmpleados.size());
    }

    @Test
    public void testAgregarClientes() {
        ArrayList<Clientes> listaC = new ArrayList();
        listaC.add(c1);
        listaC.add(c2);
        m1.agregarClientes(listaC);
        assertEquals(2, m1.listaClientes.size());
    }

    @Test
    public void testAgregarEmpleados() {
        ArrayList<Trabajador> listaT = new ArrayList();
        listaT.add(d1);
        listaT.add(d2);
        listaT.add(r1);
        listaT.add(r2);
        m1.agregarEmpleados(listaT);
        assertEquals(4, m1.listaEmpleados.size());

    }

    @Test
    public void testSalariosMercado() {
        ArrayList<Trabajador> listaT = new ArrayList();
        listaT.add(d1);
        listaT.add(d2);
        listaT.add(r1);
        listaT.add(r2);
        m1.agregarEmpleados(listaT);
        assertEquals(4150, m1.salariosMercado());
    }

    @Test
    public void testPersonasMercado() {
        ArrayList<Clientes> listaC = new ArrayList();
        listaC.add(c1);
        listaC.add(c2);
        m1.agregarClientes(listaC);
        ArrayList<Trabajador> listaT = new ArrayList();
        listaT.add(d1);
        listaT.add(d2);
        listaT.add(r1);
        listaT.add(r2);
        m1.agregarEmpleados(listaT);
        assertEquals(6, m1.personasMercado().size());
    }
    
}
