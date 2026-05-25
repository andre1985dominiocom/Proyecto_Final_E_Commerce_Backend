
package com.didistore.controller.auth.servlet;

import com.didistore.controller.auth.PermisosController;
import com.didistore.model.auth.Permisos;
import com.google.gson.Gson;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/auth/permisos")
public class PermisosServlet extends HttpServlet {
    
    private final Gson gson = new Gson();
    private final PermisosController permisosController = new PermisosController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("idPermiso");

        if (idParam != null) {
            try {
                int idPermiso = Integer.parseInt(idParam);
                Permisos permiso = permisosController.consultarPermisoPorId(idPermiso);

                if (permiso != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(gson.toJson(permiso));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"message\":\"Permiso no encontrado\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"idPermiso inválido\"}");
            }
        } else {
            List<Permisos> lista = permisosController.listarPermisos();
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
        Permisos permiso = gson.fromJson(reader, Permisos.class);

        permisosController.agregarPermiso(permiso);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("{\"message\":\"Permiso creado correctamente\"}");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        Permisos permiso = gson.fromJson(reader, Permisos.class);

        permisosController.actualizarPermiso(permiso);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{\"message\":\"Permiso actualizado correctamente\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("idPermiso");

        if (idParam != null) {
            try {
                int idPermiso = Integer.parseInt(idParam);
                permisosController.eliminarPermiso(idPermiso);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\":\"Permiso eliminado correctamente\"}");
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"idPermiso inválido\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Falta idPermiso\"}");
        }
    }
}