
package com.didistore.controller.admin.servlet;

import com.didistore.controller.admin.AdminCategoriasController;
import com.didistore.model.catalog.Categorias;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/admin/categorias")
public class AdminCategoriasServlet extends HttpServlet{
    
    private final AdminCategoriasController categoriaController = new AdminCategoriasController();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        Categorias categoria = gson.fromJson(reader, Categorias.class);

        Map<String, Object> resultado = new HashMap<>();

        if (categoria == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Body inválido");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        categoriaController.insertarCategorias(categoria);

        response.setStatus(HttpServletResponse.SC_CREATED);
        resultado.put("success", true);
        resultado.put("message", "Categoria creada correctamente");
        response.getWriter().write(gson.toJson(resultado));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        Categorias categoria = gson.fromJson(reader, Categorias.class);

        Map<String, Object> resultado = new HashMap<>();

        if (categoria == null || categoria.getidCategoria() <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Datos de categoria inválidos");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        categoriaController.actualizarCategorias(categoria);

        response.setStatus(HttpServletResponse.SC_OK);
        resultado.put("success", true);
        resultado.put("message", "Categoria actualizada correctamente");
        response.getWriter().write(gson.toJson(resultado));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> resultado = new HashMap<>();
        String id = request.getParameter("idCategoria");

        if (id == null || id.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Falta idCategoria");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        try {
            int idCategoria = Integer.parseInt(id);
            categoriaController.eliminarCategorias(idCategoria);

            response.setStatus(HttpServletResponse.SC_OK);
            resultado.put("success", true);
            resultado.put("message", "Categoria eliminada correctamente");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "idCategoria inválido");
        }
        response.getWriter().write(gson.toJson(resultado));
    }
}