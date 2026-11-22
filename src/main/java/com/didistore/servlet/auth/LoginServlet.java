
package com.didistore.servlet.auth;

import com.didistore.controller.auth.SesionesController;
import com.didistore.dao.impl.auth.UsuariosDAOImpl;
import com.didistore.model.auth.Usuarios;
import com.didistore.model.auth.enums.EstadoUsuarios;
import com.didistore.util.AuditLog;
import com.google.gson.Gson;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Permitir solicitudes CORS desde cualquier origen
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    // RF-01: bloqueo tras 5 intentos fallidos durante 15 minutos
    private static final int MAX_INTENTOS_FALLIDOS = 5;
    private static final long BLOQUEO_MINUTOS = 15;

    private final Gson gson = new Gson();
    private final SesionesController sesionesController = new SesionesController();
    private final UsuariosDAOImpl usuariosDAO = new UsuariosDAOImpl();

    // Permitir solicitudes get para verificar que el servlet está activo
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("LoginServlet activo");
    }

    // Permitir solicitudes post para iniciar sesión
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> resultado = new HashMap<>();
        LoginRequest loginRequest;

        try {
            loginRequest = gson.fromJson(request.getReader(), LoginRequest.class);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Estructura JSON inválida");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        if (loginRequest == null
                || loginRequest.getEmail() == null || loginRequest.getEmail().isBlank()
                || loginRequest.getContrasena() == null || loginRequest.getContrasena().isBlank()) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Email y contraseña son obligatorios");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        String email = loginRequest.getEmail().trim().toLowerCase();
        String contrasena = loginRequest.getContrasena().trim();

        Usuarios usuario = usuariosDAO.consultarUsuariosPorEmail(email);

        if (usuario == null) {
            AuditLog.registrar(null, email, "LOGIN_FALLIDO", "auth", "usuarios", null,
                    null, null, request.getRemoteAddr(), "ERROR");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resultado.put("success", false);
            resultado.put("message", "Credenciales incorrectas");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        // RF-01: verificar si la cuenta está bloqueada temporalmente
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        if (usuario.getbloqueadoHasta() != null && usuario.getbloqueadoHasta().after(ahora)) {
            long minutosRestantes = (usuario.getbloqueadoHasta().getTime() - ahora.getTime()) / 60000 + 1;
            AuditLog.registrar(usuario.getidUsuario(), email, "LOGIN_BLOQUEADO", "auth", "usuarios",
                    usuario.getidUsuario(), null, null, request.getRemoteAddr(), "ERROR");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resultado.put("success", false);
            resultado.put("message", "Cuenta bloqueada por demasiados intentos fallidos. "
                    + "Intenta de nuevo en " + minutosRestantes + " minuto(s).");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        boolean valido = sesionesController.validarCredenciales(email, contrasena);

        if (!valido) {
            // RF-01: incrementar intentos fallidos y bloquear al alcanzar el límite
            int nuevosIntentos = usuario.getintentosFallidos() + 1;
            Timestamp bloqueadoHasta = null;
            if (nuevosIntentos >= MAX_INTENTOS_FALLIDOS) {
                bloqueadoHasta = new Timestamp(ahora.getTime() + BLOQUEO_MINUTOS * 60000);
            }
            usuariosDAO.registrarIntentoFallido(usuario.getidUsuario(), nuevosIntentos, bloqueadoHasta);

            AuditLog.registrar(usuario.getidUsuario(), email, "LOGIN_FALLIDO", "auth", "usuarios",
                    usuario.getidUsuario(), null, null, request.getRemoteAddr(), "ERROR");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resultado.put("success", false);
            if (bloqueadoHasta != null) {
                resultado.put("message", "Credenciales incorrectas. La cuenta ha sido bloqueada "
                        + BLOQUEO_MINUTOS + " minutos por demasiados intentos fallidos.");
            } else {
                int intentosRestantes = MAX_INTENTOS_FALLIDOS - nuevosIntentos;
                resultado.put("message", "Credenciales incorrectas. Intentos restantes antes del bloqueo: "
                        + intentosRestantes);
            }
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        if (usuario.getestado() == null || usuario.getestado() != EstadoUsuarios.Activo) {

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);

            resultado.put(
                "success",
                    false );

            resultado.put(
                "message",
                "Usuario inactivo o bloqueado" );

            response.getWriter().write(gson.toJson(resultado));

            return;
        }

        // RF-01: login exitoso → limpiar intentos fallidos y actualizar último login
        usuariosDAO.limpiarIntentosFallidos(usuario.getidUsuario(), ahora);

        int perfilId = usuario.getperfilId();
        String rol = mapRol(perfilId);
        
        HttpSession session = request.getSession(true);
        session.setAttribute("usuarioId", usuario.getidUsuario());
        session.setAttribute("email", usuario.getemail());
        session.setAttribute("nombre", usuario.getnombre());
        session.setAttribute("apellido", usuario.getapellido());
        session.setAttribute("perfilId", usuario.getperfilId());
        session.setAttribute("estado", usuario.getestado());
        session.setAttribute("rol", rol);

        AuditLog.registrar(usuario.getidUsuario(), email, "LOGIN", "auth", "usuarios",
                usuario.getidUsuario(), null, null, request.getRemoteAddr(), "EXITO");

        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("idUsuario", usuario.getidUsuario());
        usuarioData.put("email", usuario.getemail());
        usuarioData.put("nombre", usuario.getnombre());
        usuarioData.put("apellido", usuario.getapellido());
        usuarioData.put("perfilId", perfilId);
        usuarioData.put("estado", usuario.getestado());
        usuarioData.put("rol", rol);

        resultado.put("success", true);
        resultado.put("message", "Inicio de sesión correcto");
        resultado.put("token", session.getId());
        resultado.put("usuario", usuarioData);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(resultado));
    }

    // Método para mapear el perfilId a un rol
    private String mapRol(int perfilId) {
        switch (perfilId) {
            case 1:
                return "ADMIN";
            case 2:
                return "EMPLEADO";
            case 3:
                return "CLIENTE";
            default:
                return "SIN_ROL";
        }
    }

    // Clase interna para representar la solicitud de inicio de sesión
    public static class LoginRequest {
        private String email;
        private String contrasena;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getContrasena() {
            return contrasena;
        }

        public void setContrasena(String contrasena) {
            this.contrasena = contrasena;
        }
    }
}