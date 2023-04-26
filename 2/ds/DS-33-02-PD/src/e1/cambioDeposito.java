package e1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class cambioDeposito implements AlgoritmoCambio {
    private List<EuroCoin> deposito;
    
    public void addDeposito(EuroCoin e){
        deposito.add(e);
    }
    
    
    public cambioDeposito() {
        deposito = new ArrayList();
        EuroCoin e2_sp_2005  = new EuroCoin(Coin.e2, paises.Spain, "Juan Carlos I", 2005);
        EuroCoin e1_sp1 = new EuroCoin(Coin.e1, paises.Spain, "Juan Carlos I", 0);
        EuroCoin c50_it = new EuroCoin(Coin.c50, paises.Italy, null, 0);
        EuroCoin c20_fr = new EuroCoin(Coin.c20, paises.France, null, 0);
        EuroCoin c1_pt = new EuroCoin(Coin.c1, paises.Portugal, null, 0);
        addDeposito(e2_sp_2005);
        addDeposito(e1_sp1);
        addDeposito(c50_it);
        addDeposito(c20_fr);
        addDeposito(c1_pt);
    }
    

    @Override
    public List<EuroCoin> cambio(int cambio, List<EuroCoin> monedas){
        List<EuroCoin> monedas_c = new ArrayList<>();
        int max;
        int restante=cambio;
        EuroCoin emax;
        while (restante>0) {
            max=0;
            emax=deposito.get(0);
            for (EuroCoin e : deposito) {
                if(e.getValorNominal()>max && e.getValorNominal()<=restante){
                    max=e.getValorNominal();
                    emax=e;
                }    
            }         
            restante=restante-max;
            monedas_c.add(emax);
            
        }
        return monedas_c;

    }
}