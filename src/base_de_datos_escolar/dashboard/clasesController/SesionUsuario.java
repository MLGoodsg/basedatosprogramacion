package base_de_datos_escolar.dashboard.clasesController;

public class SesionUsuario {
    public static String nombre;
    public static String apellido;
    public static byte[] foto;
    public static boolean usuarioActivo=false;
    public static String tipoUsuario;
    public static String ventanaActual="";
  /*Guarda la información del usuario que ha iniciado sesion para poder usar su información
  en otras ventanas.*/


    public static String getVentanaActual() {
        return ventanaActual;
    }
    public static void setVentanaActual(String ventanaActual) {
        SesionUsuario.ventanaActual = ventanaActual;
    }
    public static String getNombre() {
        return nombre;
    }
    public static void setNombre(String nombre) {
        SesionUsuario.nombre = nombre;
    }

    public static String getTipoUsuario() {
        return tipoUsuario;
    }
    public static void setTipoUsuario(String tipoUsuario) {
        SesionUsuario.tipoUsuario = tipoUsuario;
    }
    public static boolean isUsuarioActivo() {
        return usuarioActivo;
    }
    public static void setUsuarioActivo(boolean usuarioActivo) {
        SesionUsuario.usuarioActivo = usuarioActivo;
    }

    public static void limpiarSesion() {
        nombre = null;
        apellido = null;
        foto = null;
        usuarioActivo=false;
        tipoUsuario="";
        ventanaActual="";
    }
}
