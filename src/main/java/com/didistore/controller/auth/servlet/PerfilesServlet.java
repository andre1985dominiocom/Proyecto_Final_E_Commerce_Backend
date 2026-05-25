
package com.didistore.controller.auth.servlet;

import com.didistore.controller.auth.PerfilesController;
import com.didistore.model.auth.Perfiles;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/auth/perfiles")
public class PerfilesServlet extends HttpServlet {
    
    private final Gson gson = new Gson();
    private final PerfilesController perfilesController = new PerfilesController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("idPerfil");

        if (idParam != null) {
            try {
                int idPerfil = Integer.parseInt(idParam);
                Perfiles perfil = perfilesController.consultarPerfilPorId(idPerfil);

                if (perfil != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(gson.toJson(perfil));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"message\":\"Perfil no encontrado\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"idPerfil inválido\"}");
            }
        } else {
            List<Perfiles> lista = perfilesController.listarPerfiles();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(lista));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        Perfiles perfil = gson.fromJson(reader, Perfiles.class);

        perfilesController.agregarPerfil(perfil);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("{\"message\":\"Perfil creado correctamente\"}");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        Perfiles perfil = gson.fromJson(reader, Perfiles.class);

        perfilesController.actualizarPerfil(perfil);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\":\"Perfil actualizado correctamente\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("idPerfil");

        if (idParam != null) {
            try {
                int idPerfil = Integer.parseInt(idParam);
                perfilesController.eliminarPerfil(idPerfil);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\":\"Perfil eliminado correctamente\"}");
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"idPerfil inválido\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Falta idPerfil\"}");
        }
    }
}