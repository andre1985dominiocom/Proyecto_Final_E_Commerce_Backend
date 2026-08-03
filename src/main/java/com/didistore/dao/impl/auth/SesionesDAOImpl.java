
package com.didistore.dao.impl.auth;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.auth.ISesionesDAO;
import com.didistore.model.auth.Sesiones;
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

// Implementación de la interfaz ISesionesDAO para realizar operaciones CRUD en la tabla Sesiones de la base de datos.
public class SesionesDAOImpl implements ISesionesDAO{

    // Implementación del método para insertar una nueva sesión en la base de datos.
    @Override
    public void insertarSesiones(Sesiones sesion) {
        
        String sql = "INSERT INTO Sesiones (Usuario_ID,"
                + "Token_sesion,"
                + "Fecha_Creacion,"
                + "Fecha_expiracion,"
                + "IP,"
                + "User_agent,"
                + "Revocada) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setInt(1, sesion.getusuarioId());
                ps.setString(2, sesion.gettokenSesion());
                ps.setTimestamp(3, sesion.getfechaCreacion());
                ps.setTimestamp(4, sesion.getfechaExpiracion());
                ps.setString(5, sesion.getip());
                ps.setString(6, sesion.getuserAgent());
                ps.setInt(7, sesion.getrevocada());
               
                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Inicio sesión insertado correctamente en la BD!");
                }
            } catch (SQLException e) {
                System.err.println("Error al insertar inicio de sesión: " + e.getMessage());
        }
    }

    // Implementación del método para listar todas las sesiones de la base de datos.
    @Override
    public List<Sesiones> listarSesiones() {
        
        List<Sesiones> lista = new ArrayList<>();
            String sql = "SELECT Usuario_ID,"
                + "Token_sesion,"
                + "Fecha_Creacion,"
                + "Fecha_expiracion,"
                + "IP,"
                + "User_agent,"
                + "Revocada FROM sesiones";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Sesiones sesion = new Sesiones();
                sesion.setusuarioId(rs.getInt("Usuario_ID"));
                sesion.settokenSesion(rs.getString("Token_sesion"));
                sesion.setfechaCreacion(rs.getTimestamp("Fecha_creacion"));
                sesion.setfechaExpiracion(rs.getTimestamp("Fecha_expiracion"));
                sesion.setip(rs.getString("IP"));
                sesion.setuserAgent(rs.getString("User_agent"));
                sesion.setrevocada(rs.getInt("Revocada"));
                lista.add(sesion);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar sesiones: " + e.getMessage());
                    }
        return lista;
    }

    // Implementación del método para consultar una sesión específica por su ID en la base de datos.
    @Override
    public Sesiones consultarSesiones(int idSesion) {
        
        String sql = "SELECT Usuario_ID,"
                + "Token_sesion,"
                + "Fecha_Creacion,"
                + "Fecha_expiracion,"
                + "IP,"
                + "User_agent,"
                + "Revocada FROM sesiones WHERE ID_Sesion = ?";
        
        Sesiones sesion = null;
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idSesion);
            
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    sesion = new Sesiones();
                    sesion.setusuarioId(rs.getInt("Usuario_ID"));
                    sesion.settokenSesion(rs.getString("Token_sesion"));
                    sesion.setfechaCreacion(rs.getTimestamp("Fecha_creacion"));
                    sesion.setfechaExpiracion(rs.getTimestamp("Fecha_expiracion"));
                    sesion.setip(rs.getString("IP"));
                    sesion.setuserAgent(rs.getString("User_agent"));
                    sesion.setrevocada(rs.getInt("Revocada"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar sesiones por ID: " + e.getMessage());
        }
        return sesion;
    }

    // Implementación del método para actualizar una sesión existente en la base de datos.
    @Override
    public void actualizarSesiones(Sesiones sesion) {
            
            String sql = "UPDATE sesiones SET Usuario_ID = ?,"
                + "Token_sesion = ?,"
                + "Fecha_Creacion = ?,"
                + "Fecha_expiracion = ?,"
                + "IP = ?,"
                + "User_agent = ?,"
                + "Revocada = ? WHERE ID_Sesion = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setInt(1, sesion.getusuarioId());
                ps.setString(2, sesion.gettokenSesion());
                ps.setTimestamp(3, sesion.getfechaCreacion());
                ps.setTimestamp(4, sesion.getfechaExpiracion());
                ps.setString(5, sesion.getip());
                ps.setString(6, sesion.getuserAgent());
                ps.setInt(7, sesion.getrevocada());
                ps.setInt(8, sesion.getidSesion());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Sesión actualizada correctamente en la BD!");
                }
            } catch (SQLException e) {
            System.err.println("Error al actualizar sesión: " + e.getMessage());
        }
    }

    // Implementación del método para eliminar una sesión específica por su ID en la base de datos.
    @Override
    public void eliminarSesiones(int idSesion) {
        
        String sql = "DELETE FROM Sesiones WHERE ID_Sesion = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idSesion);

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Sesión eliminada correctamente en la BD!");
                }
            } catch (SQLException e) {
            System.err.println("Error al eliminar sesión: " + e.getMessage());
        }
    }

    // Implementación del método para obtener la contraseña hasheada de un usuario por su email en la base de datos.
    @Override
    public String obtenerContrasenaHasheadaPorEmail(String email) {
        
        String sql = "SELECT contrasena FROM usuarios  WHERE email = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("contrasena");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener contraseña por email: " + e.getMessage());
        }
        return null;
    }
}