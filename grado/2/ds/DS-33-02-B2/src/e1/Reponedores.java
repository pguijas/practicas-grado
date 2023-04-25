package e1;

public class Reponedores extends Trabajador{
    private String empresa;

    Reponedores(String nome, String apelido, String DNI, String direccion, int telefono, int num_ss, int salario, Turno turno, String empresa){
        super(nome, apelido,  DNI,  direccion, telefono, num_ss, salario, turno);
        if (turno == Turno.noche){
            throw new IllegalArgumentException("Non hai repoñedores nocturnos");
        }
        this.empresa = empresa;
    }
    
    Reponedores(String nome, String apelido, String DNI, int num_ss, int salario, Turno turno, String empresa){
        super(nome, apelido,  DNI, num_ss, salario, turno);
        if (turno == Turno.noche){
            throw new IllegalArgumentException("Non hai repoñedores nocturnos");
        }
        this.empresa = empresa;
    }

    public String getEmpresa() {
        return empresa;
    }

    @Override
    public String toString(){
        if (this.getTelefono() == 0 & this.getDireccion() == null){
            return "O nome do dependiente e " + this.getNome() + " " + this.getApelido() + 
                    " con DNI " + this.getDNI() + ", numero ss: " + this.getNum_ss() + 
                    ", salario: " + this.getSalario() + ", turno de " + this.getTurno() + 
                        " e da empresa: " + this.getEmpresa();
        }else{
            return "O nome do dependiente e " + this.getNome() + " " + this.getApelido() + 
                    " con DNI " + this.getDNI() +  ", direccion: " + this.getDireccion() + 
                    ", tlf: " + this.getTelefono() + ", numero ss: " + this.getNum_ss() + 
                    ", salario: " + this.getSalario() + ", turno de " + this.getTurno() + 
                    " e da empresa: " + this.getEmpresa();
        }
    }
}
