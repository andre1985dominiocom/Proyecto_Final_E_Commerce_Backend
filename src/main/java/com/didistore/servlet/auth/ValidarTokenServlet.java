
package com.didistore.servlet.auth;

import com.didistore.controller.auth.TokensRecuperacionController;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/auth/validar-token")
public class ValidarTokenServlet extends HttpServlet {
    
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
            resultado.put("message", "Token requerido");
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
}