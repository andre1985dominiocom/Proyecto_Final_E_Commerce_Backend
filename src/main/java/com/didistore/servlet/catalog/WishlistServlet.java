
package com.didistore.servlet.catalog;

import com.didistore.controller.catalog.WishlistController;
import com.didistore.model.catalog.Wishlist;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/catalog/wishlist")
public class WishlistServlet extends HttpServlet {
    
    private final WishlistController wishlistController = new WishlistController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String usuarioIdStr = request.getParameter("usuarioId");
        String productoIdStr = request.getParameter("productoId");

        if (usuarioIdStr != null && productoIdStr != null) {
            
            int usuarioId = Integer.parseInt(usuarioIdStr);
            int productoId = Integer.parseInt(productoIdStr);
            boolean existe = wishlistController.comprobarEstado(usuarioId, productoId);
            
            out.print("{\"isInWishlist\": " + existe + "}");
        } else if (usuarioIdStr != null) {
            
            int usuarioId = Integer.parseInt(usuarioIdStr);
            List<Wishlist> lista = wishlistController.listarPorUsuario(usuarioId);
            
            out.print(this.gson.toJson(lista));
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Faltan parámetros requeridos\"}");
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Wishlist wishlist = gson.fromJson(request.getReader(), Wishlist.class);
            boolean exito = wishlistController.agregarProducto(wishlist);

            if (exito) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"status\": \"success\", \"message\": \"Producto añadido a la lista de deseos.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\": \"error\", \"message\": \"No se pudo añadir (puede que ya exista).\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
        }
        out.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String usuarioIdStr = request.getParameter("usuarioId");
        String productoIdStr = request.getParameter("productoId");

        if (usuarioIdStr != null && productoIdStr != null) {
            int usuarioId = Integer.parseInt(usuarioIdStr);
            int productoId = Integer.parseInt(productoIdStr);
            
            boolean eliminado = wishlistController.eliminarProducto(usuarioId, productoId);

            if (eliminado) {
                out.print("{\"status\": \"success\", \"message\": \"Producto removido de la lista de deseos.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"status\": \"error\", \"message\": \"No se encontró el registro para eliminar.\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\": \"error\", \"message\": \"Parámetros insuficientes para la eliminación.\"}");
        }
        out.flush();
    }   
}