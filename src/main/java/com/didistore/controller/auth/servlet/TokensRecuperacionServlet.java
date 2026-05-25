
package com.didistore.controller.auth.servlet;

import com.didistore.controller.auth.TokensRecuperacionController;
import com.didistore.model.auth.TokensRecuperacion;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/auth/recuperacion")
public class TokensRecuperacionServlet extends HttpServlet {
    
    private final Gson gson = new Gson();
    private final TokensRecuperacionController tokenController = new TokensRecuperacionController();
    
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
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        TokenRequest tokenRequest = gson.fromJson(reader, TokenRequest.class);

        Map<String, Object> resultado = new HashMap<>();

        if (tokenRequest == null || tokenRequest.getUsuarioId() <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "usuarioId inválido");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        Timestamp fechaExpiracion = new Timestamp(System.currentTimeMillis() + 3600000);

        TokensRecuperacion token = tokenController.crearTokenParaUsuario(
                tokenRequest.getUsuarioId(),
                fechaExpiracion
        );

        response.setStatus(HttpServletResponse.SC_OK);
        resultado.put("success", true);
        resultado.put("message", "Token generado correctamente");
        resultado.put("token", token.gettokenHash());

        response.getWriter().write(gson.toJson(resultado));
    }

    public static class TokenRequest {
        private int usuarioId;

        public int getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(int usuarioId) {
            this.usuarioId = usuarioId;
        }
    }
}