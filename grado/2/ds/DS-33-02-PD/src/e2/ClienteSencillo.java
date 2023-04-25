package e2;

import java.util.Observable;
import java.util.Observer;

public class ClienteSencillo implements Observer{
    private final String name;
    private int updates;
    
    public ClienteSencillo(String name) {
        this.name = name;
        updates=0;
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
        if((String)arg=="cierre"){
            updates++;
            System.out.println(getName() + ", se ha producido un cambio en el precio de cierre de " + accion.getSimbolo() + ": " + accion.getCierre());
        }
       
    }
    
    
}
