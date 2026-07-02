
package com.didistore.dao.impl.catalog;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.catalog.IImagenesProductosDAO;
import com.didistore.model.catalog.ImagenesProductos;
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

// Implementación de la interfaz IImagenesProductosDAO para realizar operaciones CRUD
// en la tabla Imagenes_Productos de la base de datos.
public class ImagenesProductosDAOImpl implements IImagenesProductosDAO {

    // Implementación del método listarImagenes() para obtener todas las imágenes de productos
    @Override
    public List<ImagenesProductos> listarImagenes() {
        
        List<ImagenesProductos> lista = new ArrayList<>();
        String sql = "SELECT id_imagen, producto_id, url, formato FROM imagenes_productos";

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ImagenesProductos imagen = new ImagenesProductos();
                imagen.setIdImagen(rs.getInt("id_imagen"));
                imagen.setProductoId(rs.getInt("producto_id"));
                imagen.setUrl(rs.getString("url"));
                imagen.setFormato(rs.getString("formato"));
                lista.add(imagen);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar imágenes: " + e.getMessage());
        }
        return lista;
    }

    // Implementación del método listarPorProducto() para obtener todas las imágenes de un producto específico
    @Override
    public List<ImagenesProductos> listarPorProducto(int productoId) {
        
        List<ImagenesProductos> lista = new ArrayList<>();
        String sql = "SELECT id_imagen, producto_id, url, formato FROM Imagenes_Productos WHERE producto_id = ?";

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, productoId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ImagenesProductos imagen = new ImagenesProductos();
                    imagen.setIdImagen(rs.getInt("id_imagen"));
                    imagen.setProductoId(rs.getInt("producto_id"));
                    imagen.setUrl(rs.getString("url"));
                    imagen.setFormato(rs.getString("formato"));
                    lista.add(imagen);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar imágenes por producto: " + e.getMessage());
        }
        return lista;
    }

    // Implementación del método insertarImagen() para agregar una nueva imagen de producto a la base de datos
    @Override
    public boolean insertarImagen(ImagenesProductos imagen) {
        
        String sql = "INSERT INTO Imagenes_Productos (producto_id, url, formato) VALUES (?, ?, ?)";

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, imagen.getProductoId());
            ps.setString(2, imagen.getUrl());
            ps.setString(3, imagen.getFormato());

            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Imagen insertada correctamente en la BD!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar imagen: " + e.getMessage());
        }
        return false;
    }

    // Implementación del método eliminarImagen() para eliminar una imagen de producto de la base de datos
    @Override
    public boolean eliminarImagen(int idImagen) {
        
        String sql = "DELETE FROM Imagenes_Productos WHERE id_imagen = ?";

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idImagen);

            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Imagen eliminada correctamente en la BD!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar imagen: " + e.getMessage());
        }
        return false;
    }

    // Implementación del método consultarImagenPorId() para obtener una imagen de producto específica por su ID
    @Override
    public ImagenesProductos consultarImagenPorId(int idImagen) {
        
        String sql = "SELECT id_imagen, producto_id, url, formato FROM Imagenes_Productos WHERE id_imagen = ?";

        ImagenesProductos imagenProducto = null;

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idImagen);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    imagenProducto = new ImagenesProductos();

                    imagenProducto.setIdImagen(rs.getInt("id_imagen"));
                    imagenProducto.setProductoId(rs.getInt("producto_id"));
                    imagenProducto.setUrl(rs.getString("url"));
                    imagenProducto.setFormato(rs.getString("formato"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar imagenes por ID: " + e.getMessage());
        }
        return imagenProducto;
    }

    // Implementación del método actualizarImagen() para actualizar los datos de una
    // imagen de producto existente en la base de datos
    @Override
    public boolean actualizarImagen(ImagenesProductos imagen) {
        
        String sql = "UPDATE Imagenes_Productos SET producto_id = ?, url = ?, formato = ? WHERE id_imagen = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, imagen.getProductoId());
            ps.setString(2, imagen.getUrl());
            ps.setString(3, imagen.getFormato());
            ps.setInt(4, imagen.getIdImagen());

            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Imagen actualizada correctamente en la BD!");
            }
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error al actualizar imagen: " + e.getMessage());
        }
        return false;
    }
}