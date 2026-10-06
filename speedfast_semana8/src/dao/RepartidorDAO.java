package dao;

import conexion.ConexionBD;

import model.Repartidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a datos de la tabla repartidores (CRUD). Todas las consultas usan
 * PreparedStatement y los recursos se cierran con try-with-resources.
 * Las SQLException se propagan para que la vista muestre un mensaje claro.
 */
public class RepartidorDAO {

    /** Inserta un repartidor y deja en el objeto el id generado por MySQL. */
    public boolean create(Repartidor repartidor) throws SQLException {
        String sql = "INSERT INTO repartidores (nombre) VALUES (?)";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, repartidor.getNombre());
            boolean insertado = ps.executeUpdate() > 0;
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    repartidor.setId(claves.getInt(1));
                }
            }
            return insertado;
        }
    }

    /** Retorna todos los repartidores ordenados por id. */
    public List<Repartidor> readAll() throws SQLException {
        String sql = "SELECT id, nombre FROM repartidores ORDER BY id";
        List<Repartidor> lista = new ArrayList<>();
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Repartidor(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return lista;
    }

    /** Actualiza el nombre del repartidor indicado. Retorna true si se modificó una fila. */
    public boolean update(Repartidor repartidor) throws SQLException {
        String sql = "UPDATE repartidores SET nombre = ? WHERE id = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, repartidor.getNombre());
            ps.setInt(2, repartidor.getId());
            return ps.executeUpdate() > 0;
        }
    }

    /** Elimina el repartidor con el id indicado. Retorna true si se eliminó una fila. */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM repartidores WHERE id = ?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
