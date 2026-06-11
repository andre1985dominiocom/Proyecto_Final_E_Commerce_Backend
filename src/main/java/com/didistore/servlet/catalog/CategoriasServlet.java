
package com.didistore.servlet.catalog;

import com.didistore.controller.catalog.CategoriasController;
import com.didistore.model.catalog.Categorias;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/catalog/categorias")
public class CategoriasServlet extends HttpServlet {
    
    private final CategoriasController categoriaController = new CategoriasController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idCategoriaParam = request.getParameter("idCategoria");
        String nombreCategoriaParam = request.getParameter("nombreCategoria");
        String categoriaPadreIdParam = request.getParameter("categoriaPadreId");

        if (idCategoriaParam != null && !idCategoriaParam.trim().isEmpty()) {
            try {
                int idCategoria = Integer.parseInt(idCategoriaParam);
                Categorias categoria = categoriaController.obtenerCategoriaPorId(idCategoria);

                if (categoria != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(gson.toJson(categoria));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"message\":\"Categoria no encontrado\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"idCategoria inválido\"}");
            }
            return;
        }

        if (categoriaPadreIdParam != null && !categoriaPadreIdParam.trim().isEmpty()) {
            try {
                int categoriaPadreId = Integer.parseInt(categoriaPadreIdParam);
                List<Categorias> lista = categoriaController.obtenerSubcategorias(categoriaPadreId);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(lista));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"categoriaPadreId inválido\"}");
            }
            return;
        }

        if (nombreCategoriaParam != null && !nombreCategoriaParam.trim().isEmpty()) {
            Categorias categoria = categoriaController.buscarCategoriaPorNombre(nombreCategoriaParam);

            if (categoria != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(categoria));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\":\"Categoria no encontrada\"}");
            }
            return;
        }

        List<Categorias> lista = categoriaController.listarCategorias();

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(lista));
    }
    
}