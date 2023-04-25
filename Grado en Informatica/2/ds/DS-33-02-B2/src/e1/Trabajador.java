package e1;

public abstract class Trabajador extends Persona {
    protected final int num_ss;
    protected int salario;
    protected Turno turno;
    
    Trabajador(String nome, String apelido, String DNI, String direccion, int telefono, int num_ss, int salario, Turno turno){
        super(nome, apelido,  DNI,  direccion, telefono);
        this.num_ss = num_ss;
        this.salario = salario;
        this.turno = turno;
    }
    
    Trabajador(String nome, String apelido, String DNI, int num_ss, int salario, Turno turno){
        super(nome, apelido,  DNI);
        this.num_ss = num_ss;
        this.salario = salario;
        this.turno = turno;
    }

    public int getNum_ss() {
        return num_ss;
    }

    public int getSalario() {
        return salario;
    }

    public Turno getTurno() {
        return turno;
    }
    
    @Override
    public abstract String toString();
  
}
