package vista;

import dao.PedidoDAO;
import model.EstadoPedido;
import model.Pedido;
import model.TipoPedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Pestaña de gestión de pedidos: formulario + botones CRUD + JTable con
 * filtros opcionales por estado y tipo.
 */
public class PanelPedidos extends JPanel implements Recargable {

    private static final String TODOS = "(Todos)";
    private static final String[] COLUMNAS = {
            "ID", "Dirección", "Tipo", "Distancia (km)", "Tiempo estimado (min)", "Estado"
    };

    private final PedidoDAO dao = new PedidoDAO();
    private final List<Pedido> pedidosEnTabla = new ArrayList<>();
    private Pedido seleccionado;

    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false; // la tabla es solo de lectura
        }
    };
    private final JTable tabla = new JTable(modeloTabla);

    // Formulario
    private final JTextField txtDireccion = new JTextField();
    private final JComboBox<TipoPedido> comboTipo = new JComboBox<>(TipoPedido.values());
    private final JComboBox<EstadoPedido> comboEstado = new JComboBox<>(EstadoPedido.values());

    // Filtros
    private final JComboBox<String> comboFiltroEstado = new JComboBox<>(opcionesFiltro(EstadoPedido.values()));
    private final JComboBox<String> comboFiltroTipo = new JComboBox<>(opcionesFiltro(TipoPedido.values()));

    public PanelPedidos() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario
        JPanel panelFormulario = new JPanel(new GridLayout(3, 2, 10, 10));
        panelFormulario.add(new JLabel("Dirección de entrega:"));
        panelFormulario.add(txtDireccion);
        panelFormulario.add(new JLabel("Tipo de pedido:"));
        panelFormulario.add(comboTipo);
        panelFormulario.add(new JLabel("Estado:"));
        panelFormulario.add(comboEstado);

        // Botones CRUD
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

        // Filtros opcionales
        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnQuitarFiltros = new JButton("Quitar filtros");
        btnFiltrar.addActionListener(e -> recargar());
        btnQuitarFiltros.addActionListener(e -> {
            comboFiltroEstado.setSelectedIndex(0);
            comboFiltroTipo.setSelectedIndex(0);
            recargar();
        });

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        panelFiltros.add(new JLabel("Estado:"));
        panelFiltros.add(comboFiltroEstado);
        panelFiltros.add(new JLabel("Tipo:"));
        panelFiltros.add(comboFiltroTipo);
        panelFiltros.add(btnFiltrar);
        panelFiltros.add(btnQuitarFiltros);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarSeleccionEnFormulario();
            }
        });

        JPanel centro = new JPanel(new BorderLayout(5, 5));
        centro.add(panelFiltros, BorderLayout.NORTH);
        centro.add(new JScrollPane(tabla), BorderLayout.CENTER);

        add(norte, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
    }

    /** Arma las opciones de un combo de filtro: "(Todos)" + los valores del enum. */
    private static String[] opcionesFiltro(Object[] valores) {
        String[] opciones = new String[valores.length + 1];
        opciones[0] = TODOS;
        for (int i = 0; i < valores.length; i++) {
            opciones[i + 1] = valores[i].toString();
        }
        return opciones;
    }

    /** Estado elegido en el filtro, o null si está en "(Todos)". */
    private EstadoPedido filtroEstado() {
        int indice = comboFiltroEstado.getSelectedIndex();
        return indice <= 0 ? null : EstadoPedido.values()[indice - 1];
    }

    /** Tipo elegido en el filtro, o null si está en "(Todos)". */
    private TipoPedido filtroTipo() {
        int indice = comboFiltroTipo.getSelectedIndex();
        return indice <= 0 ? null : TipoPedido.values()[indice - 1];
    }

    /** Vuelve a leer los pedidos (respetando los filtros activos) y refresca la tabla. */
    @Override
    public void recargar() {
        try {
            List<Pedido> lista = dao.readAll(filtroEstado(), filtroTipo());
            pedidosEnTabla.clear();
            pedidosEnTabla.addAll(lista);
            modeloTabla.setRowCount(0);
            for (Pedido p : lista) {
                modeloTabla.addRow(new Object[]{
                        p.getId(),
                        p.getDireccion(),
                        p.getTipo(),
                        p.getDistanciaKm(),
                        p.calcularTiempoEntrega(),
                        p.getEstado()
                });
            }
            seleccionado = null;
        } catch (SQLException ex) {
            Mensajes.error(this, "No se pudo cargar la lista de pedidos.\n" + Mensajes.describirError(ex));
        }
    }

    private void registrar() {
        String direccion = leerDireccionValidada();
        if (direccion == null) {
            return;
        }
        try {
            Pedido nuevo = Pedido.crear((TipoPedido) comboTipo.getSelectedItem(), 0, direccion,
                    (EstadoPedido) comboEstado.getSelectedItem());
            dao.create(nuevo);
            Mensajes.info(this, "Pedido " + nuevo.getId() + " registrado correctamente como " + nuevo.getTipo() + ".");
            limpiarFormulario();
            recargar();
        } catch (SQLException ex) {
            Mensajes.error(this, "No se pudo registrar el pedido.\n" + Mensajes.describirError(ex));
        }
    }

    private void actualizar() {
        if (seleccionado == null) {
            Mensajes.advertencia(this, "Selecciona un pedido de la tabla para editarlo.");
            return;
        }
        String direccion = leerDireccionValidada();
        if (direccion == null) {
            return;
        }
        try {
            Pedido editado = Pedido.crear((TipoPedido) comboTipo.getSelectedItem(), seleccionado.getId(),
                    direccion, (EstadoPedido) comboEstado.getSelectedItem());
            if (dao.update(editado)) {
                Mensajes.info(this, "Pedido " + editado.getId() + " actualizado correctamente.");
            } else {
                Mensajes.advertencia(this, "El pedido ya no existe en la base de datos.");
            }
            limpiarFormulario();
            recargar();
        } catch (SQLException ex) {
            Mensajes.error(this, "No se pudo actualizar el pedido.\n" + Mensajes.describirError(ex));
        }
    }

    private void eliminar() {
        if (seleccionado == null) {
            Mensajes.advertencia(this, "Selecciona un pedido de la tabla para eliminarlo.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar el pedido " + seleccionado.getId() + "?")) {
            return;
        }
        try {
            if (dao.delete(seleccionado.getId())) {
                Mensajes.info(this, "Pedido eliminado correctamente.");
            } else {
                Mensajes.advertencia(this, "El pedido ya no existe en la base de datos.");
            }
            limpiarFormulario();
            recargar();
        } catch (SQLException ex) {
            Mensajes.error(this, "No se pudo eliminar el pedido.\n" + Mensajes.describirError(ex));
        }
    }

    /** Valida la dirección (obligatoria, máx. 100 caracteres, que no sea solo símbolos). */
    private String leerDireccionValidada() {
        String direccion = txtDireccion.getText().trim();
        if (direccion.isEmpty()) {
            Mensajes.advertencia(this, "La dirección de entrega es obligatoria.");
            txtDireccion.requestFocus();
            return null;
        }
        if (direccion.length() > 100) {
            Mensajes.advertencia(this, "La dirección no puede superar los 100 caracteres.");
            txtDireccion.requestFocus();
            return null;
        }
        if (!direccion.matches(".*[\\p{L}\\p{N}].*")) {
            Mensajes.advertencia(this, "La dirección debe contener letras o números.");
            txtDireccion.requestFocus();
            return null;
        }
        return direccion;
    }

    private void mostrarSeleccionEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= pedidosEnTabla.size()) {
            seleccionado = null;
            return;
        }
        seleccionado = pedidosEnTabla.get(fila);
        txtDireccion.setText(seleccionado.getDireccion());
        comboTipo.setSelectedItem(seleccionado.getTipo());
        comboEstado.setSelectedItem(seleccionado.getEstado());
    }

    private void limpiarFormulario() {
        txtDireccion.setText("");
        comboTipo.setSelectedIndex(0);
        comboEstado.setSelectedIndex(0);
        tabla.clearSelection();
        seleccionado = null;
    }
}
