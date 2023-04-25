/**
 *
 * @author pguijas
 */

package e4;

public enum Coin {
    c1(1,color.bronce), c2(2,color.bronce) ,c5(5,color.bronce), c10(10,color.oro), c20(20,color.oro), c50(50,color.oro), e1(100,color.oro_plata), e2(200,color.oro_plata);

    private int ValorNominal;
    private color colorm;

    
    private Coin (int ValorNominal, color color){
        this.ValorNominal=ValorNominal;
        this.colorm=color;
    }

    public int getValorNominal() {
        return ValorNominal;
    }

    public color getColorm() {
        return colorm;
    }
    
    
}
