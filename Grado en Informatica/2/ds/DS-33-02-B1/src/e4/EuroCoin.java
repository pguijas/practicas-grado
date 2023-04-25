package e4;

//clase que implementa una moneda de euro
public class EuroCoin {
    
 
    private paises pais;
    private Coin TipoMoneda;
    private String diseño;
    private int AnoAcunacion;

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
   
    
}