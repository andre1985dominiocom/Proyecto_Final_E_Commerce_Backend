
package com.didistore.dao.impl.auth;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.auth.IPerfilesDAO;
import com.didistore.model.auth.Perfiles;
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

// Implementación de la interfaz IPerfilesDAO para realizar operaciones CRUD en la tabla Perfiles de la base de datos.
public class PerfilesDAOImpl implements IPerfilesDAO {
    
    // Método para insertar un nuevo perfil en la base de datos.
    @Override
    public void insertarPerfiles(Perfiles perfil) {
        
        String sql = "INSERT INTO Perfiles (Nombre_perfil,"
                + "Descripcion_perfil) VALUES (?, ?)";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setString(1, perfil.getnombrePerfil());
                ps.setString(2, perfil.getdescripcionPerfil());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Perfil insertado correctamente en la BD!");
                }
            } catch (SQLException e) {
                System.err.println("Error al listar perfil: " + e.getMessage());
        }
    }

    // Método para listar todos los perfiles de la base de datos.
    @Override
    public List<Perfiles> listarPerfiles() {
        
        List<Perfiles> lista = new ArrayList<>();
            String sql = "SELECT (Nombre_perfil,"
                + "Descripcion_perfil) FROM perfiles";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Perfiles perfil = new Perfiles();
                perfil.setnombrePerfil(rs.getString("Nombre_perfil"));
                perfil.setdescripcionPerfil(rs.getString("Descripcion_perfil"));
                lista.add(perfil);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar perfil: " + e.getMessage());
        }
        return lista;
    }

    // Método para consultar un perfil específico por su ID.
    @Override
    public Perfiles consultarPerfiles(int idPerfil) {
        
        String sql = "SELECT (Nombre_perfil,"
                + "Descripcion_perfil) FROM perfiles WHERE ID_Perfil = ?";
        
        Perfiles perfil = null;
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, perfil.getidPerfil());
            
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    perfil = new Perfiles();
                    perfil.setnombrePerfil(rs.getString("Nombre_perfil"));
                    perfil.setdescripcionPerfil(rs.getString("Descripcion_perfil"));
                }
            } 
        } catch (SQLException e) {
            System.err.println("Error al consultar perfiles por ID: " + e.getMessage());
        }
        return perfil;
    }

    // Método para actualizar un perfil existente en la base de datos.
    @Override
    public void actualizarPerfiles(Perfiles perfil) {
        
        String sql = "UPDATE Usuarios SET Nombre_perfil = ?,"
                + "Descripcion_perfil = ? WHERE ID_Perfil = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setString(1, perfil.getnombrePerfil());
                ps.setString(2, perfil.getdescripcionPerfil());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Perfil actualizado correctamente en la BD!");
                }
            } catch (SQLException e) {
            System.err.println("Error al actualizar perfil: " + e.getMessage());
        }
    }

    // Método para eliminar un perfil de la base de datos por su ID.
    @Override
    public void eliminarPerfiles(int idPerfil) {
        
        String sql = "DELETE FROM Perfiles WHERE ID_Perfil = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPerfil);

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Permiso eliminado correctamente en la BD!");
                }
            } catch (SQLException e) {
            System.err.println("Error al eliminar permiso: " + e.getMessage());
        }
    }
}