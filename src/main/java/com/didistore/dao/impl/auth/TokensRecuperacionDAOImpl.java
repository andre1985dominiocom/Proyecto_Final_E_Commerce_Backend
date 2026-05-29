
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
public class TokensRecuperacionDAOImpl implements ITokensRecuperacionDAO {
    
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