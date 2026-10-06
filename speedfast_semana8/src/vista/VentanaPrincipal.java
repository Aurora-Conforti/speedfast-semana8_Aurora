package vista;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal de SpeedFast. Reúne en pestañas la gestión de
 * repartidores, pedidos y entregas. Al cambiar de pestaña se recargan
 * los datos de esa pestaña desde la base de datos.
 */
public class VentanaPrincipal extends JFrame {

    private final PanelRepartidores panelRepartidores = new PanelRepartidores();
    private final PanelPedidos panelPedidos = new PanelPedidos();
    private final PanelEntregas panelEntregas = new PanelEntregas();

    public VentanaPrincipal() {
        setTitle("SpeedFast - Sistema de Gestión de Entregas");
        setSize(900, 620);
        setMinimumSize(new Dimension(760, 520));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("SpeedFast", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Repartidores", panelRepartidores);
        pestanas.addTab("Pedidos", panelPedidos);
        pestanas.addTab("Entregas", panelEntregas);

        // Cada vez que el usuario cambia de pestaña se refrescan sus datos y combos.
        pestanas.addChangeListener(e -> {
            Component actual = pestanas.getSelectedComponent();
            if (actual instanceof Recargable) {
                ((Recargable) actual).recargar();
            }
        });

        add(titulo, BorderLayout.NORTH);
        add(pestanas, BorderLayout.CENTER);

        // La primera pestaña se carga al abrir la ventana.
        panelRepartidores.recargar();
    }
}
