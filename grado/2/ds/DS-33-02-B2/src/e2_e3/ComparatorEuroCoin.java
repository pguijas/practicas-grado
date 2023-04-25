package e2_e3;

import java.util.Comparator;

public class ComparatorEuroCoin implements Comparator<EuroCoin> {

    @Override
    public int compare(EuroCoin o1, EuroCoin o2) {
        int dif = o1.getPais().compareTo(o2.getPais());
        if (dif == 0) {
            dif = o2.getTipo_moneda().getValorNominal() - o1.getTipo_moneda().getValorNominal();
            if (dif == 0) {
                dif = o1.getDiseño().compareTo(o2.getDiseño());
            }
        }
        return dif;
    }
    
}
