package e4;

import java.util.*;

public class EuroCoinCollection {
    
    private Set<EuroCoin> collection = new HashSet<EuroCoin>();
    
    
    // Inserta una moneda si no está en la coleccion devuelve true si, y solo si la inserta
    // Para nosotros dos monedas con igual  pais, vn y diseño son iguales. 
    public boolean insertCoin(EuroCoin coin) {
        if (!hasCoin(coin)) {
            return collection.add(coin);
        } else {
            return false;
        }
    }

    // Mira si la moneda está en la colección
    public boolean hasCoin(EuroCoin coin) {
        boolean isit = false;
        for (EuroCoin moneda : collection) { //recorre coleccion y se fija en el pais,vn y diseño
            if (moneda.getTipo_moneda() == coin.getTipo_moneda() && moneda.getPais() == coin.getPais() && moneda.getDiseño() == coin.getDiseño()) {
                isit = true;
                break;
            }
        }
        return isit;
    }

    // Devuelve el valor de la colección
    public int value() {
        int valor = 0;
        for (EuroCoin moneda : collection) { //recorremos la colección sumando sus valores nominales
            valor = valor + moneda.getTipo_moneda().getValorNominal();
        }
        return valor;
    }

    // Devuelve el tamaño de la colección
    public int numCoins() {
        return collection.size();
    }

    // Borra la moneda (sin tener en cuenta el año)
    public void removeCoin(EuroCoin coin) {
        for (EuroCoin moneda : collection) {
            if (moneda.getTipo_moneda() == coin.getTipo_moneda() && moneda.getPais() == coin.getPais() && moneda.getDiseño() == coin.getDiseño()) {
                collection.remove(moneda);
                break;
            }
        }
    }

}
