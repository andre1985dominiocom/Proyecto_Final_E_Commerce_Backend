
package com.didistore.dao.impl.logistics;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.logistics.IDireccionesDAO;
import com.didistore.model.logistics.Direcciones;
import com.didistore.model.logistics.enums.EstadoDirecciones;
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
public class DireccionesDAOImpl implements IDireccionesDAO {

    @Override
    public List<Direcciones> listarPorUsuario(int usuarioId) {
        List<Direcciones> lista = new ArrayList<>();
        
        String sql = "SELECT * FROM Direcciones WHERE Usuario_ID = ?";
        
         try (Connection con = Conexion.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
             
             ps.setInt(1, usuarioId);
             
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Direcciones direccion = new Direcciones();
                    direccion.setidDireccion(rs.getInt("ID_direccion"));
                    direccion.setdireccion(rs.getString("Direccion"));
                    direccion.setesPrincipal(rs.getBoolean("Es_principal"));
                    direccion.setbarrio(rs.getString("Barrio"));
                    direccion.setreferencia(rs.getString("Referencia"));
                    direccion.setciudadId(rs.getInt("Ciudad_ID"));
                    direccion.setusuarioId(rs.getInt("Usuario_ID"));
                    String estadoStr = rs.getString("Estado");
                    if (estadoStr != null && !estadoStr.isEmpty()) {
                        direccion.setestado(EstadoDirecciones.valueOf(estadoStr));
                    }
                    direccion.setfechaCreacion(rs.getTimestamp("Fecha_creacion"));
                    
                    lista.add(direccion);
                }
            }        
        } catch (SQLException e) {
                e.printStackTrace();
        }                
        return lista;
    }

    @Override
    public Direcciones buscarPorId(int idDireccion) {
        
        String sql = "SELECT * FROM Direcciones WHERE ID_Direccion = ?";
        
        try (Connection con = Conexion.getConexion();
                PreparedStatement ps = con.prepareStatement(sql)) {
             
             ps.setInt(1, idDireccion);
             
            try (ResultSet rs = ps.executeQuery()) {
                while(rs.next()) {
                    Direcciones direccion = new Direcciones();
                    direccion.setidDireccion(rs.getInt("ID_direccion"));
                    direccion.setdireccion(rs.getString("Direccion"));
                    direccion.setesPrincipal(rs.getBoolean("Es_principal"));
                    direccion.setbarrio(rs.getString("Barrio"));
                    direccion.setreferencia(rs.getString("Referencia"));
                    direccion.setciudadId(rs.getInt("Ciudad_ID"));
                    direccion.setusuarioId(rs.getInt("Usuario_ID"));
                    String estadoStr = rs.getString("Estado");
                    if (estadoStr != null && !estadoStr.isEmpty()) {
                        direccion.setestado(EstadoDirecciones.valueOf(estadoStr));
                    }
                    direccion.setfechaCreacion(rs.getTimestamp("Fecha_creacion"));
                    return direccion;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean crearDireccion(Direcciones direccion) {
        
        String sql = "INSERT INTO Direcciones (Direccion, Barrio, Referencia, Ciudad_ID, Usuario_ID, Estado, Fecha_creacion, Es_principal) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setString(1, direccion.getdireccion());
                ps.setString(2, direccion.getbarrio());
                ps.setString(3, direccion.getreferencia());
                ps.setInt(4, direccion.getciudadId());
                ps.setInt(5, direccion.getusuarioId());
                ps.setString(6, direccion.getestado() != null ? direccion.getestado().name() : null);
                ps.setTimestamp(7, direccion.getfechaCreacion());
                ps.setBoolean(8, direccion.getesPrincipal());
                
                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Dirección insertada correctamente en la BD!");
                }                          
            } catch (SQLException e) {
            System.err.println("Error al insertar dirección: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean actualizarDireccion(Direcciones direccion) {
        
        String sql = "UPDATE Direcciones SET Direccion = ?, Barrio = ?, Referencia = ?, Ciudad_ID = ?, Usuario_ID = ?, Estado = ?, Fecha_creacion = ?, Es_principal = ? WHERE ID_Direccion = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, direccion.getdireccion());
            ps.setString(2, direccion.getbarrio());
            ps.setString(3, direccion.getreferencia());
            ps.setInt(4, direccion.getciudadId());
            ps.setInt(5, direccion.getusuarioId());
            ps.setString(6, direccion.getestado()!= null ? direccion.getestado().name() : null);
            ps.setTimestamp(7, direccion.getfechaCreacion());
            ps.setBoolean(8, direccion.getesPrincipal());
            ps.setInt(9, direccion.getidDireccion());
            
            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("¡Dirección actualizada correctamente en la BD!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar dirección: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean eliminarDireccion(int idDireccion) {
        
        String sql = "DELETE FROM Direcciones WHERE ID_Direccion = ?";
        
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idDireccion);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("¡Dirección eliminada correctamente en la BD!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar direccion: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean marcarPrincipal(int usuarioId, int idDireccion) {
        
        String sqlDesactivar = "UPDATE Direcciones SET Es_principal = 0 WHERE Usuario_ID = ?";
        String sqlActivar = "UPDATE Direcciones SET Es_principal = 1 WHERE ID_Direccion = ?";
    
        try (Connection con = Conexion.getConexion()) {
            // 1. Iniciamos la transacción
            con.setAutoCommit(false);
        
            try (PreparedStatement psDesactivar = con.prepareStatement(sqlDesactivar);
                PreparedStatement psActivar = con.prepareStatement(sqlActivar)) {
            
                // 2. Desactivar todas las direcciones del usuario
                psDesactivar.setInt(1, usuarioId);
                psDesactivar.executeUpdate();
            
                // 3. Activar la dirección seleccionada
                psActivar.setInt(1, idDireccion);
                
                int filasAfectadas = psActivar.executeUpdate();
            
                // 4. Confirmar cambios
                if (filasAfectadas > 0) {
                    con.commit();
                    return true;
                } else {
                    con.rollback();
                    return false;
                }
            } catch (SQLException e) {
                con.rollback(); // Revertir si algo falla
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("Error en la transacción de dirección principal: " + e.getMessage());
            return false;      
        }  
    }

    @Override
    public boolean borradoDireccionLogico(int idDireccion, EstadoDirecciones nuevoEstado) {
         
        String sql = "UPDATE Direcciones SET Estado = ? WHERE ID_Direccion = ?";
    
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, idDireccion);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                System.out.println("¡Dirección desactivada (borrado lógico) correctamente!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error al realizar borrado lógico de la dirección: " + e.getMessage());
        }
        return false;
    }
}