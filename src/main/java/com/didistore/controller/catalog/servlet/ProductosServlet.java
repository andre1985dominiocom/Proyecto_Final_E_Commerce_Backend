
package com.didistore.controller.catalog.servlet;

import com.didistore.controller.catalog.ProductosController;
import com.didistore.model.catalog.Productos;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

//import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/catalog/productos")
public class ProductosServlet extends HttpServlet {
    
    private final ProductosController productoController = new ProductosController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idProductoParam = request.getParameter("idProducto");
        String idCategoriaParam = request.getParameter("idCategoria");
        String nombreProductoParam = request.getParameter("nombreProducto");

        if (idProductoParam != null && !idProductoParam.trim().isEmpty()) {
            try {
                int idProducto = Integer.parseInt(idProductoParam);
                Productos producto = productoController.consultarProductosPorId(idProducto);

                if (producto != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(gson.toJson(producto));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"message\":\"Producto no encontrado\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"idProducto inválido\"}");
            }
            return;
        }

        if (idCategoriaParam != null && !idCategoriaParam.trim().isEmpty()) {
            try {
                int idCategoria = Integer.parseInt(idCategoriaParam);
                List<Productos> lista = productoController.listarProductoPorCategoria(idCategoria);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(lista));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"idCategoria inválido\"}");
            }
            return;
        }

        if (nombreProductoParam != null && !nombreProductoParam.trim().isEmpty()) {
            Productos producto = productoController.buscarProductosPorNombre(nombreProductoParam);

            if (producto != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(producto));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\":\"Producto no encontrado\"}");
            }
            return;
        }

        List<Productos> lista = productoController.listarProductos();

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(lista));
    }
}