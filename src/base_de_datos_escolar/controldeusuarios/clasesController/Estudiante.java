package base_de_datos_escolar.controldeusuarios.clasesController;

import java.sql.Date;

public class Estudiante {
    private int id;
    private String nombre;
    private String apellido;
    private String cedula;
    private Date fechaNacimiento;
    private String direccion;
    private String sexo;
    private int idInstitucion;

    public Estudiante(int id, String nombre, String apellido, String cedula,
                      Date fechaNacimiento, String direccion, String sexo, int idInstitucion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.fechaNacimiento = fechaNacimiento;
        this.direccion = direccion;
        this.sexo = sexo;
        this.idInstitucion = idInstitucion;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getCedula() { return cedula; }
    public Date getFechaNacimiento() { return fechaNacimiento; }
    public String getDireccion() { return direccion; }
    public String getSexo() { return sexo; }
    public int getIdInstitucion() { return idInstitucion; }

    public String toString() {
        return nombre + " " + apellido + " - " + cedula + " - " + sexo + " - " + direccion;
    }
}
