package e2;

import java.util.Observable;

/*
 * @author pguijas
 * 
 * Comunicación Cliente-Acción: Patrón Observador
 *
 */

public class Accion extends Observable{
    
    private final String simbolo;
    private int cierre, max, min, volumen;
    
    public Accion(String simbolo, int cierre, int max, int min, int volumen) {
        this.simbolo = simbolo;
        this.cierre = cierre;
        this.max = max;
        this.min = min;
        this.volumen = volumen;
    }

    //Geters
    public String getSimbolo() {
        return simbolo;
    }

    public int getCierre() {
        return cierre;
    }
                   
    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public int getVolumen() {
        return volumen;
    }
    
    public void setCierre(int cierre) {
        if (cierre!=this.cierre) {
           this.cierre = cierre;
           this.setChanged();
           notifyObservers("cierre");           
        }
    }

    public void setMax(int max) {
        if (max!=this.max) { 
            this.max = max;  
            this.setChanged();
            notifyObservers("max");
        }
        
    }

    public void setMin(int min) {
        if (min!=this.min) {
            this.min = min;
            this.setChanged();
            notifyObservers("min");          
        }
    }

    public void setVolumen(int volumen) {
        if (volumen!=this.volumen) {
            this.volumen = volumen;
            this.setChanged();
            notifyObservers("volumen");         
        }
    }

}
