
package com.didistore.dao.impl.auth;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.auth.IUsuariosDAO;
import com.didistore.model.auth.Usuarios;
import com.didistore.model.auth.enums.EstadoUsuarios;
import com.didistore.model.auth.enums.TipoDocumentos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Andrés Áñvarez Lache
 */
public class UsuariosDAOImpl implements IUsuariosDAO  {
    
    public void insertarUsuarios(Usuarios usuario) {
        
        String sql = "INSERT INTO Usuarios (email, "
                + "contraseña,"
                + "nombre,"
                + "apellido,"
                + "documento,"
                + "tipo_documento,"
                + "perfil_id,"
                + "estado,"
                + "email_verificado,"
                + "fecha_creacion,"
                + "fecha_actualizacion,"
                + "fecha_ultimo_login) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setString(1, usuario.getemail());
                ps.setString(2, usuario.getcontrasena());
                ps.setString(3, usuario.getnombre());
                ps.setString(4, usuario.getapellido());
                ps.setString(5, usuario.getdocumento());
                ps.setString(6, usuario.gettipoDocumento() != null ? usuario.gettipoDocumento().name() : null);
                ps.setInt(7, usuario.getperfilId());
                ps.setString(8, usuario.getestado() != null ? usuario.getestado().name() : null);
                ps.setInt(9, usuario.getemailVerificado());
                ps.setTimestamp(10, usuario.getfechaCreacion());
                ps.setTimestamp(11, usuario.getfechaActualizacion());
                ps.setTimestamp(12, usuario.getfechaUltimoLogin());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Uusario insertado correctamente en la BD!");
                }                          
            } catch (SQLException e) {
            System.err.println("Error al listar usuario: " + e.getMessage());
        }               
    }

    @Override
    public List<Usuarios> listarUsuarios() {
        
         List<Usuarios> lista = new ArrayList<>();
            String sql = "SELECT (id_Usuario, "
                + "email, "
                + "contraseña, "
                + "nombre, "
                + "apellido, "
                + "documento, "
                + "tipo_Documento, "
                + "perfil_Id, "
                + "estado, "
                + "email_Verificado, "
                + "fecha_Creacion, "
                + "fecha_Actualizacion, "
                + "Fecha_Ultimo_Login) FROM usuarios";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Usuarios usuario = new Usuarios();
                usuario.setidUsuario(rs.getInt("id_Usuario"));
                usuario.setemail(rs.getString("email"));
                usuario.setcontrasena(rs.getString("contraseña"));
                usuario.setnombre(rs.getString("nombre"));
                usuario.setapellido(rs.getString("apellido"));
                usuario.setdocumento(rs.getString("documento"));
                String tipoDocumentoStr = rs.getString("tipo_Documento");
                usuario.settipoDocumento(TipoDocumentos.valueOf(tipoDocumentoStr));
                usuario.setperfilId(rs.getInt("perfil_Id"));
                String estadoStr = rs.getString("estado");
                usuario.setestado(EstadoUsuarios.valueOf(estadoStr));
                usuario.setemailVerificado(rs.getInt("email_Verificado"));
                usuario.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                usuario.setfechaActualizacion(rs.getTimestamp("fecha_Actualizacion"));
                usuario.setfechaUltimoLogin(rs.getTimestamp("fecha_Ultimo_Login"));
                lista.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
                    }
        return lista;
    }

    @Override
    public Usuarios consultarUsuariosPorId(int usuarioId) {
        
        String sql = "SELECT (id_Usuario, "
                + "email, "
                + "contraseña, "
                + "nombre, "
                + "apellido, "
                + "documento, "
                + "tipo_Documento, "
                + "perfil_Id, "
                + "estado, "
                + "email_Verificado, "
                + "fecha_Creacion, "
                + "fecha_Actualizacion, "
                + "Fecha_Ultimo_Login) FROM usuarios WHERE ID_Usuario = ?";
        
       Usuarios usuario = null;
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, usuario.getidUsuario());
            
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    usuario = new Usuarios();
                    usuario.setidUsuario(rs.getInt("id_Usuario"));
                    usuario.setemail(rs.getString("email"));
                    usuario.setcontrasena(rs.getString("contraseña"));
                    usuario.setnombre(rs.getString("nombre"));
                    usuario.setapellido(rs.getString("apellido"));
                    usuario.setdocumento(rs.getString("documento"));
                    String tipoDocumentoStr = rs.getString("tipo_Documento");
                    usuario.settipoDocumento(TipoDocumentos.valueOf(tipoDocumentoStr));
                    usuario.setperfilId(rs.getInt("perfil_Id"));
                    String estadoStr = rs.getString("estado");
                    usuario.setestado(EstadoUsuarios.valueOf(estadoStr));
                    usuario.setemailVerificado(rs.getInt("email_Verificado"));
                    usuario.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                    usuario.setfechaActualizacion(rs.getTimestamp("fecha_Actualizacion"));
                    usuario.setfechaUltimoLogin(rs.getTimestamp("fecha_Ultimo_Login"));
                }
            } 
        } catch (SQLException e) {
            System.err.println("Error al consultar usuario por ID: " + e.getMessage());
        }
        return usuario;
    }
    
    @Override
    public Usuarios consultarUsuariosPorEmail(String email) {
        
        String sql = "SELECT (id_Usuario, "
                + "email, "
                + "contraseña, "
                + "nombre, "
                + "apellido, "
                + "documento, "
                + "tipo_Documento, "
                + "perfil_Id, "
                + "estado, "
                + "email_Verificado, "
                + "fecha_Creacion, "
                + "fecha_Actualizacion, "
                + "Fecha_Ultimo_Login) FROM usuarios WHERE Email = ?";
        
       Usuarios usuario = null;
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, usuario.getemail());
            
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    usuario = new Usuarios();
                    usuario.setidUsuario(rs.getInt("id_Usuario"));
                    usuario.setemail(rs.getString("email"));
                    usuario.setcontrasena(rs.getString("contraseña"));
                    usuario.setnombre(rs.getString("nombre"));
                    usuario.setapellido(rs.getString("apellido"));
                    usuario.setdocumento(rs.getString("documento"));
                    String tipoDocumentoStr = rs.getString("tipo_Documento");
                    usuario.settipoDocumento(TipoDocumentos.valueOf(tipoDocumentoStr));
                    usuario.setperfilId(rs.getInt("perfil_Id"));
                    String estadoStr = rs.getString("estado");
                    usuario.setestado(EstadoUsuarios.valueOf(estadoStr));
                    usuario.setemailVerificado(rs.getInt("email_Verificado"));
                    usuario.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                    usuario.setfechaActualizacion(rs.getTimestamp("fecha_Actualizacion"));
                    usuario.setfechaUltimoLogin(rs.getTimestamp("fecha_Ultimo_Login"));
                }
            } 
        } catch (SQLException e) {
            System.err.println("Error al consultar usuario: " + e.getMessage());
        }
        return usuario;
    }

    @Override
    public void actualizarUsuarios(Usuarios usuario) {
        
        String sql = "UPDATE Usuarios (SET email = ?, "
                + "SET contraseña = ?,"
                + "SET nombre = ?,"
                + "SET apellido = ?,"
                + "SET documento = ?,"
                + "SET tipo_documento = ?,"
                + "SET perfil_id = ?,"
                + "SET estado = ?,"
                + "SET email_verificado = ?,"
                + "SET fecha_creacion = ?,"
                + "SET fecha_actualizacion = ?,"
                + "SET fecha_ultimo_login = ?) WHERE ID_Usuario = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setString(1, usuario.getemail());
                ps.setString(2, usuario.getcontrasena());
                ps.setString(3, usuario.getnombre());
                ps.setString(4, usuario.getapellido());
                ps.setString(5, usuario.getdocumento());
                ps.setString(6, usuario.gettipoDocumento() != null ? usuario.gettipoDocumento().name() : null);
                ps.setInt(7, usuario.getperfilId());
                ps.setString(8, usuario.getestado() != null ? usuario.getestado().name() : null);
                ps.setInt(9, usuario.getemailVerificado());
                ps.setTimestamp(10, usuario.getfechaCreacion());
                ps.setTimestamp(11, usuario.getfechaActualizacion());
                ps.setTimestamp(12, usuario.getfechaUltimoLogin());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Uusario actualizado correctamente en la BD!");
                }                          
            } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }               
    }

    @Override
    public void eliminarUsuarios(int idUsuario) {
        
        String sql = "DELETE FROM Usuarios WHERE ID_Usuario = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Uusario eliminado correctamente en la BD!");
                }                          
            } catch (SQLException e) {
            System.err.println("Error al eliminado usuario: " + e.getMessage());
        }                   
    }
}