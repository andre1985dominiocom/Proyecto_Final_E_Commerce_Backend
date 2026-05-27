
package com.didistore.controller.auth.servlet;

import com.didistore.controller.auth.SesionesController;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    private final Gson gson = new Gson();
    private final SesionesController sesionesController = new SesionesController();
    
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

        BufferedReader reader = request.getReader();
        LoginRequest loginRequest = gson.fromJson(reader, LoginRequest.class);

        Map<String, Object> resultado = new HashMap<>();

        if (loginRequest == null
                || loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()
                || loginRequest.getContrasena() == null || loginRequest.getContrasena().trim().isEmpty()) {

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
            resultado.put("success", true);
            resultado.put("message", "Inicio de sesión correcto");
            resultado.put("token", "token-demo-123");
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