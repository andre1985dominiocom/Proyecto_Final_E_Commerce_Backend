
package com.didistore.dao;

import com.didistore.config.Conexion;
import com.didistore.model.Categorias;
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
public class CategoriasDAO {
    
    public void insertarCategorias(Categorias categoria) throws SQLException {
        
        String sql = "INSERT INTO Categorias (nombre_Categoria, "
                + "descripcion, "
                + "categoria_padre_Id, "
                + "fecha_Creacion) VALUES (?, ?, ?, ?)";
        
        try (Connection conexion = Conexion.getConexion();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            
            ps.setString(1, categoria.nombre_Categoria());
            ps.setString(2, categoria.getdescripcion());
            
            if (categoria.getcategoria_padre_Id() != null) {
                ps.setInt(3, categoria.getcategoria_padre_Id());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            
            int filasAfectadas = ps.executeUpdate();
            
            if (filasAfectadas > 0) {
                System.out.println("Categoría insertada exitosamente. ");
                
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        categoria.setid_Categoria(idGenerado);
                    }
                } catch (SQLException e) {
                    System.err.println("Error en el ID generado. " + e);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar categorías. " + e);
        }
    }
    
    public List<Categorias> listar() {
        List<Categorias> lista = new ArrayList<>();
        String sql = "SELECT id_Categoria, nombre_Categoria, descripcion, categoria_padre_Id, fecha_Creacion FROM categorias";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
                
            while (rs.next()) {
                Categorias c = new Categorias();
                c.setid_Categoria(rs.getInt("id_Categoria"));
                c.setnombre_Categoria(rs.getString("nombre_Categoria"), null);
                c.setdescripcion(rs.getString("descripcion"));
                c.setcategoria_padre_Id(rs.getInt("categoria_padre_Id"));
                c.setfecha_Creacion(rs.getTimestamp("fecha_Creacion"));
                
                lista.add(c);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar categorias: " + e.getMessage());
                    }
            
        return lista;
    }
    
}
