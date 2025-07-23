package base_de_datos_escolar.controldeusuarios.clasesController;

import java.util.Date;

public class Empleado {
    private int id;
    private String nombre;
    private String apellido;
    private Date fechaNacimiento;
    private String tipoCargo;
    private double salario;
    private String cedula;
    private String direccion;
    private String sexo;
    private int idInstitucion;

    public Empleado(int id, String nombre, String apellido, Date fechaNacimiento,
                    String tipoCargo, double salario, String cedula,
                    String direccion, String sexo, int idInstitucion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.tipoCargo = tipoCargo;
        this.salario = salario;
        this.cedula = cedula;
        this.direccion = direccion;
        this.sexo = sexo;
        this.idInstitucion = idInstitucion;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Date getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(Date fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public String getTipoCargo() { return tipoCargo; }
    public void setTipoCargo(String tipoCargo) { this.tipoCargo = tipoCargo; }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public int getIdInstitucion() { return idInstitucion; }
    public void setIdInstitucion(int idInstitucion) { this.idInstitucion = idInstitucion; }
}