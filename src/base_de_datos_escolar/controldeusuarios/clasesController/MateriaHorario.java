package base_de_datos_escolar.controldeusuarios.clasesController;

public class MateriaHorario {
    private int id;
    private int idInstitucion;
    private String nombreInstitucion;
    private int idDocente;
    private String nombreDocente;
    private int idMateria;
    private String nombreMateria;
    private String curso;
    private String grado;
    private String dia;
    private String hora;
    private int idPeriodo;

    public MateriaHorario(int id, int idInstitucion, String nombreInstitucion,
                          int idDocente, String nombreDocente, int idMateria,
                          String nombreMateria, String curso, String grado,
                          String dia, String hora, int idPeriodo) {
        this.id = id;
        this.idInstitucion = idInstitucion;
        this.nombreInstitucion = nombreInstitucion;
        this.idDocente = idDocente;
        this.nombreDocente = nombreDocente;
        this.idMateria = idMateria;
        this.nombreMateria = nombreMateria;
        this.curso = curso;
        this.grado = grado;
        this.dia = dia;
        this.hora = hora;
        this.idPeriodo = idPeriodo;
    }

    // Getters
    public int getId() {
        return id;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public String getNombreInstitucion() {
        return nombreInstitucion;
    }

    public void setNombreInstitucion(String nombreInstitucion) {
        this.nombreInstitucion = nombreInstitucion;
    }

    public String getNombreDocente() {
        return nombreDocente;
    }

    public void setNombreDocente(String nombreDocente) {
        this.nombreDocente = nombreDocente;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public void setNombreMateria(String nombreMateria) {
        this.nombreMateria = nombreMateria;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public int getIdPeriodo() {
        return idPeriodo;
    }

    public void setIdPeriodo(int idPeriodo) {
        this.idPeriodo = idPeriodo;
    }
}
