package e1;

import java.util.ArrayList;

public class Mercado {

    ArrayList <Clientes> listaClientes = new ArrayList<>();
    ArrayList <Trabajador> listaEmpleados = new ArrayList<>();
    
    public void agregarCliente(Clientes c){
        listaClientes.add(c);
    }
    
    public void agregarEmpleado(Trabajador t){
        listaEmpleados.add(t);
    }
    
    public void agregarClientes(ArrayList<Clientes> lista){
        listaClientes.addAll(lista);
    }
    
    public void agregarEmpleados(ArrayList<? extends Trabajador> lista){
        listaEmpleados.addAll(lista);
    }
    
    public int salariosMercado(){
        int total = 0;
        for (Trabajador t : this.listaEmpleados) {
            total = total + t.getSalario();
        }
        return total;
    }
    
    public ArrayList<Persona> personasMercado(){
        ArrayList <Persona> listaPersonas = new ArrayList<>();
        listaPersonas.addAll(listaClientes);
        listaPersonas.addAll(listaEmpleados);
        return listaPersonas;
    }
}
