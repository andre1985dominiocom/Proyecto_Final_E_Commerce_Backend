package com.didistore.dao.impl.marketing;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.marketing.ICuponesDAO;
import com.didistore.model.marketing.Cupones;
import com.didistore.model.marketing.enums.EstadoCupones;
import com.didistore.model.marketing.enums.TipoDescuentos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Implementación JDBC de ICuponesDAO sobre la tabla Cupones (RF-09).
public class CuponesDAOImpl implements ICuponesDAO {

    private static final String COLS =
            "ID_Cupon, codigo, tipo_descuento, valor_descuento, monto_minimo, "
            + "cantidad_maxima_usos, cantidad_usos_actuales, fecha_inicio, fecha_fin, "
            + "estado, fecha_creacion";

    @Override
    public List<Cupones> listarCupones() {
        List<Cupones> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM Cupones ORDER BY fecha_creacion DESC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar cupones: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Cupones buscarPorId(int idCupon) {
        String sql = "SELECT " + COLS + " FROM Cupones WHERE ID_Cupon = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCupon);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cupón por id: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Cupones buscarPorCodigo(String codigo) {
        String sql = "SELECT " + COLS + " FROM Cupones WHERE codigo = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar cupón por código: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean insertarCupon(Cupones cupon) {
        String sql = "INSERT INTO Cupones (codigo, tipo_descuento, valor_descuento, monto_minimo, "
                + "cantidad_maxima_usos, cantidad_usos_actuales, fecha_inicio, fecha_fin, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cupon.getcodigo());
            ps.setString(2, cupon.gettipoDescuento().name());
            ps.setDouble(3, cupon.getvalorDescuento());
            ps.setDouble(4, cupon.getmontoMinimo());
            ps.setInt(5, cupon.getcantidadMaximaUsos());
            ps.setInt(6, cupon.getcantidadUsosActuales());
            ps.setTimestamp(7, Timestamp.valueOf(cupon.getfechaInicio()));
            ps.setTimestamp(8, Timestamp.valueOf(cupon.getfechaFin()));
            ps.setString(9, cupon.getestado() != null ? cupon.getestado().name() : "Activo");

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar cupón: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarCupon(Cupones cupon) {
        String sql = "UPDATE Cupones SET codigo = ?, tipo_descuento = ?, valor_descuento = ?, "
                + "monto_minimo = ?, cantidad_maxima_usos = ?, fecha_inicio = ?, fecha_fin = ?, "
                + "estado = ? WHERE ID_Cupon = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cupon.getcodigo());
            ps.setString(2, cupon.gettipoDescuento().name());
            ps.setDouble(3, cupon.getvalorDescuento());
            ps.setDouble(4, cupon.getmontoMinimo());
            ps.setInt(5, cupon.getcantidadMaximaUsos());
            ps.setTimestamp(6, Timestamp.valueOf(cupon.getfechaInicio()));
            ps.setTimestamp(7, Timestamp.valueOf(cupon.getfechaFin()));
            ps.setString(8, cupon.getestado() != null ? cupon.getestado().name() : "Activo");
            ps.setInt(9, cupon.getidCupon());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar cupón: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarCupon(int idCupon) {
        String sql = "DELETE FROM Cupones WHERE ID_Cupon = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCupon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar cupón: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean incrementarUsos(int idCupon) {
        String sql = "UPDATE Cupones SET cantidad_usos_actuales = cantidad_usos_actuales + 1 "
                + "WHERE ID_Cupon = ? AND cantidad_usos_actuales < cantidad_maxima_usos";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCupon);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al incrementar usos del cupón: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean codigoExiste(String codigo) {
        String sql = "SELECT 1 FROM Cupones WHERE codigo = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar código de cupón: " + e.getMessage());
            return false;
        }
    }

    private Cupones mapear(ResultSet rs) throws SQLException {
        Cupones c = new Cupones();
        c.setidCupon(rs.getInt("ID_Cupon"));
        c.setcodigo(rs.getString("codigo"));

        String tipo = rs.getString("tipo_descuento");
        if (tipo != null && !tipo.isEmpty()) {
            c.settipoDescuento(TipoDescuentos.valueOf(tipo));
        }

        c.setvalorDescuento(rs.getDouble("valor_descuento"));
        c.setmontoMinimo(rs.getDouble("monto_minimo"));
        c.setcantidadMaximaUsos(rs.getInt("cantidad_maxima_usos"));
        c.setcantidadUsosActuales(rs.getInt("cantidad_usos_actuales"));

        Timestamp inicio = rs.getTimestamp("fecha_inicio");
        Timestamp fin = rs.getTimestamp("fecha_fin");
        if (inicio != null) c.setfechaInicio(inicio.toLocalDateTime());
        if (fin != null) c.setfechaFin(fin.toLocalDateTime());

        String estado = rs.getString("estado");
        if (estado != null && !estado.isEmpty()) {
            c.setestado(EstadoCupones.valueOf(estado));
        }

        c.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
        return c;
    }
}
