package e2_e3;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class EuroCoinCollectionIterator implements Iterator<EuroCoin> {

    private ArrayList<EuroCoin> lista;
    private EuroCoinCollection collection;
    private paises pais;
    private int indice;
    private int ultimo_indice;
    private int cambios;
    private boolean borrar;
    

    public EuroCoinCollectionIterator(EuroCoinCollection collection, ArrayList<EuroCoin> lista, paises pais, int cambios) {
        this.collection = collection; //realmente es un puntero
        this.cambios=cambios;
        this.lista = lista;
        this.pais = pais;
        this.indice = 0;
        if (pais != null && lista.get(indice).getPais() != pais && hasNext()) {
            next();
        }
        this.borrar=true;
        this.ultimo_indice = this.indice;
    }

    @Override
    public boolean hasNext() {
        if (collection.getCambios()!=cambios) {
            throw new ConcurrentModificationException();
        }
        int i = indice; 
        while (i < lista.size()) {
            if (lista.get(i).getPais() == pais || pais == null) {
                return true;
            }
            i++;
        }
        return false;

    }

    @Override
    public EuroCoin next() {
        if (indice>=lista.size()) { //cuando el iterador ya pasó el ultimo elemento
            throw new NoSuchElementException();
        }
        ultimo_indice=indice;
        while (++indice < lista.size()) {
            if (lista.get(indice).getPais() == pais || pais == null) {
                break;
            }
        }
        return lista.get(ultimo_indice);

    }

    @Override
    public void remove() {
        if (!this.hasNext() && borrar) {//solo se puede borrar 1 vez el último
            lista.remove(ultimo_indice);
            borrar=false;
        } else {
            throw new IllegalStateException();
        }
    }

}
