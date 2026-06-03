
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
        
        LoginRequest loginRequest = null;
        
        try {
            loginRequest = gson.fromJson(request.getReader(), LoginRequest.class);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            Map<String, Object> errorJson = new HashMap<>();
            errorJson.put("success", false);
            errorJson.put("message", "Estructura JSON inválida");
            response.getWriter().write(gson.toJson(errorJson));
            return;
        }
        Map<String, Object> resultado = new HashMap<>();

        if (loginRequest == null
                || loginRequest.getEmail() == null || loginRequest.getEmail().isBlank()
                || loginRequest.getContrasena() == null || loginRequest.getContrasena().isBlank()) {
            
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Email y contraseña son obligatorios");
            response.getWriter().write(gson.toJson(resultado));
            return;

            
        }

        boolean valido = sesionesController.validarCredenciales(
                loginRequest.getEmail(),
                loginRequest.getContrasena()
        );

        if (valido) {
            Usuarios usuario = usuariosDAO.consultarUsuariosPorEmail(loginRequest.getEmail());

            HttpSession session = request.getSession(true);
            session.setAttribute("usuarioId", usuario != null ? usuario.getidUsuario() : 0);
            session.setAttribute("email", loginRequest.getEmail());

            Map<String, Object> usuarioData = new HashMap<>();
            if (usuario != null) {
                usuarioData.put("idUsuario", usuario.getidUsuario());
                usuarioData.put("email", usuario.getemail());
                usuarioData.put("nombre", usuario.getnombre());
                usuarioData.put("apellido", usuario.getapellido());
                usuarioData.put("perfilId", usuario.getperfilId());
            } else {
                usuarioData.put("email", loginRequest.getEmail());
            }

            resultado.put("success", true);
            resultado.put("message", "Inicio de sesión correcto");
            resultado.put("token", session.getId());
            resultado.put("usuario", usuarioData);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            resultado.put("success", false);
            resultado.put("message", "Credenciales incorrectas");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        response.getWriter().write(gson.toJson(resultado));
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