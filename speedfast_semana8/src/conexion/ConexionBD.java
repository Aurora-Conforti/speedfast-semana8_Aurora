package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestiona la conexión JDBC con la base de datos MySQL speedfast_db
 * (clase de la semana 7, sin cambios en su forma de uso).
 * Cada DAO llama a conectar() al inicio de su método y cierra la
 * conexión con try-with-resources (no se comparte una única conexión
 * abierta entre toda la app, para evitar dejar recursos abiertos).
 *
 * IMPORTANTE: ajusta USUARIO y CLAVE a las credenciales de tu MySQL
 * local antes de ejecutar la aplicación.
 */
public class ConexionBD {

    private static final String URL =
            "jdbc:mysql://localhost:3306/speedfast_db?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String CLAVE = "TU_PASSWORD_AQUI";

    /**
     * Abre y devuelve una nueva conexión a speedfast_db.
     * Quien la reciba es responsable de cerrarla.
     */
    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver JDBC de MySQL "
                    + "(com.mysql.cj.jdbc.Driver). Verifica que el conector "
                    + "esté agregado a las dependencias del proyecto.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }

    /** Verifica que la conexión funcione (se usa al iniciar la aplicación). */
    public static void probarConexion() throws SQLException {
        try (Connection con = conectar()) {
            // Si llega aquí, la conexión se abrió correctamente.
        }
    }
}
