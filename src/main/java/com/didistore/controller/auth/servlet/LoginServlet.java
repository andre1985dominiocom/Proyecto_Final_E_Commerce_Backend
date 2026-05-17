
package com.didistore.controller.auth.servlet;

import com.didistore.dao.impl.auth.SesionesDAOImpl;
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

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    private final Gson gson = new Gson();
    private final SesionesDAOImpl sesionesDAO = new SesionesDAOImpl();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        BufferedReader reader = request.getReader();
        LoginRequest loginRequest = gson.fromJson(reader, LoginRequest.class);
    
        String email = loginRequest.getemail();
        String contrasena = loginRequest.getcontrasena();
 
        Map<String, Object> resultado = new HashMap<>(); 
    
        boolean valido = sesionesDAO.validarCredenciales(email, contrasena);
    
        if (valido) {
            resultado.put("success", true);
            resultado.put("message", "Inicio sesión correcto");
            resultado.put("token", "token-demo-123");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            resultado.put("Sucess", false);
            resultado.put("Message", "Credenciales Incorrectas");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }    
        response.getWriter().write(gson.toJson(resultado));   
    }
    
    public static class LoginRequest {
        private String email;
        private String contrasena;
        
        public String getemail() {
            return email;
        }
        
        public void setemail(String email) {
            this.email = email;
        }
        
        public String getcontrasena() {
            return contrasena;
        }
        
        public void setcontrasena(String contrasena) {
            this.contrasena = contrasena;
        }
    }
}