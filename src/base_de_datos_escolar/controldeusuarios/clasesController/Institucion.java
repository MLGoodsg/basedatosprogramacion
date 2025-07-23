package base_de_datos_escolar.controldeusuarios.clasesController;

public class Institucion {
    private int id;
    private String nombre;

    public Institucion(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}
