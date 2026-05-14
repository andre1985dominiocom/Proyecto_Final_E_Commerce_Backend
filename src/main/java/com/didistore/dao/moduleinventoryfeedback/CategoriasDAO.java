
package com.didistore.dao.moduleinventoryfeedback;

import com.didistore.config.Conexion;
import com.didistore.model.moduleinventoryfeedback.Categorias;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class CategoriasDAO {
    
    public int insertarCategorias(Categorias categoria) throws SQLException {
        
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
            throw e;
        }
        return idGenerado;
    }
}