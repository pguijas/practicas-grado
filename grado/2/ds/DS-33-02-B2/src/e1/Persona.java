package e1;

abstract class Persona {
    protected String nome;
    protected String apelido;
    protected final String DNI;
    protected String direccion;
    protected int telefono;

    public Persona(String nome, String apelido, String DNI, String direccion, int telefono) {
        this.nome = nome;
        this.apelido = apelido;
        this.DNI = DNI;
        this.direccion = direccion;
        this.telefono = telefono;
    }
    
    public Persona(String nome, String apelido, String DNI){
        this.nome = nome;
        this.apelido = apelido;
        this.DNI = DNI;
    }

    public String getNome() {
        return nome;
    }

    public String getApelido() {
        return apelido;
    }

    public String getDNI() {
        return DNI;
    }

    public String getDireccion() {
        return direccion;
    }

    public int getTelefono() {
        return telefono;
    }
    
    @Override
    public abstract String toString();
    
}

