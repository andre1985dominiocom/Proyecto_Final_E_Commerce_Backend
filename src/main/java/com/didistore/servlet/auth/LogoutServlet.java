
package com.didistore.servlet.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Permitir cerrar sesión desde el cliente, eliminando la sesión del servidor y devolviendo un mensaje de éxito.
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    
    // Procesar la solicitud de cierre de sesión, invalidando la sesión actual y devolviendo un mensaje JSON de éxito.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        HttpSession session = request.getSession(false);

        if(session != null){

            session.invalidate();
        }

        PrintWriter out = response.getWriter(); out.print("""
            {
            "ok": true,
            "message": "Sesión cerrada correctamente"
            }
        """);
    }

    // Permitir que la solicitud GET también cierre la sesión,
    // llamando al método doPost para manejar la lógica de cierre de sesión.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        doPost(request,response);
    }
}