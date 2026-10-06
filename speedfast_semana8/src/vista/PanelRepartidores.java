package vista;

import dao.RepartidorDAO;
import model.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Pestaña de gestión de repartidores: formulario + botones CRUD + JTable.
 * Al seleccionar una fila de la tabla, sus datos se cargan en el formulario
 * para poder editarlos o eliminarlos.
 */
public class PanelRepartidores extends JPanel implements Recargable {

    private final RepartidorDAO dao = new RepartidorDAO();
    private final List<Repartidor> repartidoresEnTabla = new ArrayList<>();
    private Repartidor seleccionado;

    private final DefaultTableModel modeloTabla = new DefaultTableModel(new String[]{"ID", "Nombre"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false; // la tabla es solo de lectura
        }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private final JTextField txtNombre = new JTextField();

    public PanelRepartidores() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario
        JPanel panelFormulario = new JPanel(new GridLayout(1, 2, 10, 10));
        panelFormulario.add(new JLabel("Nombre del repartidor:"));
        panelFormulario.add(txtNombre);

        // Botones
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        btnRegistrar.addActionListener(e -> registrar());
        btnActualizar.addActionListener(e -> actualizar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panelBotones.add(btnRegistrar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnLimpiar);

        JPanel norte = new JPanel(new BorderLayout(5, 5));
        norte.add(panelFormulario, BorderLayout.CENTER);
        norte.add(panelBotones, BorderLayout.SOUTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarSeleccionEnFormulario();
            }
        });

        add(norte, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    /** Vuelve a leer los repartidores desde la base de datos y refresca la tabla. */
    @Override
    public void recargar() {
        try {
            List<Repartidor> lista = dao.readAll();
            repartidoresEnTabla.clear();
            repartidoresEnTabla.addAll(lista);
            modeloTabla.setRowCount(0);
            for (Repartidor r : lista) {
                modeloTabla.addRow(new Object[]{r.getId(), r.getNombre()});
            }
            seleccionado = null;
        } catch (SQLException ex) {
            Mensajes.error(this, "No se pudo cargar la lista de repartidores.\n" + Mensajes.describirError(ex));
        }
    }

    private void registrar() {
        String nombre = leerNombreValidado();
        if (nombre == null) {
            return;
        }
        try {
            dao.create(new Repartidor(nombre));
            Mensajes.info(this, "Repartidor \"" + nombre + "\" registrado correctamente.");
            limpiarFormulario();
            recargar();
        } catch (SQLException ex) {
            Mensajes.error(this, "No se pudo registrar el repartidor.\n" + Mensajes.describirError(ex));
        }
    }

    private void actualizar() {
        if (seleccionado == null) {
            Mensajes.advertencia(this, "Selecciona un repartidor de la tabla para editarlo.");
            return;
        }
        String nombre = leerNombreValidado();
        if (nombre == null) {
            return;
        }
        try {
            Repartidor editado = new Repartidor(seleccionado.getId(), nombre);
            if (dao.update(editado)) {
                Mensajes.info(this, "Repartidor actualizado correctamente.");
            } else {
                Mensajes.advertencia(this, "El repartidor ya no existe en la base de datos.");
            }
            limpiarFormulario();
            recargar();
        } catch (SQLException ex) {
            Mensajes.error(this, "No se pudo actualizar el repartidor.\n" + Mensajes.describirError(ex));
        }
    }

    private void eliminar() {
        if (seleccionado == null) {
            Mensajes.advertencia(this, "Selecciona un repartidor de la tabla para eliminarlo.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar al repartidor \"" + seleccionado.getNombre() + "\"?")) {
            return;
        }
        try {
            if (dao.delete(seleccionado.getId())) {
                Mensajes.info(this, "Repartidor eliminado correctamente.");
            } else {
                Mensajes.advertencia(this, "El repartidor ya no existe en la base de datos.");
            }
            limpiarFormulario();
            recargar();
        } catch (SQLException ex) {
            Mensajes.error(this, "No se pudo eliminar el repartidor.\n" + Mensajes.describirError(ex));
        }
    }

    /** Valida el nombre (obligatorio, máx. 100 caracteres, solo letras y signos de nombre). */
    private String leerNombreValidado() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            Mensajes.advertencia(this, "El nombre del repartidor es obligatorio.");
            txtNombre.requestFocus();
            return null;
        }
        if (nombre.length() > 100) {
            Mensajes.advertencia(this, "El nombre no puede superar los 100 caracteres.");
            txtNombre.requestFocus();
            return null;
        }
        if (!nombre.matches("\\p{L}[\\p{L} .'-]*")) {
            Mensajes.advertencia(this, "El nombre solo puede contener letras y espacios.");
            txtNombre.requestFocus();
            return null;
        }
        return nombre;
    }

    private void mostrarSeleccionEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= repartidoresEnTabla.size()) {
            seleccionado = null;
            return;
        }
        seleccionado = repartidoresEnTabla.get(fila);
        txtNombre.setText(seleccionado.getNombre());
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        tabla.clearSelection();
        seleccionado = null;
    }
}
