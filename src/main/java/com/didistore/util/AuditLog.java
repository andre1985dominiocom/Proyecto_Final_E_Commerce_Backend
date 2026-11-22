package com.didistore.util;

import com.didistore.config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Helper de auditoría (RF-10 básica). Escribe en logs_auditoria de forma
 * best-effort: si falla, solo imprime el error y no interrumpe el flujo principal.
 */
public final class AuditLog {

    private static final String INSERT_SQL =
            "INSERT INTO logs_auditoria "
            + "(usuario_id, usuario_email, accion, modulo, entidad, entidad_id, "
            + "datos_antes, datos_despues, ip, resultado) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private AuditLog() {
    }

    public static void registrar(Integer usuarioId, String usuarioEmail, String accion,
            String modulo, String entidad, Integer entidadId, String datosAntes,
            String datosDespues, String ip, String resultado) {

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(INSERT_SQL)) {

            if (usuarioId != null) {
                ps.setInt(1, usuarioId);
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            ps.setString(2, usuarioEmail);
            ps.setString(3, accion);
            ps.setString(4, modulo);
            ps.setString(5, entidad);
            if (entidadId != null) {
                ps.setInt(6, entidadId);
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.setString(7, datosAntes);
            ps.setString(8, datosDespues);
            ps.setString(9, ip);
            ps.setString(10, resultado != null ? resultado : "EXITO");

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("AuditLog error (" + accion + "): " + e.getMessage());
        }
    }
}
