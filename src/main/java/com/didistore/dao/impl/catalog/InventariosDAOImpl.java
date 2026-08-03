
package com.didistore.dao.impl.catalog;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.catalog.IInventariosDAO;
import com.didistore.model.catalog.Inventarios;
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
public class InventariosDAOImpl implements IInventariosDAO {

    @Override
    public List<Inventarios> listarInventario() {
        
        List<Inventarios> lista = new ArrayList<>();

        String sql = "SELECT ID_Inventario, Producto_ID, Stock_actual, Stock_minimo, Stock_reservado, Fecha_creacion, Fecha_actualizacion FROM Inventarios";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Inventarios inventario = new Inventarios();

                inventario.setidInventario(rs.getInt("ID_Inventario"));
                inventario.setproductoId(rs.getInt("Producto_ID"));
                inventario.setstockActual(rs.getInt("Stock_actual"));
                inventario.setstockMinimo(rs.getInt("Stock_minimo"));
                inventario.setstockReservado(rs.getInt("Stock_reservado"));
                inventario.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                inventario.setfechaActualizacion(rs.getTimestamp("fecha_actualizacion"));

                lista.add(inventario);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar inventario: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<Inventarios> listarPorProducto(int productoId) {
        
        List<Inventarios> lista = new ArrayList<>();
        
        String sql = "SELECT ID_Inventario, Producto_ID, Stock_actual, Stock_minimo, Stock_reservado, Fecha_creacion, Fecha_actualizacion FROM Inventarios WHERE Producto_ID = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, productoId);
            
            try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Inventarios inventario = new Inventarios();

                inventario.setidInventario(rs.getInt("ID_Inventario"));
                inventario.setproductoId(rs.getInt("Producto_ID"));
                inventario.setstockActual(rs.getInt("Stock_actual"));
                inventario.setstockMinimo(rs.getInt("Stock_minimo"));
                inventario.setstockReservado(rs.getInt("Stock_reservado"));
                inventario.setfechaCreacion(rs.getTimestamp("fecha_Creacion"));
                inventario.setfechaActualizacion(rs.getTimestamp("fecha_Actualizacion"));

                lista.add(inventario);
            }
        }
        } catch (SQLException e) {
            System.err.println("Error al listar inventario por ID: " + e.getMessage());
        }  
        return lista;
    }

    @Override
    public Inventarios consultarInventarioPorId(int idInventario) {
        
        String sql = "SELECT ID_Inventario, Producto_ID, Stock_actual, Stock_minimo, Stock_reservado, Fecha_creacion, Fecha_actualizacion FROM Inventarios WHERE ID_Inventario = ?";

        Inventarios inventario = null;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idInventario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    inventario = new Inventarios();

                inventario.setidInventario(rs.getInt("ID_Inventario"));
                inventario.setproductoId(rs.getInt("Producto_ID"));
                inventario.setstockActual(rs.getInt("Stock_actual"));
                inventario.setstockMinimo(rs.getInt("Stock_minimo"));
                inventario.setstockReservado(rs.getInt("Stock_reservado"));
                inventario.setfechaCreacion(rs.getTimestamp("fecha_Creacion"));
                inventario.setfechaActualizacion(rs.getTimestamp("fecha_Actualizacion"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar inventario por ID: " + e.getMessage());
        }
        return inventario;
    }

    @Override
    public void insertarInventario(Inventarios inventario) {
        
        String sql = "INSERT INTO Inventarios (Producto_ID, Stock_actual, Stock_minimo, Stock_reservado, Fecha_creacion, Fecha_actualizacion) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setInt(1, inventario.getproductoId());
                ps.setInt(2, inventario.getstockActual());
                ps.setInt(3, inventario.getstockMinimo());
                ps.setInt(4, inventario.getstockReservado());
                ps.setTimestamp(5, inventario.getfechaCreacion());
                ps.setTimestamp(6, inventario.getfechaActualizacion());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Inventario insertado correctamente en la BD!");
                }                          
            } catch (SQLException e) {
                System.err.println("Error al listar inventario: " + e.getMessage());
        }
    }

    @Override
    public boolean actualizarInventario(Inventarios inventario) {
        
        String sql = "UPDATE Inventarios SET producto_id = ?, Stock_actual = ?, Stock_minimo = ?, Stock_reservado = ?, Fecha_creacion = ?, Fecha_actualizacion = ? WHERE ID_Inventario = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, inventario.getproductoId());
            ps.setInt(2, inventario.getstockActual());
            ps.setInt(3, inventario.getstockMinimo());
            ps.setInt(4, inventario.getstockReservado());
            ps.setTimestamp(5, inventario.getfechaCreacion());
            ps.setTimestamp(6, inventario.getfechaActualizacion());
            ps.setInt(7, inventario.getidInventario());

            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Inventario actualizado correctamente en la BD!");
            }
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar inventario: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminarInventario(int idInventario) {
        
        String sql = "DELETE FROM Inventarios WHERE ID_Inventario = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idInventario);

            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Inventario eliminado correctamente en la BD!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar inventario: " + e.getMessage());
        }
        return false;
    }
}