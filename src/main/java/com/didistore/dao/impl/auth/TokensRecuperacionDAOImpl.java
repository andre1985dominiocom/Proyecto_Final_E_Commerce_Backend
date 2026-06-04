
package com.didistore.dao.impl.auth;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.auth.ITokensRecuperacionDAO;
import com.didistore.model.auth.TokensRecuperacion;
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

// Implementación de la interfaz ITokensRecuperacionDAO para gestionar los tokens de recuperación de contraseña en la base de datos.
public class TokensRecuperacionDAOImpl implements ITokensRecuperacionDAO {
    
    // Método para insertar un nuevo token de recuperación en la base de datos.
    @Override
    public void insertarToken(TokensRecuperacion token) {
        
        String sql = "INSERT INTO tokens_recuperacion (usuario_ID, "
                + "token_hash,"
                + "fecha_creacion,"
                + "fecha_expiracion,"
                + "usado,"
                + "intentos) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setInt(1, token.getusuarioId());
                ps.setString(2, token.gettokenHash());
                ps.setTimestamp(3, token.getfechaCreacion());
                ps.setTimestamp(4, token.getfechaExpiracion());
                ps.setBoolean(5, token.getusado());
                ps.setInt(6, token.getintentos());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Token insertado correctamente en la BD!");
                }
            } catch (SQLException e) {
            System.err.println("Error al listar token: " + e.getMessage());
        }
    }

    // Método para consultar un token de recuperación por su hash.
    @Override
    public TokensRecuperacion consultarTokenPorHash(String tokenHash) {
        
        String sql = "SELECT usuario_ID, "
                + "token_hash,"
                + "fecha_creacion,"
                + "fecha_expiracion,"
                + "usado,"
                + "intentos FROM tokens_recuperacion WHERE token_Hash = ?";
        
        TokensRecuperacion token = null;
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, tokenHash);
            
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    token = new TokensRecuperacion();
                    token.setusuarioId(rs.getInt("usuario_ID"));
                    token.settokenHash(rs.getString("token_hash"));
                    token.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                    token.setfechaExpiracion(rs.getTimestamp("fecha_expiracion"));
                    token.setusado(rs.getBoolean("usado"));
                    token.setintentos(rs.getInt("intentos"));
                }
            } 
        } catch (SQLException e) {
            System.err.println("Error al consultar token por hash: " + e.getMessage());
        }
        return token;
    }

    // Método para consultar un token de recuperación por el ID del usuario.
    @Override
    public TokensRecuperacion consultarTokenPorUsuario(int usuarioId) {
        
        String sql = "SELECT usuario_ID, "
                + "token_hash,"
                + "fecha_creacion,"
                + "fecha_expiracion,"
                + "usado,"
                + "intentos FROM tokens_recuperacion WHERE usuario_ID = ?";
        
        TokensRecuperacion token = null;
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    token = new TokensRecuperacion();
                    token.setusuarioId(rs.getInt("usuario_ID"));
                    token.settokenHash(rs.getString("token_hash"));
                    token.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                    token.setfechaExpiracion(rs.getTimestamp("fecha_expiracion"));
                    token.setusado(rs.getBoolean("usado"));
                    token.setintentos(rs.getInt("intentos"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar token por usuario: " + e.getMessage());
        }
        return token;
    }

    // Método para listar todos los tokens de recuperación almacenados en la base de datos.
    @Override
    public List<TokensRecuperacion> listarToken() {
        
        List<TokensRecuperacion> lista = new ArrayList<>();
        
            String sql = "SELECT usuario_ID, "
                + "token_hash,"
                + "fecha_creacion,"
                + "fecha_expiracion,"
                + "usado,"
                + "intentos FROM tokens_recuperacion";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                TokensRecuperacion token = new TokensRecuperacion();
                token.setusuarioId(rs.getInt("usuario_ID"));
                token.settokenHash(rs.getString("token_hash"));
                token.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                token.setfechaExpiracion(rs.getTimestamp("fecha_expiracion"));
                token.setusado(rs.getBoolean("usado"));
                token.setintentos(rs.getInt("intentos"));
                lista.add(token);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar tokens: " + e.getMessage());
                    }
        return lista;
    }

    // Método para actualizar un token de recuperación existente en la base de datos.
    @Override
    public void actualizarTokens(TokensRecuperacion token) {
        
        String sql = "UPDATE tokens_recuperacion SET Usuario_ID = ?,"
                + "token_hash = ?,"
                + "fecha_creacion = ?,"
                + "fecha_expiracion = ?,"
                + "usado = ?,"
                + "intentos = ? WHERE ID_Token = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setInt(1, token.getusuarioId());
                ps.setString(2, token.gettokenHash());
                ps.setTimestamp(3, token.getfechaCreacion());
                ps.setTimestamp(4, token.getfechaExpiracion());
                ps.setBoolean(5, token.getusado());
                ps.setInt(6, token.getintentos());
                ps.setInt(7, token.getidToken());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Tokens actualizados correctamente en la BD!");
                }
            } catch (SQLException e) {
            System.err.println("Error al actualizar tokens: " + e.getMessage());
        }
    }

    // Método para eliminar todos los tokens de recuperación asociados a un usuario específico en la base de datos.
    @Override
    public void eliminarTokensPorUsuario(int usuarioId) {
        
        String sql = "DELETE FROM tokens_recuperacion WHERE Usuario_ID = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Token eliminado correctamente en la BD!");
                }
            } catch (SQLException e) {
            System.err.println("Error al eliminar token: " + e.getMessage());
        }
    }

    // Método para eliminar un token de recuperación específico por su ID en la base de datos.
    @Override
    public void eliminarTokenPorId(int idToken) {
        
        String sql = "DELETE FROM tokens_recuperacion WHERE ID_Token = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idToken);

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Token eliminado correctamente en la BD!");
                }
            } catch (SQLException e) {
            System.err.println("Error al eliminar token: " + e.getMessage());
        }
    }
}