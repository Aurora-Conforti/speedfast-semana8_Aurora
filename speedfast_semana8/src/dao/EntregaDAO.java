package dao;

import conexion.ConexionBD;

import model.Entrega;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla entregas (CRUD). La lectura hace JOIN con
 * pedidos y repartidores para traer textos legibles, y permite filtrar
 * por pedido y/o por repartidor.
 */
public class EntregaDAO {

    /** Inserta una entrega y deja en el objeto el id generado por MySQL. */
    public boolean create(Entrega entrega) throws SQLException {
        String sql = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, Date.valueOf(entrega.getFecha()));
            ps.setTime(4, Time.valueOf(entrega.getHora()));
            boolean insertado = ps.executeUpdate() > 0;
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    entrega.setId(claves.getInt(1));
                }
            }
            return insertado;
        }
    }

    /** Retorna todas las entregas. */
    public List<Entrega> readAll() throws SQLException {
        return readAll(null, null);
    }

    /**
     * Retorna las entregas aplicando filtros opcionales: si idPedido o
     * idRepartidor es null, ese filtro no se aplica.
     */
    public List<Entrega> readAll(Integer idPedido, Integer idRepartidor) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT e.id, e.id_pedido, e.id_repartidor, e.fecha, e.hora, "
                        + "p.direccion, r.nombre "
                        + "FROM entregas e "
                        + "LEFT JOIN pedidos p ON e.id_pedido = p.id "
                        + "LEFT JOIN repartidores r ON e.id_repartidor = r.id "
                        + "WHERE 1 = 1");
        List<Integer> parametros = new ArrayList<>();
        if (idPedido != null) {
            sql.append(" AND e.id_pedido = ?");
            parametros.add(idPedido);
        }
        if (idRepartidor != null) {
            sql.append(" AND e.id_repartidor = ?");
            parametros.add(idRepartidor);
        }
        sql.append(" ORDER BY e.fecha DESC, e.hora DESC, e.id DESC");

        List<Entrega> lista = new ArrayList<>();
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setInt(i + 1, parametros.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    /** Actualiza pedido, repartidor, fecha y hora de la entrega indicada. */
    public boolean update(Entrega entrega) throws SQLException {
        String sql = "UPDATE entregas SET id_pedido = ?, id_repartidor = ?, fecha = ?, hora = ? WHERE id = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, entrega.getIdPedido());
            ps.setInt(2, entrega.getIdRepartidor());
            ps.setDate(3, Date.valueOf(entrega.getFecha()));
            ps.setTime(4, Time.valueOf(entrega.getHora()));
            ps.setInt(5, entrega.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina la entrega con el id indicado. */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM entregas WHERE id = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /** Convierte la fila actual del ResultSet (con JOIN) en una Entrega. */
    private Entrega mapear(ResultSet rs) throws SQLException {
        Date fecha = rs.getDate("fecha");
        Time hora = rs.getTime("hora");
        Entrega entrega = new Entrega(
                rs.getInt("id"),
                rs.getInt("id_pedido"),
                rs.getInt("id_repartidor"),
                fecha == null ? null : fecha.toLocalDate(),
                hora == null ? null : hora.toLocalTime());
        entrega.setDireccionPedido(rs.getString("direccion"));
        entrega.setNombreRepartidor(rs.getString("nombre"));
        return entrega;
    }
}
