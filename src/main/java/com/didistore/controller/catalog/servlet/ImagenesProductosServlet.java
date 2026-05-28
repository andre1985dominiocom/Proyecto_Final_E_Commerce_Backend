
package com.didistore.controller.catalog.servlet;

import com.didistore.controller.catalog.ImagenesProductosController;
import com.didistore.model.catalog.ImagenesProductos;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/catalog/imagenes-productos")
public class ImagenesProductosServlet extends HttpServlet {
    
    private final ImagenesProductosController imagenesController = new ImagenesProductosController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idImagenParam = request.getParameter("idImagen");
        String productoIdParam = request.getParameter("productoId");

        if (idImagenParam != null && !idImagenParam.trim().isEmpty()) {
            try {
                int idImagen = Integer.parseInt(idImagenParam);
                ImagenesProductos imagen = imagenesController.consultarImagenPorId(idImagen);

                if (imagen != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(gson.toJson(imagen));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"message\":\"Imagen no encontrada\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"idImagen inválido\"}");
            }
            return;
        }

        if (productoIdParam != null && !productoIdParam.trim().isEmpty()) {
            try {
                int productoId = Integer.parseInt(productoIdParam);
                List<ImagenesProductos> lista = imagenesController.listarPorProducto(productoId);

                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(gson.toJson(lista));
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"message\":\"productoId inválido\"}");
            }
            return;
        }

        List<ImagenesProductos> lista = imagenesController.listarImagenes();
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(lista));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        ImagenesProductos imagen = gson.fromJson(reader, ImagenesProductos.class);

        Map<String, Object> resultado = new HashMap<>();

        boolean insertado = imagenesController.insertarImagen(imagen);

        if (insertado) {
            response.setStatus(HttpServletResponse.SC_CREATED);
            resultado.put("success", true);
            resultado.put("message", "Imagen insertada correctamente");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "No se pudo insertar la imagen");
        }

        response.getWriter().write(gson.toJson(resultado));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BufferedReader reader = request.getReader();
        ImagenesProductos imagen = gson.fromJson(reader, ImagenesProductos.class);

        Map<String, Object> resultado = new HashMap<>();

        boolean actualizado = imagenesController.actualizarImagen(imagen);

        if (actualizado) {
            response.setStatus(HttpServletResponse.SC_OK);
            resultado.put("success", true);
            resultado.put("message", "Imagen actualizada correctamente");
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "No se pudo actualizar la imagen");
        }

        response.getWriter().write(gson.toJson(resultado));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> resultado = new HashMap<>();
        String idImagenParam = request.getParameter("idImagen");

        if (idImagenParam == null || idImagenParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "Falta idImagen");
            response.getWriter().write(gson.toJson(resultado));
            return;
        }

        try {
            int idImagen = Integer.parseInt(idImagenParam);
            boolean eliminado = imagenesController.eliminarImagen(idImagen);

            if (eliminado) {
                response.setStatus(HttpServletResponse.SC_OK);
                resultado.put("success", true);
                resultado.put("message", "Imagen eliminada correctamente");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resultado.put("success", false);
                resultado.put("message", "No se pudo eliminar la imagen");
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "idImagen inválido");
        }

        response.getWriter().write(gson.toJson(resultado));
    } 
}