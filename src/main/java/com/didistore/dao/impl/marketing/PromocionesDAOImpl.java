package com.didistore.dao.impl.marketing;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.marketing.IPromocionesDAO;
import com.didistore.model.marketing.Promociones;
import com.didistore.model.marketing.enums.EstadoPromociones;
import com.didistore.model.marketing.enums.TipoDescuentos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Implementación JDBC de IPromocionesDAO sobre la tabla Promociones (RF-09 / banner).
public class PromocionesDAOImpl implements IPromocionesDAO {

    private static final String COLS =
            "ID_Promocion, nombre_promocion, descripcion, tipo_descuento, valor_descuento, "
            + "producto_id, categoria_id, fecha_inicio, fecha_fin, estado, aplica_con_otros, "
            + "fecha_creacion, fecha_actualizacion";

    @Override
    public List<Promociones> listarPromociones() {
        List<Promociones> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM Promociones ORDER BY fecha_creacion DESC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar promociones: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Promociones buscarPorId(int idPromocion) {
        String sql = "SELECT " + COLS + " FROM Promociones WHERE ID_Promocion = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPromocion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar promoción: " + e.getMessage());
        }
        return null;
    }

    // Promociones vigentes y activas — fuente del banner de promociones (CU-CLI-01).
    @Override
    public List<Promociones> listarActivas() {
        List<Promociones> lista = new ArrayList<>();
        String sql = "SELECT " + COLS + " FROM Promociones "
                + "WHERE estado = 'Activo' AND fecha_inicio <= NOW() AND fecha_fin >= NOW() "
                + "ORDER BY fecha_inicio DESC";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar promociones activas: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public boolean insertarPromocion(Promociones promocion) {
        String sql = "INSERT INTO Promociones (nombre_promocion, descripcion, tipo_descuento, "
                + "valor_descuento, producto_id, categoria_id, fecha_inicio, fecha_fin, estado, "
                + "aplica_con_otros) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, promocion.getnombrePromocion());
            ps.setString(2, promocion.getdescripcion());
            ps.setString(3, promocion.gettipoDescuento().name());
            ps.setDouble(4, promocion.getvalorDescuento());
            if (promocion.getproductoId() > 0) {
                ps.setInt(5, promocion.getproductoId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            if (promocion.getcategoriaId() > 0) {
                ps.setInt(6, promocion.getcategoriaId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.setTimestamp(7, Timestamp.valueOf(promocion.getfechaInicio()));
            ps.setTimestamp(8, Timestamp.valueOf(promocion.getfechaFin()));
            ps.setString(9, promocion.getestado() != null ? promocion.getestado().name() : "Activo");
            ps.setInt(10, promocion.getaplicaConOtros());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar promoción: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarPromocion(Promociones promocion) {
        String sql = "UPDATE Promociones SET nombre_promocion = ?, descripcion = ?, "
                + "tipo_descuento = ?, valor_descuento = ?, producto_id = ?, categoria_id = ?, "
                + "fecha_inicio = ?, fecha_fin = ?, estado = ?, aplica_con_otros = ? "
                + "WHERE ID_Promocion = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, promocion.getnombrePromocion());
            ps.setString(2, promocion.getdescripcion());
            ps.setString(3, promocion.gettipoDescuento().name());
            ps.setDouble(4, promocion.getvalorDescuento());
            if (promocion.getproductoId() > 0) {
                ps.setInt(5, promocion.getproductoId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            if (promocion.getcategoriaId() > 0) {
                ps.setInt(6, promocion.getcategoriaId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.setTimestamp(7, Timestamp.valueOf(promocion.getfechaInicio()));
            ps.setTimestamp(8, Timestamp.valueOf(promocion.getfechaFin()));
            ps.setString(9, promocion.getestado() != null ? promocion.getestado().name() : "Activo");
            ps.setInt(10, promocion.getaplicaConOtros());
            ps.setInt(11, promocion.getidPromocion());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar promoción: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminarPromocion(int idPromocion) {
        String sql = "DELETE FROM Promociones WHERE ID_Promocion = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idPromocion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar promoción: " + e.getMessage());
            return false;
        }
    }

    private Promociones mapear(ResultSet rs) throws SQLException {
        Promociones p = new Promociones();
        p.setidPromocion(rs.getInt("ID_Promocion"));
        p.setnombrePromocion(rs.getString("nombre_promocion"));
        p.setdescripcion(rs.getString("descripcion"));

        String tipo = rs.getString("tipo_descuento");
        if (tipo != null && !tipo.isEmpty()) {
            p.settipoDescuento(TipoDescuentos.valueOf(tipo));
        }

        p.setvalorDescuento(rs.getDouble("valor_descuento"));
        p.setproductoId(rs.getInt("producto_id"));
        p.setcategoriaId(rs.getInt("categoria_id"));

        Timestamp inicio = rs.getTimestamp("fecha_inicio");
        Timestamp fin = rs.getTimestamp("fecha_fin");
        if (inicio != null) p.setfechaInicio(inicio.toLocalDateTime());
        if (fin != null) p.setfechaFin(fin.toLocalDateTime());

        String estado = rs.getString("estado");
        if (estado != null && !estado.isEmpty()) {
            p.setestado(EstadoPromociones.valueOf(estado));
        }

        p.setaplicaConOtros(rs.getInt("aplica_con_otros"));
        p.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
        p.setfechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
        return p;
    }
}
