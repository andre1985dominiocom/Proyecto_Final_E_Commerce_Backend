
package com.didistore.dao.impl.sales;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.sales.ICarritoComprasDAO;
import com.didistore.model.sales.CarritoCompras;
import com.didistore.model.sales.ItemCarritos;
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
public class CarritoComprasDAOImpl implements ICarritoComprasDAO {

    // Métodos carritos de compras
    @Override
    public boolean crearCarrito(CarritoCompras carrito) {
        
        String sql = "INSERT INTO Carrito_compras (Usuario_ID, Sesion_ID) VALUES (?, ?)";
        
        try (Connection con = Conexion.getConexion();
        PreparedStatement ps = con.prepareStatement(sql)) {
            
            if (carrito.getusuarioId() != 0) {
                ps.setInt(1, carrito.getusuarioId());
                ps.setNull(2, java.sql.Types.VARCHAR);
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
                ps.setString(2, carrito.getsesionId());
            }
            
            return ps.executeUpdate() > 0;           
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }  

    @Override
    public CarritoCompras buscarPorUsuario(int usuarioId) {
        
        String sql = "SELECT * FROM Carrito_compras WHERE Usuario_ID = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCarrito(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public CarritoCompras buscarPorSesion(String sesionId) {
        
        String sql = "SELECT * FROM Carrito_compras WHERE Sesion_ID = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, sesionId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCarrito(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public boolean actualizarPorFecha(int idCarrito) {
        
        String sql = "UPDATE Carrito_Compras SET Fecha_actualizacion = CURRENT_TIMESTAMP WHERE ID_Carrito = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idCarrito);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean eliminarCarrito(int idCarrito) {
        
        vaciarCarrito(idCarrito);
        
        String sql = "DELETE FROM Carrito_Compras WHERE ID_Carrito = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idCarrito);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Métodos Item de carrito
    @Override
    public boolean agregarItem(ItemCarritos item) {
        
        String sql = "INSERT INTO Item_Carrito (Carrito_ID, Producto_ID, Cantidad, Precio_unitario) VALUES (?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, item.getcarritoId());
            ps.setInt(2, item.getproductoId());
            ps.setInt(3, item.getcantidad());
            ps.setDouble(4, item.getprecioUnitario());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean actualizarCantidad(int idItem, int cantidad) {
        
        String sql = "UPDATE Item_Carrito SET Cantidad = Cantidad + ? WHERE ID_Item = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, cantidad);
            ps.setInt(2, idItem);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminarItem(int itemId) {
        
        String sql = "DELETE FROM Item_Carrito WHERE ID_Item = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<ItemCarritos> listarItems(int carritoId) {
        
        List<ItemCarritos> lista = new ArrayList<>();
        String sql = "SELECT " +
                     "i.ID_Item," +
                     "i.Carrito_ID," +
                     "i.Producto_ID," +
                     "i.Cantidad," +
                     "i.Precio_unitario," +
                     "i.Subtotal," +
                     "p.nombre_Producto " +
                     "FROM Item_Carrito i " +
                     "INNER JOIN Productos p " +
                     "ON p.ID_Producto = i.Producto_ID " +
                     "WHERE i.Carrito_ID = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, carritoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ItemCarritos item = new ItemCarritos();
                    item.setidItem(rs.getInt("ID_Item"));
                    item.setcarritoId(rs.getInt("Carrito_ID"));
                    item.setproductoId(rs.getInt("Producto_ID"));
                    item.setcantidad(rs.getInt("Cantidad"));
                    item.setprecioUnitario(rs.getDouble("Precio_unitario"));
                    item.setnombreProducto(rs.getString("nombre_Producto"));
                    
                    // Opcional: si tu objeto 'ItemCarritos' tiene la propiedad subtotal,
                    // puedes recuperarla directamente de la columna calculada por MySQL:
                    // item.setSubtotal(rs.getDouble("Subtotal"));
                    
                    lista.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
       return lista;
    }

    @Override
    public boolean vaciarCarrito(int carritoId) {
        
        String sql = "DELETE FROM Item_Carrito WHERE Carrito_ID = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, carritoId);
            return ps.executeUpdate() > 0; 
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public double obtenerTotal(int carritoId) {
        
        String sql = "SELECT SUM(Subtotal) AS Total FROM Item_Carrito WHERE Carrito_ID = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, carritoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("Total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }    

    private CarritoCompras mapearCarrito(ResultSet rs) throws SQLException {
        
        CarritoCompras carrito = new CarritoCompras();
        carrito.setidCarrito(rs.getInt("ID_Carrito"));
        
        // Manejo del campo NULL para el usuario
        int usuarioId = rs.getInt("Usuario_ID");
        if (!rs.wasNull()) {
            carrito.setusuarioId(usuarioId);
        }
        
        carrito.setsesionId(rs.getString("Sesion_ID"));
        return carrito;
    }
}