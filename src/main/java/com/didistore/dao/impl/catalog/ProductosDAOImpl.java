
package com.didistore.dao.impl.catalog;

import com.didistore.dao.interfaces.catalog.IProductosDAO;
import com.didistore.config.Conexion;
import com.didistore.model.catalog.Productos;
import com.didistore.model.catalog.enums.EstadoProductos;
import com.didistore.model.catalog.enums.TallaProductos;
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
public class ProductosDAOImpl implements IProductosDAO {
    
    @Override
    public void insertarProductos(Productos producto) {
           
        String sql = "INSERT INTO Productos (nombre_Producto, "
                + "descripcion_Corta, "
                + "descripcion_Larga, "
                + "precio, "
                + "sku, "
                + "talla, "
                + "color, "
                + "categoria_Id, "
                + "estado, "
                + "es_Destacado, "
                + "fecha_Creacion, "
                + "fecha_Actualizacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setString(1, producto.getnombreProducto());
                ps.setString(2, producto.getdescripcionCorta());
                ps.setString(3, producto.getdescripcionLarga());
                ps.setFloat(4, producto.getprecio());
                ps.setString(5, producto.getsku());
                ps.setString(6, producto.gettalla() != null ? producto.gettalla().name() : null);
                ps.setString(7, producto.getcolor());
                ps.setInt(8, producto.getcategoriaId());
                ps.setString(9, producto.getestado() != null ? producto.getestado().name() : null);
                ps.setBoolean(10, producto.getesDestacado());
                ps.setTimestamp(11, producto.getfechaCreacion());
                ps.setTimestamp(12, producto.getfechaActualizacion());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Producto insertado correctamente en la BD!");
                }                          
            } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
    }

    @Override
    public void actualizarProductos(Productos producto) {
        
        String sql = "UPDATE productos SET nombre_producto = ?, descripcion_corta = ?, descripcion_larga = ?, precio = ?, SKU = ?, talla = ?, color = ?, categoria_id = ?, estado = ?, es_destacado = ?, fecha_creacion = ?, fecha_actualizacion = ? WHERE id_Producto = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, producto.getnombreProducto());
            ps.setString(2, producto.getdescripcionCorta());
            ps.setString(3, producto.getdescripcionLarga());
            ps.setFloat(4, producto.getprecio());
            ps.setString(5, producto.getsku());
            ps.setString(6, producto.gettalla() != null ? producto.gettalla().name() : null);
            ps.setString(7, producto.getcolor());
            ps.setInt(8, producto.getcategoriaId());
            ps.setString(9, producto.getestado() != null ? producto.getestado().name() : null);
            ps.setBoolean(10, producto.getesDestacado());
            ps.setTimestamp(11, producto.getfechaCreacion());
            ps.setTimestamp(12, producto.getfechaActualizacion());
            ps.setInt(13, producto.getidProducto());

            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Producto actualizado correctamente en la BD!");
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
        }
    }

    @Override
    public List<Productos> listarProductos() {
        
        List<Productos> lista = new ArrayList<>();

        String sql = "SELECT id_Producto, nombre_Producto, "
                + "descripcion_Corta, "
                + "descripcion_Larga, "
                + "precio, "
                + "SKU, "
                + "talla, "
                + "color, "
                + "categoria_Id, "
                + "estado, "
                + "es_Destacado, "
                + "fecha_Creacion, "
                + "fecha_Actualizacion FROM Productos";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Productos producto = new Productos();

                producto.setidProducto(rs.getInt("id_Producto"));
                producto.setnombreProducto(rs.getString("nombre_producto"));
                producto.setdescripcionCorta(rs.getString("descripcion_corta"));
                producto.setdescripcionLarga(rs.getString("descripcion_larga"));
                producto.setprecio(rs.getFloat("precio"));
                producto.setsku(rs.getString("SKU"));

                String tallaProductoStr = rs.getString("talla");
                if (tallaProductoStr != null && !tallaProductoStr.isEmpty()) {
                    producto.settalla(TallaProductos.valueOf(tallaProductoStr));
                }
                
                producto.setcolor(rs.getString("color"));
                producto.setcategoriaId(rs.getInt("categoria_id"));

                String estadoStr = rs.getString("estado");
                if (estadoStr != null && !estadoStr.isEmpty()) {
                   producto.setestado(EstadoProductos.valueOf(estadoStr));
                }

                producto.setesDestacado(rs.getBoolean("es_destacado"));
                producto.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                producto.setfechaActualizacion(rs.getTimestamp("fecha_actualizacion"));

                lista.add(producto);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
        
        return lista;
    }

    @Override
    public Productos consultarProductosPorId(int idProducto) {
        
        String sql = "SELECT  FROM productos id_Producto, nombre_Producto, "
                + "descripcion_Corta, "
                + "descripcion_Larga, "
                + "precio, "
                + "SKU, "
                + "talla, "
                + "color, "
                + "categoria_Id, "
                + "estado, "
                + "es_Destacado, "
                + "fecha_Creacion, "
                + "fecha_Actualizacion WHERE id_Producto = ?";

        Productos producto = null;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    producto = new Productos();

                    producto.setidProducto(rs.getInt("id_Producto"));
                    producto.setnombreProducto(rs.getString("nombre_producto"));
                    producto.setdescripcionCorta(rs.getString("descripcion_corta"));
                    producto.setdescripcionLarga(rs.getString("descripcion_larga"));
                    producto.setprecio(rs.getFloat("precio"));
                    producto.setsku(rs.getString("SKU"));

                    String tallaProductoStr = rs.getString("talla");
                    if (tallaProductoStr != null && !tallaProductoStr.isEmpty()) {
                        producto.settalla(TallaProductos.valueOf(tallaProductoStr));
                    }
                    
                    producto.setcolor(rs.getString("color"));
                    producto.setcategoriaId(rs.getInt("categoria_id"));

                    String estadoStr = rs.getString("estado");
                    if (estadoStr != null && !estadoStr.isEmpty()) {
                        producto.setestado(EstadoProductos.valueOf(estadoStr));
                    }

                    producto.setesDestacado(rs.getBoolean("es_destacado"));
                    producto.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                    producto.setfechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar producto por ID: " + e.getMessage());
        }
        return producto;
    }
    
    @Override
    public void eliminarProductos(int idProducto) {
        
        String sql = "DELETE FROM productos WHERE id_Producto = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idProducto);

            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Producto eliminado correctamente en la BD!");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
        }
    }

    @Override
    public Productos buscarProductoPorNombre(String nombreProducto) {
        
        String sql = "SELECT id_Producto, nombre_Producto, "
                + "descripcion_Corta, "
                + "descripcion_Larga, "
                + "precio, "
                + "SKU, "
                + "talla, "
                + "color, "
                + "categoria_Id, "
                + "estado, "
                + "es_Destacado, "
                + "fecha_Creacion, "
                + "fecha_Actualizacion FROM Productos WHERE nombre_Producto = ?";
        
        Productos producto = null;
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombreProducto);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    producto = new Productos();

                    producto.setidProducto(rs.getInt("id_Producto"));
                    producto.setnombreProducto(rs.getString("nombre_producto"));
                    producto.setdescripcionCorta(rs.getString("descripcion_corta"));
                    producto.setdescripcionLarga(rs.getString("descripcion_larga"));
                    producto.setprecio(rs.getFloat("precio"));
                    producto.setsku(rs.getString("SKU"));

                    String tallaProductoStr = rs.getString("talla");
                    if (tallaProductoStr != null && !tallaProductoStr.isEmpty()) {
                        producto.settalla(TallaProductos.valueOf(tallaProductoStr));
                    }
                    
                    producto.setcolor(rs.getString("color"));
                    producto.setcategoriaId(rs.getInt("categoria_id"));

                    String estadoStr = rs.getString("estado");
                    if (estadoStr != null && !estadoStr.isEmpty()) {
                        producto.setestado(EstadoProductos.valueOf(estadoStr));
                    }

                    producto.setesDestacado(rs.getBoolean("es_destacado"));
                    producto.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                    producto.setfechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar producto por nombre: " + e.getMessage());
        }
        return producto;
    }

    @Override
    public List<Productos> listarProductoPorCategoria(int idCategoria) {
        List<Productos> lista = new ArrayList<>();

    String sql = "SELECT id_Producto, nombre_Producto, "
            + "descripcion_Corta, "
            + "descripcion_Larga, "
            + "precio, "
            + "SKU, "
            + "talla, "
            + "color, "
            + "categoria_Id, "
            + "estado, "
            + "es_Destacado, "
            + "fecha_Creacion, "
            + "fecha_Actualizacion "
            + "FROM Productos WHERE categoria_Id = ?";

    try (Connection con = Conexion.getConexion();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idCategoria);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Productos producto = new Productos();

                producto.setidProducto(rs.getInt("id_Producto"));
                producto.setnombreProducto(rs.getString("nombre_Producto"));
                producto.setdescripcionCorta(rs.getString("descripcion_Corta"));
                producto.setdescripcionLarga(rs.getString("descripcion_Larga"));
                producto.setprecio(rs.getFloat("precio"));
                producto.setsku(rs.getString("SKU"));

                String tallaProductoStr = rs.getString("talla");
                if (tallaProductoStr != null && !tallaProductoStr.isEmpty()) {
                    producto.settalla(TallaProductos.valueOf(tallaProductoStr));
                }

                producto.setcolor(rs.getString("color"));
                producto.setcategoriaId(rs.getInt("categoria_Id"));

                String estadoStr = rs.getString("estado");
                if (estadoStr != null && !estadoStr.isEmpty()) {
                    producto.setestado(EstadoProductos.valueOf(estadoStr));
                }

                producto.setesDestacado(rs.getBoolean("es_Destacado"));
                producto.setfechaCreacion(rs.getTimestamp("fecha_Creacion"));
                producto.setfechaActualizacion(rs.getTimestamp("fecha_Actualizacion"));

                lista.add(producto);
            }
        }
    } catch (SQLException e) {
        System.err.println("Error al listar productos por categoría: " + e.getMessage());
        }
        return lista;       
    }
}