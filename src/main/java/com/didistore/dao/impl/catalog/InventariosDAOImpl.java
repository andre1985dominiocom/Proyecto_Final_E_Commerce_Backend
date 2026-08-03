
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

// Implementación de la interfaz IInventariosDAO para realizar operaciones CRUD en la tabla Inventarios
public class InventariosDAOImpl implements IInventariosDAO {

    // Implementación del método listarInventario() para obtener todos los registros de la tabla Inventarios
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

    // Implementación del método listarPorProducto() para obtener los registros
    // de la tabla Inventarios filtrados por el ID del producto
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

    // Implementación del método consultarInventarioPorId() para obtener un registro
    @Override
    public Inventarios consultarInventarioPorId(int idInventario) {
        
<<<<<<< HEAD
        String sql = "SELECT ID_Inventario, Producto_ID, Stock_actual, Stock_minimo, Stock_reservado, Fecha_creacion, Fecha_actualizacion FROM Inventarios WHERE ID_Inventario = ?";
=======
        String sql = "SELECT Producto_ID, Stock_actual, Stock_minimo, Stock_reservado, Fecha_creacion, Fecha_actualizacion  FROM Inventarios WHERE ID_inventario = ?";
>>>>>>> origin/main

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

    // Implementación del método insertarInventario() para insertar un nuevo registro en la tabla Inventarios
    @Override
    public boolean insertarInventario(Inventarios inventario) {
        
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
                System.err.println("Error al insertar inventario: " + e.getMessage());
        }
        return false;
    }

    // Implementación del método actualizarInventario() para actualizar un registro existente en la tabla Inventarios
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

    // Implementación del método eliminarInventario() para eliminar un registro existente en la tabla Inventarios
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
    
    // Implementación del método obtenerStockDisponible() para obtener el stock disponible de un producto específico
    @Override
    public int obtenerStockDisponible(int productoId) {
        
        String sql = " SELECT (stock_Actual - stock_Reservado) AS disponible FROM inventarios WHERE producto_id = ? ";

        try (
            Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql); ) {
            
                ps.setInt(1, productoId);

                ResultSet rs = ps.executeQuery();

                if(rs.next()) {
                    return rs.getInt("disponible");
                }
                return 0;
        } catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    // Implementación del método hayStockSuficiente() para verificar
    // si hay suficiente stock disponible para un producto específico
    @Override
    public boolean hayStockSuficiente(int productoId, int cantidad) {
        
        return obtenerStockDisponible(productoId) >= cantidad;
    }

    // Implementación del método descontarStock() para descontar una cantidad específica de stock de un producto
    @Override
    public boolean descontarStock(int productoId, int cantidad) {
        
        String sql = " UPDATE inventarios SET stock_actual = stock_actual - ? WHERE producto_id = ? AND stock_actual >= ? ";

        try (
            Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, cantidad);
            ps.setInt(2, productoId);
            ps.setInt(3, cantidad);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}