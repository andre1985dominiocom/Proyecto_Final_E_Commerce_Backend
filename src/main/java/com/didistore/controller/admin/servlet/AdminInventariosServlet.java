package com.didistore.controller.admin.servlet;

import com.didistore.controller.admin.AdminInventariosController;
import com.didistore.model.catalog.Inventarios;
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

@WebServlet("/admin/inventarios")
public class AdminInventariosServlet extends HttpServlet {

    private AdminInventariosController inventariocontroller;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        this.inventariocontroller = new AdminInventariosController();
        this.gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCabecerasJSON(response);
        String idParam = request.getParameter("id");

        try {
            if (idParam != null && !idParam.trim().isEmpty()) {
                int id = Integer.parseInt(idParam);
                Inventarios inventario = inventariocontroller.consultarInventariosPorId(id);

                if (inventario == null) {
                    List<Inventarios> inventarios = inventariocontroller.listarInventarioPorProducto(id);
                    inventario = inventarios.isEmpty() ? null : inventarios.get(0);
                }

                if (inventario != null) {
                    response.getWriter().write(gson.toJson(inventario));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\": \"Inventario no encontrado\"}");
                }
            } else {
                List<Inventarios> lista = inventariocontroller.listarInventario();
                response.getWriter().write(gson.toJson(lista));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"El ID provisto no es válido\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCabecerasJSON(response);

        Inventarios inventarioNuevo = leerJsonDesdeCuerpo(request, Inventarios.class);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("{\"mensaje\": \"Funcionalidad POST lista para insertar\"}");
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCabecerasJSON(response);

        try {
            Inventarios inventarioEditar = leerJsonDesdeCuerpo(request, Inventarios.class);
            boolean exito = inventariocontroller.actualizarInventario(inventarioEditar);

            if (exito) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"success\": true, \"mensaje\": \"Inventario actualizado con éxito\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"success\": false, \"error\": \"Validación de negocio fallida o ID no encontrado\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\": false, \"error\": \"Error al procesar los datos\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCabecerasJSON(response);
        String idParam = request.getParameter("id");

        if (idParam == null || idParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"error\": \"Falta el parámetro ID\"}");
            return;
        }

        try {
            int idInventario = Integer.parseInt(idParam);
            boolean eliminado = inventariocontroller.eliminarInventario(idInventario);

            if (eliminado) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"success\": true, \"mensaje\": \"Inventario eliminado con éxito\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"success\": false, \"error\": \"No se pudo eliminar el inventario\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"error\": \"ID inválido\"}");
        }
    }

    private void configurarCabecerasJSON(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private <T> T leerJsonDesdeCuerpo(HttpServletRequest request, Class<T> claseDestino) throws IOException {
        StringBuilder sb = new StringBuilder();
        String linea;
        try (BufferedReader reader = request.getReader()) {
            while ((linea = reader.readLine()) != null) {
                sb.append(linea);
            }
        }
        return gson.fromJson(sb.toString(), claseDestino);
    }
}
