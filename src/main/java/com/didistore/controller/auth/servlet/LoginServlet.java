
package com.didistore.controller.auth.servlet;

import com.didistore.controller.auth.SesionesController;
import com.didistore.dao.impl.auth.UsuariosDAOImpl;
import com.didistore.model.auth.Usuarios;
import com.google.gson.Gson;
import java.io.IOException;
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
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final SesionesController sesionesController = new SesionesController();
    private final UsuariosDAOImpl usuariosDAO = new UsuariosDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("LoginServlet activo");
    }

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

        boolean valido = sesionesController.validarCredenciales(email, contrasena);

        if (!valido) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resultado.put("success", false);
            resultado.put("message", "Credenciales incorrectas");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        Usuarios usuario = usuariosDAO.consultarUsuariosPorEmail(email);

        if (usuario == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resultado.put("success", false);
            resultado.put("message", "Usuario no encontrado");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        int perfilId = usuario.getperfilId();
        String rol = mapRol(perfilId);
        
        HttpSession session = request.getSession(true);
        session.setAttribute("usuarioId", usuario.getidUsuario());
        session.setAttribute("email", usuario.getemail());
        session.setAttribute("nombre", usuario.getnombre());
        session.setAttribute("apellido", usuario.getapellido());
        session.setAttribute("perfilId", perfilId);
        session.setAttribute("rol", rol);

        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("idUsuario", usuario.getidUsuario());
        usuarioData.put("email", usuario.getemail());
        usuarioData.put("nombre", usuario.getnombre());
        usuarioData.put("apellido", usuario.getapellido());
        usuarioData.put("perfilId", perfilId);
        usuarioData.put("rol", rol);

        resultado.put("success", true);
        resultado.put("message", "Inicio de sesión correcto");
        resultado.put("token", session.getId());
        resultado.put("usuario", usuarioData);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(resultado));
    }

    private String mapRol(int perfilId) {
        switch (perfilId) {
            case 1:
                return "Administrador";
            case 2:
                return "Empleado";
            case 3:
                return "Cliente";
            default:
                return "Sin_Rol";
        }
    }

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