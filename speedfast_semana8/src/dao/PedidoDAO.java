package dao;

import conexion.ConexionBD;

import model.EstadoPedido;
import model.Pedido;
import model.TipoPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla pedidos (CRUD, con filtros opcionales por
 * estado y tipo en la lectura). Usa PreparedStatement y ResultSet.
 */
public class PedidoDAO {

    /** Inserta un pedido y deja en el objeto el id generado por MySQL. */
    public boolean create(Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos (direccion, tipo, estado) VALUES (?, ?, ?)";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());
            boolean insertado = ps.executeUpdate() > 0;
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    pedido.setId(claves.getInt(1));
                }
            }
            return insertado;
        }
    }

    /** Retorna todos los pedidos. */
    public List<Pedido> readAll() throws SQLException {
        return readAll(null, null);
    }

    /**
     * Retorna los pedidos aplicando filtros opcionales: si estado o tipo
     * es null, ese filtro no se aplica.
     */
    public List<Pedido> readAll(EstadoPedido estado, TipoPedido tipo) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT id, direccion, tipo, estado FROM pedidos WHERE 1 = 1");
        List<String> parametros = new ArrayList<>();
        if (estado != null) {
            sql.append(" AND estado = ?");
            parametros.add(estado.name());
        }
        if (tipo != null) {
            sql.append(" AND tipo = ?");
            parametros.add(tipo.name());
        }
        sql.append(" ORDER BY id");

        List<Pedido> lista = new ArrayList<>();
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setString(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    /** Actualiza dirección, tipo y estado del pedido indicado. */
    public boolean update(Pedido pedido) throws SQLException {
        String sql = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().name());
            ps.setString(3, pedido.getEstado().name());
            ps.setInt(4, pedido.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina el pedido con el id indicado. */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Convierte la fila actual del ResultSet en la subclase de Pedido que corresponde. */
    private Pedido mapear(ResultSet rs) throws SQLException {
        // Las columnas tipo y estado aceptan NULL en el script; si una fila
        // fue insertada a mano sin esos datos se usan valores por defecto.
        String tipoTexto = rs.getString("tipo");
        String estadoTexto = rs.getString("estado");
        TipoPedido tipo = tipoTexto == null ? TipoPedido.COMIDA : TipoPedido.valueOf(tipoTexto);
        EstadoPedido estado = estadoTexto == null ? EstadoPedido.PENDIENTE : EstadoPedido.valueOf(estadoTexto);
        return Pedido.crear(tipo, rs.getInt("id"), rs.getString("direccion"), estado);
    }
}
