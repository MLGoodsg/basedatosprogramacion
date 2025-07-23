package base_de_datos_escolar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    public static void main(String[] args) {
        // URL corregida con "//" después de "mysql:"
        String url = "jdbc:mysql://nozomi.proxy.rlwy.net:51090/bd_escolar";
        String usuario = "root";
        String clave = "abvqWjezmsgvxfbtyvYJoQAzNSWHpEnw";
        try {
            // Cargar explícitamente el driver (opcional, pero recomendado)
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(url, usuario, clave);
            System.out.println("Conectado exitosamente a la base de datos.");
        } catch (SQLException e) {
            System.out.println("Error al conectar: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Driver MySQL no encontrado: " + e.getMessage());
        }
    }
}
