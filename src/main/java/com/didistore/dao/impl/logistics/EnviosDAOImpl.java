
package com.didistore.dao.impl.logistics;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.logistics.IEnviosDAO;
import com.didistore.model.logistics.Envios;
import com.didistore.model.logistics.enums.EstadoEnvios;
import com.didistore.model.logistics.enums.TransportadoraEnvios;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Implementación de la interfaz IEnviosDAO para la tabla Envios.
public class EnviosDAOImpl implements IEnviosDAO {

    private Envios mapear(ResultSet rs) throws SQLException {
        Envios envio = new Envios();
        envio.setidEnvio(rs.getInt("ID_Envio"));
        envio.setpedidoId(rs.getInt("Pedido_ID"));
        int dir = rs.getInt("Direccion_ID");
        envio.setdireccionId(rs.wasNull() ? 0 : dir);
        envio.setmetodoEnvio(rs.getString("metodo_envio"));
        String transp = rs.getString("transportadora");
        if (transp != null && !transp.isEmpty()) {
            try {
                envio.settransportadora(TransportadoraEnvios.valueOf(transp));
            } catch (IllegalArgumentException ignored) {}
        }
        envio.setnumeroGuia(rs.getString("numero_guia"));
        String estado = rs.getString("estado");
        if (estado != null && !estado.isEmpty()) {
            try {
                envio.setestado(EstadoEnvios.valueOf(estado));
            } catch (IllegalArgumentException ignored) {
                envio.setestado(EstadoEnvios.Pendiente);
            }
        }
        envio.setfechaDespacho(rs.getTimestamp("fecha_despacho"));
        envio.setfechaEntregaEstimada(rs.getTimestamp("fecha_entrega_estimada"));
        envio.setfechaEntregaReal(rs.getTimestamp("fecha_entrega_real"));
        envio.setobservaciones(rs.getString("observaciones"));
        envio.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
        envio.setfechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
        return envio;
    }

    @Override
    public Envios buscarPorPedido(int pedidoId) {
        String sql = "SELECT * FROM Envios WHERE Pedido_ID = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Envios buscarPorId(int idEnvio) {
        String sql = "SELECT * FROM Envios WHERE ID_Envio = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEnvio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Envios> listarPorUsuario(int usuarioId) {
        List<Envios> lista = new ArrayList<>();
        String sql = "SELECT e.* FROM Envios e "
                + "INNER JOIN Pedidos p ON e.Pedido_ID = p.ID_Pedido "
                + "WHERE p.Usuario_ID = ? ORDER BY e.fecha_creacion DESC";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean crearEnvio(Envios envio) {
        String sql = "INSERT INTO Envios (Pedido_ID, Direccion_ID, metodo_envio, transportadora, numero_guia, estado, "
                + "fecha_despacho, fecha_entrega_estimada, fecha_entrega_real, observaciones) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, envio.getpedidoId());
            if (envio.getdireccionId() > 0) {
                ps.setInt(2, envio.getdireccionId());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, envio.getmetodoEnvio());
            ps.setString(4, envio.gettransportadora() != null ? envio.gettransportadora().name() : null);
            ps.setString(5, envio.getnumeroGuia());
            ps.setString(6, envio.getestado() != null ? envio.getestado().name() : EstadoEnvios.Pendiente.name());
            ps.setTimestamp(7, envio.getfechaDespacho());
            ps.setTimestamp(8, envio.getfechaEntregaEstimada());
            ps.setTimestamp(9, envio.getfechaEntregaReal());
            ps.setString(10, envio.getobservaciones());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // Pedido_ID UNIQUE: si ya existe envío, no es error fatal en checkout
            System.err.println("Error al crear envío: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarEnvio(Envios envio) {
        String sql = "UPDATE Envios SET Direccion_ID = ?, metodo_envio = ?, transportadora = ?, numero_guia = ?, "
                + "estado = ?, fecha_despacho = ?, fecha_entrega_estimada = ?, fecha_entrega_real = ?, observaciones = ? "
                + "WHERE ID_Envio = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (envio.getdireccionId() > 0) {
                ps.setInt(1, envio.getdireccionId());
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setString(2, envio.getmetodoEnvio());
            ps.setString(3, envio.gettransportadora() != null ? envio.gettransportadora().name() : null);
            ps.setString(4, envio.getnumeroGuia());
            ps.setString(5, envio.getestado() != null ? envio.getestado().name() : null);
            ps.setTimestamp(6, envio.getfechaDespacho());
            ps.setTimestamp(7, envio.getfechaEntregaEstimada());
            ps.setTimestamp(8, envio.getfechaEntregaReal());
            ps.setString(9, envio.getobservaciones());
            ps.setInt(10, envio.getidEnvio());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean actualizarEstado(int idEnvio, String estado) {
        String sql = "UPDATE Envios SET estado = ?, fecha_actualizacion = CURRENT_TIMESTAMP WHERE ID_Envio = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idEnvio);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
