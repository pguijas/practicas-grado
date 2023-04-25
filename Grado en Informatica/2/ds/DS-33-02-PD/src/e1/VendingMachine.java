package e1;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class VendingMachine{
    
    private HashMap<String, Integer> productos = new HashMap<>();
    private List <EuroCoin> monedas = new ArrayList<>(); //solo son monedas de euro
    private AlgoritmoCambio algoritmo;
    
    public VendingMachine(){}
    
    public void insertProduct(String product, int price){
        productos.put(product, price);
    }
    
    public void insertCoin(EuroCoin e){
        monedas.add(e);
    }
    
    public List<EuroCoin> buy (String product){
        int precio, saldo;
        if (productos.containsKey(product)) {
            saldo = 0;
            for (EuroCoin e: monedas){
                saldo = saldo + e.getValorNominal();
            }
            precio = productos.get(product);
            if (saldo<precio){
                System.out.print("Saldo insuficiente para a compra do produto");
            } else{
                return algoritmo.cambio(saldo-precio, monedas);
            }     
        } else 
            System.out.print("Non esta dispoñible tal produto na maquina");
        return null;
    }
    
    public List<EuroCoin> cancel(){
        List <EuroCoin> devuelve = monedas;
        monedas = new ArrayList<>();
        return devuelve;
    }
    
    public AlgoritmoCambio getAlgoritmo(){
        return algoritmo;
    }
    
    public void setAlgoritmo (AlgoritmoCambio algoritmo){
        this.algoritmo = algoritmo;
    }
  
}