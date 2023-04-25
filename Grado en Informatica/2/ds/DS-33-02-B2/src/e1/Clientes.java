package e1;

public class Clientes extends Persona{
    private final int codigo;
    private int num_compras;
    
    Clientes(String nome, String apelido, String DNI, String direccion, int telefono, int codigo, int num_compras){
        super(nome, apelido,  DNI,  direccion, telefono);
        this.codigo = codigo;
        this.num_compras = num_compras;
    }
        
    Clientes(String nome, String apelido, String DNI, int codigo, int num_compras){
        super(nome, apelido,  DNI);
        this.codigo = codigo;
        this.num_compras = num_compras;
    }

    public int getCodigo() {
        return codigo;
    }

    public int getNum_compras() {
        return num_compras;
    }
    
    public int descuento() {
        return (num_compras/100);
    }

    @Override
    public String toString(){
        if (this.telefono == 0 & this.direccion == null){
            return "O nome do cliente e " + this.getNome() + " " + this.getApelido() + 
                    " con DNI " + this.getDNI() + ", codigo: " + this.getCodigo() + 
                    " ,numero de compras: " + this.getNum_compras() + 
                    " e un descuento de " + this.descuento() + "%";
        }else{
        
            return "O nome do cliente e " + this.getNome() + " " + this.getApelido() + 
                    " con DNI " + this.getDNI() + ", direccion: " + this.getDireccion() + 
                    ", tlf: " + this.getTelefono() + ", codigo: " + this.getCodigo() + 
                    " ,numero de compras: " + this.getNum_compras() + 
                    " e un descuento de " + this.descuento() + "%";
        }
    }
}
