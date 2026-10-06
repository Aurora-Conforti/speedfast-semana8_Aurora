package vista;

import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import model.Entrega;
import model.Pedido;
import model.Repartidor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Pestaña de gestión de entregas. Los JComboBox de Pedido y Repartidor se
 * cargan desde la base de datos: muestran "id - texto" pero conservan el id
 * (ver ItemCombo). Se refrescan cada vez que se entra a esta pestaña y
 * después de cada operación, para reflejar pedidos/repartidores nuevos,
 * editados o eliminados.
 */
public class PanelEntregas extends JPanel implements Recargable {

    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String[] COLUMNAS = {"ID", "Pedido", "Repartidor", "Fecha", "Hora"};

    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();
    private final List<Entrega> entregasEnTabla = new ArrayList<>();
    private Entrega seleccionada;

    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false; // la tabla es solo de lectura
        }
    };
    private final JTable tabla = new JTable(modeloTabla);

    // Formulario
    private final JComboBox<ItemCombo> comboPedido = new JComboBox<>();
    private final JComboBox<ItemCombo> comboRepartidor = new JComboBox<>();
    private final JTextField txtFecha = new JTextField();
    private final JTextField txtHora = new JTextField();

    // Filtros
    private final JComboBox<ItemCombo> comboFiltroPedido = new JComboBox<>();
    private final JComboBox<ItemCombo> comboFiltroRepartidor = new JComboBox<>();

    public PanelEntregas() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario
        JButton btnAhora = new JButton("Ahora");
        btnAhora.setToolTipText("Rellena la fecha y la hora actuales");
        btnAhora.addActionListener(e -> {
            txtFecha.setText(LocalDate.now().toString());
            txtHora.setText(LocalTime.now().withNano(0).format(FORMATO_HORA));
        });

        JPanel panelFormulario = new JPanel(new GridLayout(4, 2, 10, 10));
        panelFormulario.add(new JLabel("Pedido:"));
        panelFormulario.add(comboPedido);
        panelFormulario.add(new JLabel("Repartidor:"));
        panelFormulario.add(comboRepartidor);
        panelFormulario.add(new JLabel("Fecha (AAAA-MM-DD):"));
        panelFormulario.add(txtFecha);
        panelFormulario.add(new JLabel("Hora (HH:mm):"));
        panelFormulario.add(txtHora);

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
        panelBotones.add(btnAhora);

        JPanel norte = new JPanel(new BorderLayout(5, 5));
        norte.add(panelFormulario, BorderLayout.CENTER);
        norte.add(panelBotones, BorderLayout.SOUTH);

        // Filtros opcionales (por pedido y/o por repartidor)
        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnQuitarFiltros = new JButton("Quitar filtros");
        btnFiltrar.addActionListener(e -> cargarTabla());
        btnQuitarFiltros.addActionListener(e -> {
            comboFiltroPedido.setSelectedIndex(0);
            comboFiltroRepartidor.setSelectedIndex(0);
            cargarTabla();
        });

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros"));
        panelFiltros.add(new JLabel("Pedido:"));
        panelFiltros.add(comboFiltroPedido);
        panelFiltros.add(new JLabel("Repartidor:"));
        panelFiltros.add(comboFiltroRepartidor);
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

    /** Refresca los combos (pedidos y repartidores desde la BD) y la tabla de entregas. */
    @Override
    public void recargar() {
        cargarCombos();
        cargarTabla();
    }

    /**
     * Recarga los combos desde la base de datos conservando, si todavía
     * existen, los elementos que estaban seleccionados.
     */
    private void cargarCombos() {
        try {
            Integer idPedido = ItemCombo.idSeleccionado(comboPedido);
            Integer idRepartidor = ItemCombo.idSeleccionado(comboRepartidor);
            Integer idFiltroPedido = ItemCombo.idSeleccionado(comboFiltroPedido);
            Integer idFiltroRepartidor = ItemCombo.idSeleccionado(comboFiltroRepartidor);

            List<Pedido> pedidos = pedidoDAO.readAll();
            List<Repartidor> repartidores = repartidorDAO.readAll();

            comboPedido.removeAllItems();
            comboFiltroPedido.removeAllItems();
            comboFiltroPedido.addItem(new ItemCombo(null, "(Todos)"));
            for (Pedido p : pedidos) {
                ItemCombo item = new ItemCombo(p.getId(), p.getId() + " - " + p.getDireccion());
                comboPedido.addItem(item);
                comboFiltroPedido.addItem(item);
            }

            comboRepartidor.removeAllItems();
            comboFiltroRepartidor.removeAllItems();
            comboFiltroRepartidor.addItem(new ItemCombo(null, "(Todos)"));
            for (Repartidor r : repartidores) {
                ItemCombo item = new ItemCombo(r.getId(), r.getId() + " - " + r.getNombre());
                comboRepartidor.addItem(item);
                comboFiltroRepartidor.addItem(item);
            }

            ItemCombo.seleccionar(comboPedido, idPedido);
            ItemCombo.seleccionar(comboRepartidor, idRepartidor);
            ItemCombo.seleccionar(comboFiltroPedido, idFiltroPedido);
            ItemCombo.seleccionar(comboFiltroRepartidor, idFiltroRepartidor);
        } catch (SQLException ex) {
            Mensajes.error(this, "No se pudieron cargar los pedidos y repartidores.\n" + Mensajes.describirError(ex));
        }
    }

    /** Lee las entregas (respetando los filtros activos) y refresca la tabla. */
    private void cargarTabla() {
        try {
            List<Entrega> lista = entregaDAO.readAll(
                    ItemCombo.idSeleccionado(comboFiltroPedido),
                    ItemCombo.idSeleccionado(comboFiltroRepartidor));
            entregasEnTabla.clear();
            entregasEnTabla.addAll(lista);
            modeloTabla.setRowCount(0);
            for (Entrega e : lista) {
                modeloTabla.addRow(new Object[]{
                        e.getId(),
                        e.getIdPedido() + " - " + e.getDireccionPedido(),
                        e.getIdRepartidor() + " - " + e.getNombreRepartidor(),
                        e.getFecha() == null ? "" : e.getFecha().toString(),
                        e.getHora() == null ? "" : e.getHora().format(FORMATO_HORA)
                });
            }
            seleccionada = null;
        } catch (SQLException ex) {
            Mensajes.error(this, "No se pudo cargar la lista de entregas.\n" + Mensajes.describirError(ex));
        }
    }

    private void registrar() {
        Entrega nueva = leerFormularioValidado(0);
        if (nueva == null) {
            return;
        }
        try {
            entregaDAO.create(nueva);
            Mensajes.info(this, "Entrega " + nueva.getId() + " registrada correctamente.");
            limpiarFormulario();
            recargar();
        } catch (SQLException ex) {
            Mensajes.error(this, "No se pudo registrar la entrega.\n" + Mensajes.describirError(ex));
        }
    }

    private void actualizar() {
        if (seleccionada == null) {
            Mensajes.advertencia(this, "Selecciona una entrega de la tabla para editarla.");
            return;
        }
        Entrega editada = leerFormularioValidado(seleccionada.getId());
        if (editada == null) {
            return;
        }
        try {
            if (entregaDAO.update(editada)) {
                Mensajes.info(this, "Entrega " + editada.getId() + " actualizada correctamente.");
            } else {
                Mensajes.advertencia(this, "La entrega ya no existe en la base de datos.");
            }
            limpiarFormulario();
            recargar();
        } catch (SQLException ex) {
            Mensajes.error(this, "No se pudo actualizar la entrega.\n" + Mensajes.describirError(ex));
        }
    }

    private void eliminar() {
        if (seleccionada == null) {
            Mensajes.advertencia(this, "Selecciona una entrega de la tabla para eliminarla.");
            return;
        }
        if (!Mensajes.confirmar(this, "¿Eliminar la entrega " + seleccionada.getId() + "?")) {
            return;
        }
        try {
            if (entregaDAO.delete(seleccionada.getId())) {
                Mensajes.info(this, "Entrega eliminada correctamente.");
            } else {
                Mensajes.advertencia(this, "La entrega ya no existe en la base de datos.");
            }
            limpiarFormulario();
            recargar();
        } catch (SQLException ex) {
            Mensajes.error(this, "No se pudo eliminar la entrega.\n" + Mensajes.describirError(ex));
        }
    }

    /**
     * Valida el formulario (pedido y repartidor seleccionados, fecha y hora
     * con formato válido) y retorna la Entrega lista para guardar, o null
     * si algún dato es inválido (en ese caso ya se mostró el mensaje).
     */
    private Entrega leerFormularioValidado(int id) {
        Integer idPedido = ItemCombo.idSeleccionado(comboPedido);
        if (idPedido == null) {
            Mensajes.advertencia(this, "Debes seleccionar un pedido. Si no hay ninguno, regístralo primero en la pestaña Pedidos.");
            return null;
        }
        Integer idRepartidor = ItemCombo.idSeleccionado(comboRepartidor);
        if (idRepartidor == null) {
            Mensajes.advertencia(this, "Debes seleccionar un repartidor. Si no hay ninguno, regístralo primero en la pestaña Repartidores.");
            return null;
        }

        String textoFecha = txtFecha.getText().trim();
        if (textoFecha.isEmpty()) {
            Mensajes.advertencia(this, "La fecha es obligatoria (formato AAAA-MM-DD, por ejemplo 2026-10-05).");
            txtFecha.requestFocus();
            return null;
        }
        LocalDate fecha;
        try {
            fecha = LocalDate.parse(textoFecha);
        } catch (DateTimeParseException ex) {
            Mensajes.advertencia(this, "La fecha no es válida. Usa el formato AAAA-MM-DD (por ejemplo 2026-10-05).");
            txtFecha.requestFocus();
            return null;
        }

        String textoHora = txtHora.getText().trim();
        if (textoHora.isEmpty()) {
            Mensajes.advertencia(this, "La hora es obligatoria (formato HH:mm, por ejemplo 14:30).");
            txtHora.requestFocus();
            return null;
        }
        LocalTime hora;
        try {
            hora = LocalTime.parse(textoHora);
        } catch (DateTimeParseException ex) {
            Mensajes.advertencia(this, "La hora no es válida. Usa el formato HH:mm (por ejemplo 14:30).");
            txtHora.requestFocus();
            return null;
        }

        return new Entrega(id, idPedido, idRepartidor, fecha, hora);
    }

    private void mostrarSeleccionEnFormulario() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= entregasEnTabla.size()) {
            seleccionada = null;
            return;
        }
        seleccionada = entregasEnTabla.get(fila);
        ItemCombo.seleccionar(comboPedido, seleccionada.getIdPedido());
        ItemCombo.seleccionar(comboRepartidor, seleccionada.getIdRepartidor());
        txtFecha.setText(seleccionada.getFecha() == null ? "" : seleccionada.getFecha().toString());
        txtHora.setText(seleccionada.getHora() == null ? "" : seleccionada.getHora().format(FORMATO_HORA));
    }

    private void limpiarFormulario() {
        if (comboPedido.getItemCount() > 0) {
            comboPedido.setSelectedIndex(0);
        }
        if (comboRepartidor.getItemCount() > 0) {
            comboRepartidor.setSelectedIndex(0);
        }
        txtFecha.setText("");
        txtHora.setText("");
        tabla.clearSelection();
        seleccionada = null;
    }
}
