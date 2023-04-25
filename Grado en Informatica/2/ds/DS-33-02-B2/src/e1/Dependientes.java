package e1;

public class Dependientes extends Trabajador {
    private String especialidade;
    
    Dependientes(String nome, String apelido, String DNI, String direccion, int telefono, int num_ss, int salario, Turno turno, String especialidade){
        super(nome, apelido,  DNI,  direccion, telefono, num_ss, salario, turno);
        this.especialidade = especialidade;
        if (turno == Turno.noche){
            this.salario = this.getSalario() + 150;
        }
    }
    Dependientes(String nome, String apelido, String DNI, int num_ss, int salario, Turno turno, String especialidade){
        super(nome, apelido,  DNI, num_ss, salario, turno);
        this.especialidade = especialidade;
        if (turno == Turno.noche){
            this.salario = this.getSalario() + 150;
        }
    } 

    public String getEspecialidade() {
        return especialidade;
    }
    
    @Override
    public String toString(){
        if (this.getTelefono() == 0 & this.getDireccion() == null){
            return "O nome do dependiente e " + this.getNome() + " " + this.getApelido() + " con DNI " + this.getDNI() + ", numero ss: " + this.getNum_ss() + ", salario: " + this.getSalario() + ", turno de " + this.getTurno() + " e especialidade: " + this.getEspecialidade();
        }else{
            return "O nome do dependiente e " + this.getNome() + " " + this.getApelido() + " con DNI " + this.getDNI() +  ", direccion: " + this.getDireccion() + ", tlf: " + this.getTelefono() + ", numero ss: " + this.getNum_ss() + ", salario: " + this.getSalario() + ", turno de " + this.getTurno() + " e especialidade: " + this.getEspecialidade();
        }
    } 
}
