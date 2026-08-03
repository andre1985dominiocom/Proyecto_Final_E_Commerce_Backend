
package com.didistore.dao.impl.auth;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.auth.IPermisosDAO;
import com.didistore.model.auth.Permisos;
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

// Clase que implementa la interfaz IPermisosDAO para realizar operaciones CRUD en la tabla Permisos de la base de datos.
public class PermisosDAOImpl implements IPermisosDAO {

    // Implementación del método para insertar un nuevo permiso en la base de datos.
    @Override
    public void insertarPermisos(Permisos permiso) {
        
        String sql = "INSERT INTO Permisos (Nombre_permiso,"
                + "Descripcion_permiso) VALUES (?, ?)";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setString(1, permiso.getnombrePermiso());
                ps.setString(2, permiso.getdescripcionPermiso());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Permiso insertado correctamente en la BD!");
                }
            } catch (SQLException e) {
                System.err.println("Error al insertar permiso: " + e.getMessage());
        }
    }

    // Implementación del método para listar todos los permisos de la base de datos.
    @Override
    public List<Permisos> listarPermisos() {
        
        List<Permisos> lista = new ArrayList<>();
            String sql = "SELECT (Nombre_permiso,"
                + "Descripcion_permiso) FROM permisos";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Permisos permiso = new Permisos();
                permiso.setnombrePermiso(rs.getString("Nombre_permiso"));
                permiso.setdescripcionPermiso(rs.getString("Descripcion_permiso"));
                lista.add(permiso);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar permisos: " + e.getMessage());
        }
        return lista;
    }

    // Implementación del método para consultar un permiso específico por su ID.
    @Override
    public Permisos consultarPermisos(int idPermiso) {
        
        String sql = "SELECT (Nombre_permiso,"
                + "Descripcion_permiso) FROM permisos WHERE ID_Permiso = ?";
        
        Permisos permiso = null;
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, permiso.getidPermiso());
            
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    permiso = new Permisos();
                    permiso.setnombrePermiso(rs.getString("Nombre_permiso"));
                    permiso.setdescripcionPermiso(rs.getString("Descripcion_permiso"));
                }
            } 
        } catch (SQLException e) {
            System.err.println("Error al consultar permisos por ID: " + e.getMessage());
        }
        return permiso;
    }

    // Implementación del método para actualizar un permiso existente en la base de datos.
    @Override
    public void actualizarPermisos(Permisos permiso) {
        
        String sql = "UPDATE Permisos SET Nombre_permiso = ?,"
                + "Descripcion_permiso = ? WHERE ID_Permiso = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setString(1, permiso.getnombrePermiso());
                ps.setString(2, permiso.getdescripcionPermiso());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Permiso actualizado correctamente en la BD!");
                }
            } catch (SQLException e) {
            System.err.println("Error al actualizar permiso: " + e.getMessage());
        }
    }

    // Implementación del método para eliminar un permiso de la base de datos por su ID.
    @Override
    public void eliminarPermisos(int idPermiso) {
        
        String sql = "DELETE FROM Permisos WHERE ID_Permiso = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idPermiso);

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