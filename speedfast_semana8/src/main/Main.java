package main;

import conexion.ConexionBD;
import vista.Mensajes;
import vista.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.sql.SQLException;

/**
 * Punto de entrada. Antes de abrir la interfaz comprueba que la conexión
 * con MySQL funcione; si falla, muestra un mensaje claro y termina.
 */
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si no se puede aplicar el estilo del sistema, se usa el de Java por defecto.
        }

        SwingUtilities.invokeLater(() -> {
            try {
                ConexionBD.probarConexion();
            } catch (SQLException e) {
                Mensajes.error(null, "No fue posible iniciar SpeedFast.\n" + Mensajes.describirError(e));
                System.exit(1);
                return;
            }
            new VentanaPrincipal().setVisible(true);
        });
    }
}
