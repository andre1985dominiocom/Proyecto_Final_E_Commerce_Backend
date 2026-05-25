
package com.didistore.dao.impl.auth;

import com.didistore.config.Conexion;
import com.didistore.model.auth.PerfilPermisos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.didistore.dao.interfaces.auth.IPerfilPermisosDAO;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PerfilPermisosDAOImpl implements IPerfilPermisosDAO {
    
    public boolean insertarPerfilPermisos(PerfilPermisos perfilPermiso) {
        
    String sql = "INSERT INTO Perfil_permiso (ID_perfil,"
                + "ID_permiso) VALUES (?, ?)";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setInt(1, perfilPermiso.getidPerfil());
                ps.setInt(2, perfilPermiso.getidPermiso());
               
                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Perfil permisos insertado correctamente en la BD!");
                }                          
            } catch (SQLException e) {
                System.err.println("Error al listar perfil permiso: " + e.getMessage());
        }   
        return false;
    }    

    @Override
    public List<PerfilPermisos> listarPermisosPorPerfil(int idPerfil) {
        
        List<PerfilPermisos> lista = new ArrayList<>();
            String sql = "SELECT ID_Perfil,"
                + "ID_Permiso FROM Perfil_Permiso WHERE ID_Perfil = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPerfil);
                
            try (ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                PerfilPermisos permiso = new PerfilPermisos();
                permiso.setidPerfil(rs.getInt("ID_Perfil"));
                permiso.setidPermiso(rs.getInt("ID_Permiso"));
                lista.add(permiso);
            }
        }           
    } catch (SQLException e) {
        System.err.println("Error al listar permisos por perfil: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<PerfilPermisos> listarPerfilesPorPermiso(int idPermiso) {
        
        List<PerfilPermisos> lista = new ArrayList<>();
            String sql = "SELECT ID_Perfil,"
                + "ID_Permiso FROM Perfil_Permiso WHERE ID_Permiso = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPermiso);
            
            try (ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                PerfilPermisos perfil = new PerfilPermisos();   
                perfil.setidPerfil(rs.getInt("ID_Perfil"));
                perfil.setidPermiso(rs.getInt("ID_Permiso"));
                lista.add(perfil);
            }
        }    
    } catch (SQLException e) {
            System.err.println("Error al listar perfiles por permiso: " + e.getMessage());
        }
       return lista;
    }

    @Override
    public void eliminarPerfilPermisos(int idPerfil, int idPermiso) {
        
        String sql = "DELETE FROM Perfil_permiso WHERE ID_Perfil = ? AND ID_Permiso = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPerfil);
            ps.setInt(2, idPermiso);

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Perfil permiso eliminado correctamente en la BD!");
                }                          
            } catch (SQLException e) {
            System.err.println("Error al eliminar perfil permiso: " + e.getMessage());
        }                
    }

    @Override
    public PerfilPermisos consultarPerfilPermisosPorId(int idPerfil, int idPermiso) {
        
        String sql = "SELECT ID_Perfil,"
                + "ID_Permiso FROM Perfil_permiso WHERE ID_Perfil = ? AND ID_Permiso = ?";
        
        PerfilPermisos perfilPermiso = null;
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPerfil);
            ps.setInt(2, idPermiso);
            
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    perfilPermiso = new PerfilPermisos();
                    perfilPermiso.setidPerfil(rs.getInt("ID_Perfil"));
                    perfilPermiso.setidPermiso(rs.getInt("ID_Permiso"));
                }
            } 
        } catch (SQLException e) {
            System.err.println("Error al consultar perfil permiso por ID: " + e.getMessage());
        }
        return perfilPermiso;
    }
        

    @Override
    public void actualizarPerfilPermisos(PerfilPermisos perfilPermiso) {
       
        String sql = "UPDATE Perfil_permiso SET ID_Permiso = ?, WHERE"
                + "ID_Perfil = ? AND ID_Permiso = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
                     
                ps.setInt(2, perfilPermiso.getidPermiso());
                ps.setInt(1, perfilPermiso.getidPerfil());
                ps.setInt(3, perfilPermiso.getidPermiso());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Perfil permiso actualizado correctamente en la BD!");
                }                          
            } catch (SQLException e) {
            System.err.println("Error al actualizar perfil permiso: " + e.getMessage());
        }               
    }      
}