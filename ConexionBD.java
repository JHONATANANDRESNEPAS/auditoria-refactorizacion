import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase exclusiva para gestionar la conexión con la base de datos
 */
public class ConexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/tienda";
    private static final String USUARIO = "root";
    private static final String CLAVE = "admin123";

    public static Connection obtenerConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, CLAVE);
        } catch (Exception e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
            return null;
        }
    }
}