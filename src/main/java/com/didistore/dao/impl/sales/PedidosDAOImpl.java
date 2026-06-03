
package com.didistore.dao.impl.sales;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.sales.IPedidosDAO;
import com.didistore.model.sales.DetallesPedidos;
import com.didistore.model.sales.Pedidos;
import com.didistore.model.sales.enums.EstadoPedidos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PedidosDAOImpl implements IPedidosDAO {

    @Override
    public boolean crearPedido(Pedidos pedido, List<DetallesPedidos> detalles) {
        
        String sqlPedido = "INSERT INTO Pedidos (Numero_pedido, Usuario_ID, Direccion_envio_ID, Estado_pedido, Subtotal, Descuento, IVA, Costo_envio, Monto_Total, Cupon_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO Detalles_Pedidos (Pedido_ID, Producto_ID, Cantidad, Precio_unitario, Subtotal) VALUES (?, ?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement psPedido = null;
        PreparedStatement psDetalle = null;
        ResultSet rs = null;

        try {
            con = Conexion.getConexion();
            // Desactivamos el autoCommit para que sea una transacción única y segura
            con.setAutoCommit(false);
    
            // Inicializamos psPedido solicitando las llaves generadas (ID_Pedido)
            psPedido = con.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
    
            psPedido.setString(1, pedido.getnumeroPedido());
            psPedido.setInt(2, pedido.getusuarioId());
            psPedido.setInt(3, pedido.getdireccionEnvioId());
    
            // Si getEstadoPedido() ya retorna el Enum 'EstadoPedidos', usamos .name()
            psPedido.setString(4, pedido.getestadoPedido().name()); 
            psPedido.setDouble(5, pedido.getsubTotal());
            psPedido.setDouble(6, pedido.getdescuento());
            psPedido.setDouble(7, pedido.getiva());
            psPedido.setDouble(8, pedido.getcostoEnvio());
            psPedido.setDouble(9, pedido.getmontoTotal()); 
    
            // Corregido: cambiados los "ps" erróneos por "psPedido"
            if (pedido.getcuponId() != 0) {
                psPedido.setInt(10, pedido.getcuponId());
            } else {
                psPedido.setNull(10, java.sql.Types.INTEGER);
            }

            int filasAfectadas = psPedido.executeUpdate();
            
            if (filasAfectadas == 0) {
                throw new SQLException("Error al insertar la cabecera del pedido.");
            }

            // Corregido: Declaración explícita del ResultSet (rs)
            rs = psPedido.getGeneratedKeys();
            int idPedidoGenerado = 0;
            
            if (rs.next()) {
                idPedidoGenerado = rs.getInt(1);
            } else {
                throw new SQLException("No se pudo obtener el ID del pedido generado.");
            }

            // 2. Insertar todos los detalles asociados (Corregida la declaración)
            psDetalle = con.prepareStatement(sqlDetalle);
            
            for (DetallesPedidos detalle : detalles) {
                    psDetalle.setInt(1, idPedidoGenerado);
                    psDetalle.setInt(2, detalle.getproductoId());
                    psDetalle.setInt(3, detalle.getcantidad());
                    psDetalle.setDouble(4, detalle.getprecioUnitario());
                    psDetalle.setDouble(5, detalle.getsubTotal()); 
                    psDetalle.addBatch(); // Empaquetamos para procesar en lote
                }

                psDetalle.executeBatch();

            // Si todo se ejecutó sin errores, guardamos los cambios definitivamente
            con.commit();
            return true;

        } catch (SQLException e) {
            // Si algo falla en cualquier punto, hacemos rollback total
            if (con != null) {
                try {
                    con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
            e.printStackTrace();
            
        } finally {
            // El bloque finally ahora cerrará de forma segura todos los objetos abiertos
            try {
                if (rs != null) rs.close();
                if (psPedido != null) psPedido.close();
                if (psDetalle != null) psDetalle.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public Pedidos buscarPorId(int idPedido) {
        
        String sql = "SELECT * FROM Pedidos WHERE ID_Pedido = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPedido);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPedido(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Pedidos buscarPorNumeroPedido(String numeroPedido) {
        
        String sql = "SELECT * FROM Pedidos WHERE Numero_pedido = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, numeroPedido);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPedido(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Pedidos> listarPorUsuario(int usuarioId) {
        
        List<Pedidos> lista = new ArrayList<>();
        // Ordenamos por Fecha_pedido descendente para mostrar primero los más recientes
        String sql = "SELECT * FROM Pedidos WHERE Usuario_ID = ? ORDER BY Fecha_pedido DESC";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPedido(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean actualizarEstado(int idPedido, EstadoPedidos nuevoEstado) {
        
        String sql = "UPDATE Pedidos SET Estado_pedido = ? WHERE ID_Pedido = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            // Convertimos el objeto enum de Java a String usando .name() para MySQL
            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, idPedido);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<DetallesPedidos> listarDetallesPorPedido(int pedidoId) {
        
        List<DetallesPedidos> lista = new ArrayList<>();
        String sql = "SELECT * FROM Detalles_Pedidos WHERE Pedido_ID = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, pedidoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DetallesPedidos detalle = new DetallesPedidos();
                    // Mapeo exacto según tu script (ID_Detalle, Pedido_ID, Producto_ID, etc.)
                    detalle.setidDetalle(rs.getInt("ID_Detalle"));
                    detalle.setpedidoId(rs.getInt("Pedido_ID"));
                    detalle.setproductoId(rs.getInt("Producto_ID"));
                    detalle.setcantidad(rs.getInt("Cantidad"));
                    detalle.setprecioUnitario(rs.getDouble("Precio_unitario"));
                    detalle.setsubTotal(rs.getDouble("Subtotal")); // Snapshot financiero de la BD
                    
                    lista.add(detalle);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return  lista;
    }

    private Pedidos mapearPedido(ResultSet rs) throws SQLException {
        Pedidos pedido = new Pedidos();
        pedido.setidPedido(rs.getInt("ID_Pedido"));
        pedido.setnumeroPedido(rs.getString("Numero_pedido"));
        pedido.setusuarioId(rs.getInt("Usuario_ID"));
        pedido.setdireccionEnvioId(rs.getInt("Direccion_envio_ID"));
        
        // Convertimos el String del ENUM de la BD de vuelta al Enum de Java
        String estadoString = rs.getString("Estado_pedido");
        if (estadoString != null) {
            pedido.setestadoPedido(EstadoPedidos.valueOf(estadoString));
        }
        
        pedido.setsubTotal(rs.getDouble("Subtotal"));
        pedido.setdescuento(rs.getDouble("Descuento"));
        pedido.setiva(rs.getDouble("IVA"));
        pedido.setcostoEnvio(rs.getDouble("Costo_envio"));
        pedido.setmontoTotal(rs.getDouble("Monto_Total"));
        
        // Manejo de nulos seguro para el Cupon_ID
        int cuponId = rs.getInt("Cupon_ID");
        if (!rs.wasNull()) {
            pedido.setcuponId(cuponId);
        }    
        return pedido;
    }
}