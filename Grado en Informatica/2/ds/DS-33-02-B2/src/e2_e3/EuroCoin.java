package e2_e3;

//clase que implementa una moneda de euro

import java.util.Objects;

public class EuroCoin implements Comparable<EuroCoin>{
    
 
    private final paises pais;
    private final Coin TipoMoneda;
    private final String diseño;
    private final int AnoAcunacion;

    //constructor
    public EuroCoin(Coin TipoMoneda, paises pais, String diseño, int AnoAcunacion) {
        this.TipoMoneda = TipoMoneda;
        this.pais = pais;
        this.diseño = diseño;
        this.AnoAcunacion = AnoAcunacion;
    }

    public Coin getTipo_moneda() {
        return TipoMoneda;
    }

    public paises getPais() {
        return pais;
    }

    public String getDiseño() {
        return diseño;
    }

    public int getAnoAcunacion() {
        return AnoAcunacion;
    } 

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.pais);
        hash = 13 * hash + Objects.hashCode(this.TipoMoneda);
        hash = 13 * hash + Objects.hashCode(this.diseño);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final EuroCoin other = (EuroCoin) obj;
        if (!Objects.equals(this.diseño, other.diseño)) {
            return false;
        }
        if (this.pais != other.pais) {
            return false;
        }
        if (this.TipoMoneda != other.TipoMoneda) {
            return false;
        }
        return true;
    }

    //implementamos el compareTo de la interfaz Comparable
    @Override
    public int compareTo(EuroCoin e) {
        int dif = e.getTipo_moneda().getValorNominal() - this.getTipo_moneda().getValorNominal();
        if (dif == 0) {
            dif = this.getPais().compareTo(e.getPais());
            if (dif == 0) {
                dif = this.getDiseño().compareTo(e.getDiseño());
            }
        }
        return dif; 
        //valor, paises, diseño
    }    
}