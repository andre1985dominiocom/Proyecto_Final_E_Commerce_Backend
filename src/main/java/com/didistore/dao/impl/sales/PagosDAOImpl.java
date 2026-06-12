
package com.didistore.dao.impl.sales;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.sales.IPagosDAO;
import com.didistore.model.sales.Pagos;
import com.didistore.model.sales.enums.EstadoPagos;
import com.didistore.model.sales.enums.MetodoPagos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PagosDAOImpl implements IPagosDAO {

    @Override
    public boolean registrarPago(Pagos pago) {
        
        String sql = "INSERT INTO Pagos (Pedido_ID, Metodo_Pago, Estado_Pago, Monto, Referencia_transaccion, Referencia_interna, Datos_pasarela, Fecha_pago, Fecha_creacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
        PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, pago.getpedidoId());
            ps.setString(2, pago.getmetodoPago() != null ? pago.getmetodoPago().name() : null);
            ps.setString(3, pago.getestadoPago() != null ? pago.getestadoPago().name() : null);
            ps.setDouble(4, pago.getmonto());
            ps.setString(5, pago.getreferenciaTransaccion());
            ps.setString(6, pago.getreferenciaInterna());
            ps.setString(7, pago.getdatosPasarela());
            ps.setTimestamp(8, pago.getfechaPago());
            ps.setTimestamp(9, pago.getfechaCreacion());
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;                  
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean cancelarPago(int idPago) {
        
        String sql = "UPDATE Pagos SET Estado_pago = ? = WHERE ID_Pago = ?";
        
        try (Connection con = Conexion.getConexion();
        PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, EstadoPagos.Expirado.name());
            ps.setInt(2, idPago);
            
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }      
    }

    @Override
    public Pagos buscarPorId(int idPago) {
        
        String sql = "SELECT * FROM Pagos WHERE ID_Pago = ?";
        Pagos pago = null;
        
         try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
             
             ps.setInt(1, idPago);
             
             try (ResultSet rs = ps.executeQuery()) {
                 if (rs.next()) {
                     pago = mapearPagos(rs);
                 }
             }
         } catch (SQLException e) {
             e.printStackTrace();
         }
        return pago;
    }

    @Override
    public List<Pagos> listarPagosPorVenta(int pedidoId) {
        
        String sql = "SELECT * FROM Pagos WHERE Pedido_ID = ?";
        List<Pagos> listaPagos = new ArrayList<>();
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, pedidoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaPagos.add(mapearPagos(rs));
                }
            }   
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listaPagos;
    }

    private Pagos mapearPagos(ResultSet rs) throws SQLException {
        
        Pagos pago = new Pagos();
        
        pago.setidPago(rs.getInt("ID_Pago"));
        pago.setpedidoId(rs.getInt("Pedido_ID"));
        String metodoStr = rs.getString("Metodo_pago");
        if (metodoStr != null) {
            pago.setmetodoPago(MetodoPagos.valueOf(metodoStr));
        }
        String estadoStr = rs.getString("Estado_pago");
        if (estadoStr != null) {
            pago.setestadoPago(EstadoPagos.valueOf(estadoStr));
        }
        pago.setmonto(rs.getDouble("Monto"));
        pago.setreferenciaTransaccion(rs.getString("Referencia_transaccion"));
        pago.setreferenciaInterna(rs.getString("Referencia_interna"));
        pago.setdatosPasarela(rs.getString("Referencia_pasarela"));
        pago.setfechaPago(rs.getTimestamp("Fecha_pago"));
        pago.setfechaCreacion(rs.getTimestamp("Fecha_creacion"));
        
        return pago;
    }
}