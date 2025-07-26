package base_de_datos_escolar.controldeusuarios.clasesController;

public class Materia {
    private int id;
    private String nombre;
    private String curso;
    private int horasClase;
    private String area;
    private String especialidad;


    public Materia() {
    }

    public Materia(int id, String nombre, String curso, int horasClase, String area, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.curso = curso;
        this.horasClase = horasClase;
        this.area = area;
        this.especialidad = especialidad;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public int getHorasClase() {
        return horasClase;
    }

    public void setHorasClase(int horasClase) {
        this.horasClase = horasClase;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return nombre + " (" + curso + ")";
    }
}
