
package com.didistore.controller.admin.servlet;

import com.didistore.controller.admin.AdminProductosController;
import com.didistore.controller.catalog.ProductosController;
import com.didistore.model.catalog.Productos;
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

@WebServlet("/admin/productos")
public class AdminProductosServlet extends HttpServlet {
    
    private final AdminProductosController productoController = new AdminProductosController();
    private final ProductosController catalogProductosController = new ProductosController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("idProducto");
        Map<String, Object> resultado = new HashMap<>();

        if (idParam == null || idParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Falta idProducto");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        try {
            int idProducto = Integer.parseInt(idParam);
            Productos producto = catalogProductosController.consultarProductosPorId(idProducto);

            if (producto != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(producto));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resultado.put("success", false);
                resultado.put("message", "Producto no encontrado");
                response.getWriter().write(gson.toJson(resultado));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "idProducto inválido");
            response.getWriter().write(gson.toJson(resultado));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        Productos producto = gson.fromJson(reader, Productos.class);

        Map<String, Object> resultado = new HashMap<>();

        if (producto == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Body inválido");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        productoController.insertarProductos(producto);

        response.setStatus(HttpServletResponse.SC_CREATED);
        resultado.put("success", true);
        resultado.put("message", "Producto creado correctamente");
        response.getWriter().write(gson.toJson(resultado));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        Productos producto = gson.fromJson(reader, Productos.class);

        Map<String, Object> resultado = new HashMap<>();

        if (producto == null || producto.getidProducto() <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Datos de producto inválidos");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        productoController.actualizarProductos(producto);

        response.setStatus(HttpServletResponse.SC_OK);
        resultado.put("success", true);
        resultado.put("message", "Producto actualizado correctamente");
        response.getWriter().write(gson.toJson(resultado));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> resultado = new HashMap<>();
        String id = request.getParameter("idProducto");

        if (id == null || id.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Falta idProducto");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        try {
            int idProducto = Integer.parseInt(id);
            productoController.eliminarProductos(idProducto);

            response.setStatus(HttpServletResponse.SC_OK);
            resultado.put("success", true);
            resultado.put("message", "Producto eliminado correctamente");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "idProducto inválido");
        }
        response.getWriter().write(gson.toJson(resultado));
    }
}