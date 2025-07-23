package base_de_datos_escolar.controldeusuarios.clasesController;
public class Acudiente {
    private int id;
    private String nombre;
    private String apellido;
    private String cedula;
    private String telefono;
    private String parentesco;
    private String nombreEstudiante;

    public Acudiente(int id, String nombre, String apellido, String cedula,
                     String telefono, String parentesco, String nombreEstudiante) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.telefono = telefono;
        this.parentesco = parentesco;
        this.nombreEstudiante = nombreEstudiante;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getParentesco() { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }

    public String getNombreEstudiante() { return nombreEstudiante; }
    public void setNombreEstudiante(String nombreEstudiante) { this.nombreEstudiante = nombreEstudiante; }
}