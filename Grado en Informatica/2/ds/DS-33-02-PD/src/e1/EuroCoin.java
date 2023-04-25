package e1;

import java.util.Objects;

public class EuroCoin{
    
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
    
    public paises getPais(){
        return pais;
    }
    
    public String getDiseño() {
        return diseño;
    }

    public int getAnoAcunacion() {
        return AnoAcunacion;
    }    
    
    public int getValorNominal() {
        return TipoMoneda.getValorNominal();
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

 
}