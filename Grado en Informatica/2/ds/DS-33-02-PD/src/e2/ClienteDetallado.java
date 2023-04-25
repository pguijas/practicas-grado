package e2;

import java.util.Observable;
import java.util.Observer;

public class ClienteDetallado implements Observer{
    private final String name;
    private int updates;
    
    public ClienteDetallado(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getUpdates() {
        return updates;
    }
    
    @Override
    public void update(Observable o, Object arg) {  
        Accion accion=(Accion)o;
        updates++;
        switch((String)arg){
            case "cierre":
                System.out.println(getName() + ", se ha producido un cambio en el valor de cierre de " + accion.getSimbolo() + ": " + ((Accion)o).getCierre());
                break;            
            case "max":
                System.out.println(getName() + ", se ha producido un cambio en el valor de máximo de " + accion.getSimbolo() + ": " + ((Accion)o).getMax());
                break;
            case "min":
                System.out.println(getName() + ", se ha producido un cambio en el valor de mínimo de " + accion.getSimbolo() + ": " + ((Accion)o).getMin());
                break;
            case "volumen":
                System.out.println(getName() + ", se ha producido un cambio en el numero de acciones negociadas de " + accion.getSimbolo() + ": " + ((Accion)o).getVolumen());
                break;
        }
    }
    
    
}

