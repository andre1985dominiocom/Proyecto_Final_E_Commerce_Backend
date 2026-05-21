
package com.didistore.dao.impl.catalog;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.catalog.ICategoriasDAO;
import com.didistore.model.catalog.Categorias;
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
public class CategoriasDAOImpl implements ICategoriasDAO {

    @Override
    public void insertarCategorias(Categorias categoria) {
        
        String sql = "INSERT INTO Categorias (nombre_Categoria, "
                + "descripcion, "
                + "Categoria_padre_ID, "
                + "fecha_Creacion) VALUES (?, ?, ?, ?)";
        
        int idGenerado = 0;
        
        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, categoria.getnombreCategoria());
            ps.setString(2, categoria.getdescripcion());
            
            if (categoria.getcategoriaPadreId() != null && categoria.getcategoriaPadreId() > 0) {
                ps.setObject(3, categoria.getcategoriaPadreId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
               
            ps.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGenerado = rs.getInt(1);
                        
                        categoria.setidCategoria(idGenerado);
                    }
                } 
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar categorías. " + e.getMessage());
        }  
    }

    @Override
    public List<Categorias> listar() {
        
        List<Categorias> lista = new ArrayList<>();

        String sql = "SELECT id_Categoria, nombre_Categoria, "
                + "descripcion, "
                + "Categoria_padre_ID, "
                + "fecha_Creacion FROM Categorias";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Categorias categoria = new Categorias();

                categoria.setidCategoria(rs.getInt("id_categoria"));
                categoria.setdescripcion(rs.getString("descripcion"));
                categoria.setcategoriaPadreId(rs.getInt("categoria_padre_id"));
                categoria.setfechaCreacion(rs.getTimestamp("fecha_creacion"));

                lista.add(categoria);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar categorias: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public Categorias consultarCategoriasPorId(int idCategoria) {
        
        String sql = "SELECT  FROM categorias nombre_Categoria, "
                + "descripcion, "
                + "Categoria_padre_ID, "
                + "fecha_Creacion  WHERE id_Categoria = ?";

        Categorias categoria = null;

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    categoria = new Categorias();

                    categoria.setidCategoria(rs.getInt("id_Categoria"));
                    categoria.setdescripcion(rs.getString("descripcion"));
                    categoria.setcategoriaPadreId(rs.getInt("categoria_padre_id"));
                    categoria.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar producto por ID: " + e.getMessage());
        }
        return categoria;
    }

    @Override
    public void actualizarCategoria(Categorias categoria) {
        
        String sql = "UPDATE categorias SET descripcion = ?, categoria_padre_id = ?, fecha_Creacion = ? WHERE id_categoria = ? ";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, categoria.getdescripcion());
            ps.setInt(2, categoria.getcategoriaPadreId());
            ps.setTimestamp(3, categoria.getfechaCreacion());

            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Categoria actualizada correctamente en la BD!");
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar categoria: " + e.getMessage());
        }
        
    }

    @Override
    public void eliminarCategorias(int idCategoria) {
        
        String sql = "DELETE FROM categorias WHERE id_categoria = ?";

        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);

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
        
} 