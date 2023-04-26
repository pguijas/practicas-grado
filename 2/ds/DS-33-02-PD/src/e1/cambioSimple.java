package e1;

import java.util.ArrayList;
import java.util.List;


public class cambioSimple implements AlgoritmoCambio {
    
    @Override
    public List<EuroCoin> cambio(int cambio, List<EuroCoin> monedas){
        List<EuroCoin> monedas_c = new ArrayList<>();
        int restante = cambio;
        for (EuroCoin e : monedas) {
            if ((e.getValorNominal())<restante){
                restante=restante-e.getValorNominal();
                monedas_c.add(e);
            }
        }
        return monedas_c;  
    }
}   