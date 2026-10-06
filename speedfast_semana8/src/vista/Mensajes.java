package vista;

import javax.swing.JOptionPane;
import java.awt.Component;
import java.sql.SQLException;

/**
 * Mensajes reutilizables (JOptionPane) y traducción de errores SQL a
 * textos comprensibles para el usuario.
 */
public class Mensajes {

    private Mensajes() {
        // Clase utilitaria: no se instancia.
    }

    public static void info(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "SpeedFast", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void advertencia(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Datos inválidos", JOptionPane.WARNING_MESSAGE);
    }

    public static void error(Component padre, String mensaje) {
        JOptionPane.showMessageDialog(padre, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Pide confirmación (Sí/No). Retorna true si el usuario eligió Sí. */
    public static boolean confirmar(Component padre, String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(padre, mensaje, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        return opcion == JOptionPane.YES_OPTION;
    }

    /** Convierte una SQLException en un mensaje claro según su código de error. */
    public static String describirError(SQLException e) {
        String estado = e.getSQLState() == null ? "" : e.getSQLState();
        if (estado.startsWith("08")) {
            return "No se pudo conectar con la base de datos. Verifica que MySQL esté en ejecución "
                    + "y que los datos de ConexionBD sean correctos.";
        }
        switch (e.getErrorCode()) {
            case 1045:
                return "Usuario o contraseña de MySQL incorrectos (revisa ConexionBD).";
            case 1049:
                return "La base de datos speedfast_db no existe. Ejecuta primero el script SQL.";
            case 1146:
                return "Falta alguna tabla en la base de datos. Ejecuta el script SQL completo.";
            case 1451:
                return "No se puede eliminar porque el registro está siendo usado por otros datos "
                        + "(por ejemplo, entregas asociadas). Elimina primero esas entregas.";
            case 1452:
                return "El pedido o el repartidor seleccionado ya no existe en la base de datos.";
            case 1406:
                return "Alguno de los textos ingresados es demasiado largo.";
            default:
                return "Error de base de datos: " + e.getMessage();
        }
    }
}
