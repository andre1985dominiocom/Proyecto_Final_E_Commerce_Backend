
package com.didistore.dao.impl.catalog;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.catalog.ICategoriasDAO;
import com.didistore.model.catalog.Categorias;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Implementación de la interfaz ICategoriasDAO para realizar operaciones CRUD en la tabla Categorias de la base de datos.
public class CategoriasDAOImpl implements ICategoriasDAO {

    // Implementación del método para insertar una nueva categoría en la base de datos.
    @Override
    public boolean insertarCategorias(Categorias categoria) {
        String sql = "INSERT INTO Categorias (nombre_Categoria, "
                + "Descripcion, "
                + "Categoria_padre_ID, "
                + "Fecha_creacion) VALUES (?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
                ps.setString(1, categoria.getnombreCategoria());
                ps.setString(2, categoria.getdescripcion());
                
                if (categoria.getcategoriaPadreId() == null) {
                    ps.setNull(3, Types.INTEGER);
                } else {
                    ps.setInt(3, categoria.getcategoriaPadreId());
                }
                ps.setTimestamp(4, categoria.getfechaCreacion());
                
                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    
                    try (ResultSet rsKeys = ps.getGeneratedKeys()) {
                        if (rsKeys.next()) {
                            categoria.setidCategoria(rsKeys.getInt(1));
                        }
                    }
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
        }
        return false;
    }

    // Implementación del método para actualizar una categoría existente en la base de datos.
    @Override
    public boolean actualizarCategorias(Categorias categoria) {
        
        String sql = "UPDATE Categorias SET nombre_Categoria = ?, "
                + "Descripcion = ?, "
                + "Categoria_padre_ID = ?, "
                + "Fecha_creacion = ? WHERE ID_Categoria = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, categoria.getnombreCategoria());
            ps.setString(2, categoria.getdescripcion());

                if (categoria.getcategoriaPadreId() == null) {
                    ps.setNull(3, Types.INTEGER);
                } else {
                    ps.setInt(3, categoria.getcategoriaPadreId());
                }
                ps.setTimestamp(4, categoria.getfechaCreacion());
                ps.setInt(5, categoria.getidCategoria());
                
                return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Implementación del método para eliminar una categoría de la base de datos.
    @Override
    public boolean eliminarCategorias(int idCategoria) {
        
        String sql = "DELETE FROM Categorias WHERE ID_Categoria = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idCategoria);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Implementación del método para obtener una categoría por su ID.
    @Override
    public Categorias obtenerCategoriaPorId(int idCategoria) {
        
        String sql = "SELECT * FROM Categorias WHERE ID_Categoria = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idCategoria);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCategoria(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Categorias> obtenerTodasLasCategorias() {
        
        List<Categorias> lista = new ArrayList<>();
        
       String sql = "SELECT * FROM Categorias ORDER BY ID_Categoria ASC";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(mapearCategoria(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Implementación del método para obtener todas las categorías raíz (categorías sin padre).
    @Override
    public List<Categorias> obtenerCategoriasRaiz() {
        
        List <Categorias> lista = new ArrayList<>();
        
        String sql = "SELECT * FROM Categorias WHERE Categoria_padre_ID IS NULL ORDER BY Nombre_categoria ASC";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                lista.add(mapearCategoria(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Implementación del método para obtener todas las subcategorías de una categoría padre específica.
    @Override
    public List<Categorias> obtenerSubcategorias(int categoriaPadreId) {
        
        List <Categorias> lista = new ArrayList<>();
        
        String sql = "SELECT * FROM Categorias WHERE Categoria_padre_ID = ? ORDER BY Nombre_categoria ASC";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, categoriaPadreId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCategoria(rs));
                }
            } 
        } catch (SQLException e) {
                e.printStackTrace();
        }
        return lista;
    }

    // Método privado para mapear un ResultSet a un objeto Categorias.
    private Categorias mapearCategoria(ResultSet rs) throws SQLException {
        
        Categorias cat = new Categorias();
        
        cat.setidCategoria(rs.getInt("ID_Categoria"));
        cat.setnombreCategoria(rs.getString("Nombre_categoria"));
        cat.setdescripcion(rs.getString("Descripcion"));
        cat.setfechaCreacion(rs.getTimestamp("Fecha_creacion"));
        
        int categoriaPadreId = rs.getInt("Categoria_padre_ID");
        if (rs.wasNull()) {
            cat.setcategoriaPadreId(null);
        } else {
            cat.setcategoriaPadreId(categoriaPadreId);
        }
        return cat;
    }

    // Implementación del método para buscar una categoría por su nombre.
    @Override
    public Categorias buscarCategoriaPorNombre(String nombreCategoria) {

        String sql = "SELECT ID_Categoria, nombre_Categoria, "
                + "Descripcion, "
                + "Categoria_padre_ID, "
                + "Fecha_creacion FROM Categorias WHERE nombre_Categoria = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombreCategoria);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCategoria(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}