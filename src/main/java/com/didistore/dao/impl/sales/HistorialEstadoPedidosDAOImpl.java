
package com.didistore.dao.impl.sales;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.sales.IHistorialEstadoPedidosDAO;
import com.didistore.model.sales.HistorialEstadoPedidos;
import com.didistore.model.sales.enums.EstadoPedidos;
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
public class HistorialEstadoPedidosDAOImpl implements IHistorialEstadoPedidosDAO {
    
    @Override
    public boolean registrarCambioEstado(HistorialEstadoPedidos historial) {
        String sql = "INSERT INTO Historial_Estados_Pedido (Pedido_ID, Estado_anterior, Estado_nuevo, Usuario_ID, Fecha_cambio, Notas) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, historial.getpedidoId());
            // Soporta que el estado anterior sea null (por ejemplo, cuando el pedido se crea por primera vez)
            ps.setString(2, historial.getestadoAnterior() != null ? historial.getestadoAnterior().name() : null);
            ps.setString(3, historial.getestadoNuevo() != null ? historial.getestadoNuevo().name() : null);
            ps.setInt(4, historial.getusuarioId());
            ps.setTimestamp(5, historial.getfechaCambio());
            ps.setString(6, historial.getnotas());
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public List<HistorialEstadoPedidos> listarHistorialPorPedido(int pedidoId) {
        // Ordenado por fecha de forma ascendente para mostrar la línea de tiempo correctamente
        String sql = "SELECT * FROM Historial_Estados_Pedido WHERE Pedido_ID = ? ORDER BY Fecha_cambio ASC";
        List<HistorialEstadoPedidos> lista = new ArrayList<>();
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, pedidoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearHistorial(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
     @Override
    public HistorialEstadoPedidos obtenerUltimoEstado(int pedidoId) {
        // Trae solo el registro más reciente basado en la fecha de cambio o el ID autoincremental
        String sql = "SELECT * FROM Historial_Estados_Pedido WHERE Pedido_ID = ? ORDER BY Fecha_cambio DESC LIMIT 1";
        HistorialEstadoPedidos historial = null;
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, pedidoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    historial = mapearHistorial(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historial;
    }
    
    private HistorialEstadoPedidos mapearHistorial(ResultSet rs) throws SQLException {
        
        HistorialEstadoPedidos historial = new HistorialEstadoPedidos();
        
        historial.setidHistorial(rs.getInt("ID_Historial"));
        historial.setpedidoId(rs.getInt("Pedido_ID"));
        
        String estadoAntStr = rs.getString("Estado_anterior");
        if (estadoAntStr != null) {
            historial.setestadoAnterior(EstadoPedidos.valueOf(estadoAntStr));
        }
        
        String estadoNueStr = rs.getString("Estado_nuevo");
        if (estadoNueStr != null) {
            historial.setestadoNuevo(EstadoPedidos.valueOf(estadoNueStr));
        }
        
        historial.setusuarioId(rs.getInt("Usuario_ID"));
        historial.setfechaCambio(rs.getTimestamp("Fecha_cambio"));
        historial.setnotas(rs.getString("Notas"));
        
        return historial;
    }
}