package e2_e3;

import java.util.*;
    
public class EuroCoinCollection implements Iterable<EuroCoin>{
    
    private ArrayList<EuroCoin> collection = new ArrayList<EuroCoin>();
    private paises pais_it = null;
    private int cambios = 0;

    // Mira si la moneda está en la colección
    public boolean hasCoin(EuroCoin coin) {
        return collection.contains(coin);
    }
   
    // Inserta una moneda si no está en la coleccion devuelve true si, y solo si la inserta
    // Para nosotros dos monedas con igual  pais, vn y diseño son iguales. 
    public boolean insertCoin(EuroCoin coin) {
        if (hasCoin(coin)) {
            return false;
        } else {
            cambios++;
            return collection.add(coin);
        }
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
        cambios++;
        collection.remove(coin);
    }

    //Ordena orden naturas
    public void sort(){
        cambios++;
        Collections.sort(collection);
    }
    
    //Ordena con determinado comparador
    public void sort(Comparator c){
        cambios++;
        Collections.sort(collection, c);
    }
    
    @Override //sobreescribimos metodo iterator para poder iterar la colección
    public Iterator<EuroCoin> iterator() {
        return new EuroCoinCollectionIterator(this, collection, pais_it, cambios);
    }

    //Seters    
    public void setPais_it(paises pais_it) {
        this.pais_it = pais_it;
    }

    public int getCambios() {
        return cambios;
    }
    
}
