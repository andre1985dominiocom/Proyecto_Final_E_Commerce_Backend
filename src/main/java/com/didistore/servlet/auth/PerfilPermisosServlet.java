
package com.didistore.servlet.auth;

import com.didistore.controller.auth.PerfilPermisosController;
import com.didistore.model.auth.PerfilPermisos;
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

// Permite realizar operaciones CRUD sobre la relación entre perfiles y permisos
@WebServlet("/auth/perfil-permisos")
public class PerfilPermisosServlet extends HttpServlet {
    
    private final Gson gson = new Gson();
    private final PerfilPermisosController perfilPermisosController = new PerfilPermisosController();

    // GET: Consultar la relación perfil-permiso, listar permisos por perfil o listar perfiles por permiso
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idPerfilParam = request.getParameter("idPerfil");
        String idPermisoParam = request.getParameter("idPermiso");

        try {
            if (idPerfilParam != null && idPermisoParam != null) {
                int idPerfil = Integer.parseInt(idPerfilParam);
                int idPermiso = Integer.parseInt(idPermisoParam);

                PerfilPermisos perfilPermiso = perfilPermisosController.consultarPerfilPermiso(idPerfil, idPermiso);

                if (perfilPermiso != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(gson.toJson(perfilPermiso));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"message\":\"Relación perfil-permiso no encontrada\"}");
                }

            } else if (idPerfilParam != null) {
                int idPerfil = Integer.parseInt(idPerfilParam);
                List<PerfilPermisos> lista = perfilPermisosController.listarPermisosPorPerfil(idPerfil);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(lista));

            } else if (idPermisoParam != null) {
                int idPermiso = Integer.parseInt(idPermisoParam);
                List<PerfilPermisos> lista = perfilPermisosController.listarPerfilesPorPermiso(idPermiso);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(lista));

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"Debe enviar idPerfil o idPermiso\"}");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Parámetros inválidos\"}");
        }
    }

    // POST: Asignar un permiso a un perfil
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        PerfilPermisos perfilPermiso = gson.fromJson(reader, PerfilPermisos.class);

        boolean insertado = perfilPermisosController.asignarPermisoAPerfil(
                perfilPermiso.getidPerfil(),
                perfilPermiso.getidPermiso()
        );

        if (insertado) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write("{\"message\":\"Permiso asignado al perfil correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"No se pudo asignar el permiso al perfil\"}");
        }
    }
    // DELETE: Eliminar la relación perfil-permiso
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idPerfilParam = request.getParameter("idPerfil");
        String idPermisoParam = request.getParameter("idPermiso");

        if (idPerfilParam != null && idPermisoParam != null) {
            try {
                int idPerfil = Integer.parseInt(idPerfilParam);
                int idPermiso = Integer.parseInt(idPermisoParam);

                perfilPermisosController.eliminarPerfilPermiso(idPerfil, idPermiso);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\":\"Relación perfil-permiso eliminada correctamente\"}");
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"Parámetros inválidos\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"message\":\"Faltan idPerfil e idPermiso\"}");
        }
    }
}