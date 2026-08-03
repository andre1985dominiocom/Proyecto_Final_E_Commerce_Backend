
package com.didistore.servlet.auth;

import com.didistore.controller.auth.TokensRecuperacionController;
import com.didistore.dao.impl.auth.UsuariosDAOImpl;
import com.didistore.model.auth.TokensRecuperacion;
import com.didistore.model.auth.Usuarios;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Permitir solicitude de tokens de recuperación de contraseña y validación de los mismos
@WebServlet("/auth/recuperacion")
public class TokensRecuperacionServlet extends HttpServlet {
    
    private final Gson gson = new Gson();
    private final TokensRecuperacionController tokenController = new TokensRecuperacionController();
    private final UsuariosDAOImpl usuariosDAO = new UsuariosDAOImpl();
    
    // GET: Validar token de recuperación
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String tokenHash = request.getParameter("token");
        
        Map<String, Object> resultado = new HashMap<>();
        
        if (tokenHash == null || tokenHash.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Debe enviar el parámetro token");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }
        
        boolean valido = tokenController.tokenEsValido(tokenHash);
        
        if (valido) {
            response.setStatus(HttpServletResponse.SC_OK);
            resultado.put("success", true);
            resultado.put("message", "Token válido");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resultado.put("success", false);
            resultado.put("message", "Token inválido o expirado");
        }
        
        response.getWriter().write(gson.toJson(resultado));
    }
    
    // POST: Solicitar token de recuperación
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        TokenRequest tokenRequest = gson.fromJson(reader, TokenRequest.class);

        Map<String, Object> resultado = new HashMap<>();

        if (tokenRequest == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Body inválido");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        int usuarioId = tokenRequest.getUsuarioId();

        if (usuarioId <= 0 && tokenRequest.getEmail() != null && !tokenRequest.getEmail().isBlank()) {
            Usuarios usuario = usuariosDAO.consultarUsuariosPorEmail(tokenRequest.getEmail().trim().toLowerCase());
            if (usuario == null) {
                response.setStatus(HttpServletResponse.SC_OK);
                resultado.put("success", true);
                resultado.put("message", "Si el correo está registrado, recibirás instrucciones.");
                response.getWriter().write(gson.toJson(resultado));
                return;
            }
            usuarioId = usuario.getidUsuario();
        }

        if (usuarioId <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Debe enviar email o usuarioId válido");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        Timestamp fechaExpiracion = new Timestamp(System.currentTimeMillis() + 3600000);

        TokensRecuperacion token = tokenController.crearTokenParaUsuario(
                usuarioId,
                fechaExpiracion
        );

        response.setStatus(HttpServletResponse.SC_OK);
        resultado.put("success", true);
        resultado.put("message", "Token generado correctamente");
        //resultado.put("token", token.gettokenHash());

        response.getWriter().write(gson.toJson(resultado));
    }

    // Clase interna para mapear la solicitud de token
    public static class TokenRequest {
        private int usuarioId;
        private String email;

        public int getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(int usuarioId) {
            this.usuarioId = usuarioId;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}