
package com.didistore.controller.auth.servlet;

import com.didistore.controller.auth.TokensRecuperacionController;
import com.didistore.dao.impl.auth.UsuariosDAOImpl;
import com.didistore.model.auth.TokensRecuperacion;
import com.google.gson.Gson;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/auth/restablecer-password")
public class RestablecerPasswordServlet extends HttpServlet {
    
    private final Gson gson = new Gson();
    private final TokensRecuperacionController tokenController = new TokensRecuperacionController();
    private final UsuariosDAOImpl usuariosDAO = new UsuariosDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        RestablecerRequest datos = gson.fromJson(reader, RestablecerRequest.class);

        Map<String, Object> resultado = new HashMap<>();

        if (datos == null || datos.getToken() == null || datos.getToken().trim().isEmpty()
                || datos.getnuevaContrasena() == null || datos.getnuevaContrasena().trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Datos incompletos");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        boolean tokenValido = tokenController.tokenEsValido(datos.getToken());

        if (!tokenValido) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resultado.put("success", false);
            resultado.put("message", "Token inválido o expirado");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        TokensRecuperacion token = tokenController.consultarTokenPorHash(datos.getToken());

        if (token == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resultado.put("success", false);
            resultado.put("message", "Token no encontrado");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        usuariosDAO.actualizarContrasena(token.getusuarioId(), datos.getnuevaContrasena());
        
        tokenController.marcarTokenComoUsado(datos.getToken());

        response.setStatus(HttpServletResponse.SC_OK);
        resultado.put("success", true);
        resultado.put("message", "Contraseña actualizada correctamente");

        response.getWriter().write(gson.toJson(resultado));
    }

    public static class RestablecerRequest {
        private String token;
        private String nuevaContrasena;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getnuevaContrasena() {
            return nuevaContrasena;
        }

        public void setnuevaContrasena(String nuevaContrasena) {
            this.nuevaContrasena = nuevaContrasena;
        }
    }  
}