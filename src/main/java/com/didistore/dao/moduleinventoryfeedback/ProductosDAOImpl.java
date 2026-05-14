
package com.didistore.dao.moduleinventoryfeedback;

import com.didistore.config.Conexion;
import com.didistore.model.moduleinventoryfeedback.Productos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class ProductosDAOImpl implements IProductosDAO {
    
    @Override
    public void insertarProducto(Productos producto) {
        
        System.out.println("-> [DAO] ¡El método correcto ha sido invocado con éxito!");
        System.out.println("-> [DAO] Guardando producto: " + producto.getnombreProducto());
        
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
                ps.setInt(10, producto.getesDestacado());
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
    }

    @Override
    public void eliminarProductos(int idProducto) {
    }

    @Override
    public List<Productos> listar() {
        return null;
    }
}