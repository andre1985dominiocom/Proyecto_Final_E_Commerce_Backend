
package com.didistore.dao.impl.catalog;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.catalog.IWishlistDAO;
import com.didistore.model.catalog.Wishlist;
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
public class WishlistDAOImpl implements IWishlistDAO {

    @Override
    public boolean agregarAlWishlist(Wishlist wishlist) {
        
        String sql = "INSERT INTO Wishlist (Usuario_ID, Producto_ID) VALUES (?, ?)";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, wishlist.getusuarioId());
            ps.setInt(2, wishlist.getproductoId());
            
            int filasAfectadas = ps.executeUpdate();
            
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean eliminarDelWishlist(int usuarioId, int productoId) {
        
        String sql = "DELETE FROM Wishlist WHERE Usuario_ID = ? AND Producto_ID = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            ps.setInt(2, productoId);
            
            int filasAfectadas = ps.executeUpdate();
            
            return filasAfectadas > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Wishlist> obtenerWishlistPorUsuario(int usuarioId) {
        
        List<Wishlist> lista = new ArrayList<>();
        String sql = "SELECT ID_Wishlist, Usuario_ID, Producto_ID FROM Wishlist WHERE Usuario_ID = ?";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearWishlist(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public boolean perteneceAlWishlist(int usuarioId, int productoId) {
        
        String sql = "SELECT 1 FROM Wishlist WHERE Usuario_ID = ? AND Producto_ID = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            ps.setInt(2, productoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Wishlist mapearWishlist(ResultSet rs) throws SQLException {
        
        Wishlist wishlist = new Wishlist();
        wishlist.setidWishlist(rs.getInt("ID_Wishlist"));
        wishlist.setusuarioId(rs.getInt("Usuario_ID"));
        wishlist.setproductoId(rs.getInt("Producto_ID"));
        
        return wishlist;
    }   
}