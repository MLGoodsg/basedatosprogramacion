package base_de_datos_escolar.controldeusuarios.clasesController;

public class CalificacionDetalle {
    private String materia;
    private double nota;
    private String tipo;
    private double porcentaje;

    public CalificacionDetalle(String materia, double nota, String tipo, double porcentaje) {
        this.materia = materia;
        this.nota = nota;
        this.tipo = tipo;
        this.porcentaje = porcentaje;
    }

    public String getMateria() {
        return materia;
    }

    public double getNota() {
        return nota;
    }

    public String getTipo() {
        return tipo;
    }

    public double getPorcentaje() {
        return porcentaje;
    }
}
