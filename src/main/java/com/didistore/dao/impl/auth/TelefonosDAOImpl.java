
package com.didistore.dao.impl.auth;

import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.auth.ITelefonosDAO;
import com.didistore.model.auth.Telefonos;
import com.didistore.model.auth.enums.TipoTelefonos;
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
public class TelefonosDAOImpl implements ITelefonosDAO {

    @Override
    public void insertarTelefono(Telefonos telefono) {
        
        String sql = "INSERT INTO telefonos (Usuario_ID,"
                + "tipo,"
                + "numero,"
                + "es_verificado,"
                + "fecha_agregado) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setInt(1, telefono.getusuarioId());
                ps.setString(2, telefono.gettipo() != null ? telefono.gettipo().name() : null);
                ps.setString(3, telefono.getnumero());
                ps.setInt(4, telefono.getesVerificado());
                ps.setTimestamp(5, telefono.getfechaAgregado());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Telefono insertado correctamente en la BD!");
                }
            } catch (SQLException e) {
            System.err.println("Error al insertar telefono: " + e.getMessage());
        }
        
    }

    @Override
    public Telefonos consultarTelefono(int idTelefono) {
        
        String sql = "SELECT (Usuario_ID,"
                + "tipo,"
                + "numero,"
                + "es_verificado,"
                + "fecha_agregado) FROM telefono WHERE ID_Telefono = ?";
        
        Telefonos telefono = null;
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, telefono.getidTelefono());
            
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    telefono = new Telefonos();
                    telefono.setusuarioId(rs.getInt("usuario_ID"));
                    String tipoStr = rs.getString("tipo");
                    telefono.settipo(TipoTelefonos.valueOf(tipoStr));
                    telefono.setnumero(rs.getString("numero"));
                    telefono.setesVerificado(rs.getInt("es_verificado"));
                    telefono.setfechaAgregado(rs.getTimestamp("fecha_agregado"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar telefono: " + e.getMessage());
        }
        return telefono;
        
    }

    @Override
    public Telefonos consultarTelefonoPorUsuario(int usuarioId) {
        
        String sql = "SELECT (Usuario_ID,"
                + "tipo,"
                + "numero,"
                + "es_verificado,"
                + "fecha_agregado) FROM usuarios WHERE usuario_ID = ?";
        
        Telefonos telefono = null;
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, telefono.getusuarioId());
            
            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()) {
                    telefono = new Telefonos();
                    telefono.setusuarioId(rs.getInt("usuario_ID"));
                    String tipoStr = rs.getString("tipo");
                    telefono.settipo(TipoTelefonos.valueOf(tipoStr));
                    telefono.setnumero(rs.getString("numero"));
                    telefono.setesVerificado(rs.getInt("es_verificado"));
                    telefono.setfechaAgregado(rs.getTimestamp("fecha_agregado"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar telefono por ID: " + e.getMessage());
        }
        return telefono;
    }

    @Override
    public List<Telefonos> listarTelefono() {
        
        List<Telefonos> lista = new ArrayList<>();
            String sql = "SELECT (Usuario_ID,"
                + "tipo,"
                + "numero,"
                + "es_verificado,"
                + "fecha_agregado) FROM telefonos";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Telefonos telefono = new Telefonos();
                telefono.setusuarioId(rs.getInt("usuario_ID"));
                String tipoStr = rs.getString("tipo");
                telefono.settipo(TipoTelefonos.valueOf(tipoStr));
                telefono.setnumero(rs.getString("numero"));
                telefono.setesVerificado(rs.getInt("es_verificado"));
                telefono.setfechaAgregado(rs.getTimestamp("fecha_agregado"));
                lista.add(telefono);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar telefono: " + e.getMessage());
                    }
        return lista;
    }

    @Override
    public void actualizarTelefono(Telefonos telefono) {
        
        String sql = "UPDATE telefonos (SET Usuario_ID = ?,"
                + "SET tipo = ?,"
                + "SET numero = ?,"
                + "SET es_verificado = ?,"
                + "SET fecha_agregado = ?) WHERE ID_Telefono = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
                ps.setInt(1, telefono.getusuarioId());
                ps.setString(2, telefono.gettipo() != null ? telefono.gettipo().name() : null);
                ps.setString(3, telefono.getnumero());
                ps.setInt(4, telefono.getesVerificado());
                ps.setTimestamp(5, telefono.getfechaAgregado());

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Telefono actualizado correctamente en la BD!");
                }
            } catch (SQLException e) {
            System.err.println("Error al actualizar telefono: " + e.getMessage());
        }
    }

    @Override
    public void eliminarTelefonoPorUsuario(int usuarioId) {
        
        String sql = "DELETE FROM telefonos WHERE Usuario_ID = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, usuarioId);

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Telefono eliminado correctamente en la BD!");
                }
            } catch (SQLException e) {
            System.err.println("Error al eliminar telefono con Usuario ID: " + e.getMessage());
        }
    }

    @Override
    public void eliminarTelefonoPorId(int idTelefono) {
        
        String sql = "DELETE FROM telefonos WHERE ID_Telefono = ?";
        
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idTelefono);

                int filasAfectadas = ps.executeUpdate();
                
                if (!con.getAutoCommit()) {
                    con.commit();
                }

                if (filasAfectadas > 0) {
                    System.out.println("¡Telefono eliminado correctamente en la BD!");
                }
            } catch (SQLException e) {
            System.err.println("Error al eliminar telefono con ID: " + e.getMessage());
        }
    }
}