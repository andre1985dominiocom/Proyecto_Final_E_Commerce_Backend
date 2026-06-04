
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

// Implementación de la interfaz IUsuariosDAO para gestionar operaciones CRUD en la tabla "usuarios" de la base de datos.
public class UsuariosDAOImpl implements IUsuariosDAO {

    // Método para insertar un nuevo usuario en la base de datos.
    @Override
    public void insertarUsuarios(Usuarios usuario) {

        String sql = "INSERT INTO usuarios (email, contrasena, nombre, apellido, documento, tipo_documento, perfil_id, estado, email_verificado, fecha_creacion, fecha_actualizacion, fecha_ultimo_login) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
            ps.setBoolean(9, usuario.getemailVerificado());
            ps.setTimestamp(10, usuario.getfechaCreacion());
            ps.setTimestamp(11, usuario.getfechaActualizacion());
            ps.setTimestamp(12, usuario.getfechaUltimoLogin());

            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Usuario insertado correctamente en la BD!");
            }

        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
        }
    }

    // Método para listar todos los usuarios de la base de datos.
    @Override
    public List<Usuarios> listarUsuarios() {

        List<Usuarios> lista = new ArrayList<>();

        String sql = "SELECT id_Usuario, email, contrasena, nombre, apellido, documento, tipo_documento, perfil_id, estado, email_verificado, fecha_creacion, fecha_actualizacion, fecha_ultimo_login FROM usuarios";

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuarios usuario = new Usuarios();

                usuario.setidUsuario(rs.getInt("id_Usuario"));
                usuario.setemail(rs.getString("email"));
                usuario.setcontrasena(rs.getString("contrasena"));
                usuario.setnombre(rs.getString("nombre"));
                usuario.setapellido(rs.getString("apellido"));
                usuario.setdocumento(rs.getString("documento"));

                String tipoDocumentoStr = rs.getString("tipo_documento");
                if (tipoDocumentoStr != null && !tipoDocumentoStr.isEmpty()) {
                    usuario.settipoDocumento(TipoDocumentos.valueOf(tipoDocumentoStr));
                }

                usuario.setperfilId(rs.getInt("perfil_id"));

                String estadoStr = rs.getString("estado");
                if (estadoStr != null && !estadoStr.isEmpty()) {
                    usuario.setestado(EstadoUsuarios.valueOf(estadoStr));
                }

                usuario.setemailVerificado(rs.getBoolean("email_verificado"));
                usuario.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                usuario.setfechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
                usuario.setfechaUltimoLogin(rs.getTimestamp("fecha_ultimo_login"));

                lista.add(usuario);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }

        return lista;
    }

    // Método para consultar un usuario por su ID.
    @Override
    public Usuarios consultarUsuariosPorId(int usuarioId) {

        String sql = "SELECT id_Usuario, email, contrasena, nombre, apellido, documento, tipo_documento, perfil_id, estado, email_verificado, fecha_creacion, fecha_actualizacion, fecha_ultimo_login FROM usuarios WHERE id_Usuario = ?";

        Usuarios usuario = null;

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, usuarioId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuarios();

                    usuario.setidUsuario(rs.getInt("id_Usuario"));
                    usuario.setemail(rs.getString("email"));
                    usuario.setcontrasena(rs.getString("contrasena"));
                    usuario.setnombre(rs.getString("nombre"));
                    usuario.setapellido(rs.getString("apellido"));
                    usuario.setdocumento(rs.getString("documento"));

                    String tipoDocumentoStr = rs.getString("tipo_documento");
                    if (tipoDocumentoStr != null && !tipoDocumentoStr.isEmpty()) {
                        usuario.settipoDocumento(TipoDocumentos.valueOf(tipoDocumentoStr));
                    }

                    usuario.setperfilId(rs.getInt("perfil_id"));

                    String estadoStr = rs.getString("estado");
                    if (estadoStr != null && !estadoStr.isEmpty()) {
                        usuario.setestado(EstadoUsuarios.valueOf(estadoStr));
                    }

                    usuario.setemailVerificado(rs.getBoolean("email_verificado"));
                    usuario.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                    usuario.setfechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
                    usuario.setfechaUltimoLogin(rs.getTimestamp("fecha_ultimo_login"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar usuario por ID: " + e.getMessage());
        }

        return usuario;
    }

    // Método para consultar un usuario por su email.
    @Override
    public Usuarios consultarUsuariosPorEmail(String email) {

        String sql = "SELECT id_Usuario, email, contrasena, nombre, apellido, documento, tipo_documento, perfil_id, estado, email_verificado, fecha_creacion, fecha_actualizacion, fecha_ultimo_login FROM usuarios WHERE email = ?";

        Usuarios usuario = null;

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuarios();

                    usuario.setidUsuario(rs.getInt("id_Usuario"));
                    usuario.setemail(rs.getString("email"));
                    usuario.setcontrasena(rs.getString("contrasena"));
                    usuario.setnombre(rs.getString("nombre"));
                    usuario.setapellido(rs.getString("apellido"));
                    usuario.setdocumento(rs.getString("documento"));

                    String tipoDocumentoStr = rs.getString("tipo_documento");
                    if (tipoDocumentoStr != null && !tipoDocumentoStr.isEmpty()) {
                        usuario.settipoDocumento(TipoDocumentos.valueOf(tipoDocumentoStr));
                    }

                    usuario.setperfilId(rs.getInt("perfil_id"));

                    String estadoStr = rs.getString("estado");
                    if (estadoStr != null && !estadoStr.isEmpty()) {
                        usuario.setestado(EstadoUsuarios.valueOf(estadoStr));
                    }

                    usuario.setemailVerificado(rs.getBoolean("email_verificado"));
                    usuario.setfechaCreacion(rs.getTimestamp("fecha_creacion"));
                    usuario.setfechaActualizacion(rs.getTimestamp("fecha_actualizacion"));
                    usuario.setfechaUltimoLogin(rs.getTimestamp("fecha_ultimo_login"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar usuario por email: " + e.getMessage());
        }

        return usuario;
    }

    // Método para actualizar un usuario existente en la base de datos.
    @Override
    public void actualizarUsuarios(Usuarios usuario) {

        String sql = "UPDATE usuarios SET email = ?, contrasena = ?, nombre = ?, apellido = ?, documento = ?, tipo_documento = ?, perfil_id = ?, estado = ?, email_verificado = ?, fecha_creacion = ?, fecha_actualizacion = ?, fecha_ultimo_login = ? WHERE id_Usuario = ?";

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
            ps.setBoolean(9, usuario.getemailVerificado());
            ps.setTimestamp(10, usuario.getfechaCreacion());
            ps.setTimestamp(11, usuario.getfechaActualizacion());
            ps.setTimestamp(12, usuario.getfechaUltimoLogin());
            ps.setInt(13, usuario.getidUsuario());

            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Usuario actualizado correctamente en la BD!");
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
        }
    }
    
    // Método para actualizar la contraseña de un usuario existente en la base de datos.
    @Override
    public void actualizarContrasena(int idUsuario, String nuevaContrasena) {
        String sql = "UPDATE usuarios SET contrasena = ?, fecha_actualizacion = ? WHERE id_Usuario = ?";

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nuevaContrasena);
            ps.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setInt(3, idUsuario);

            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Contraseña actualizada correctamente en la BD!");
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
        }
    }

    // Método para eliminar un usuario existente en la base de datos.
    @Override
    public void eliminarUsuarios(int idUsuario) {

        String sql = "DELETE FROM usuarios WHERE id_Usuario = ?";

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Usuario eliminado correctamente en la BD!");
            }

        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
        }
    }
}