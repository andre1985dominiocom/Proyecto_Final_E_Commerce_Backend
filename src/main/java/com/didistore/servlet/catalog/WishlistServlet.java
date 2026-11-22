
package com.didistore.servlet.catalog;

import com.didistore.controller.catalog.WishlistController;
import com.didistore.model.catalog.Wishlist;
import com.didistore.util.SessionUtil;
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

// Lista de deseos: el usuarioId SIEMPRE se toma de la sesión (evita IDOR).
@WebServlet("/catalog/wishlist")
public class WishlistServlet extends HttpServlet {
    
    private final WishlistController wishlistController = new WishlistController();
    private final Gson gson = new Gson();

    // Manejar solicitudes GET para obtener la lista de deseos
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Integer usuarioId = SessionUtil.obtenerUsuarioId(request);
        if (usuarioId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"status\": \"error\", \"message\": \"Sesión no iniciada\"}");
            out.flush();
            return;
        }

        String productoIdStr = request.getParameter("productoId");

        if (productoIdStr != null) {
            try {
                int productoId = Integer.parseInt(productoIdStr);
                boolean existe = wishlistController.comprobarEstado(usuarioId, productoId);
                out.print("{\"isInWishlist\": " + existe + "}");
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\": \"error\", \"message\": \"productoId inválido\"}");
            }
        } else {
            List<Wishlist> lista = wishlistController.listarPorUsuario(usuarioId);
            out.print(this.gson.toJson(lista));
        }
        out.flush();
    }

    // Manejar solicitudes POST para agregar un producto a la lista de deseos
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Integer usuarioId = SessionUtil.obtenerUsuarioId(request);
        if (usuarioId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"status\": \"error\", \"message\": \"Sesión no iniciada\"}");
            out.flush();
            return;
        }

        try {
            Wishlist wishlist = gson.fromJson(request.getReader(), Wishlist.class);
            if (wishlist == null || wishlist.getproductoId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\": \"error\", \"message\": \"productoId requerido\"}");
                out.flush();
                return;
            }
            // Forzar usuario de sesión (evita IDOR)
            wishlist.setusuarioId(usuarioId);

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

    // Manejar solicitudes DELETE para eliminar un producto de la lista de deseos
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Integer usuarioId = SessionUtil.obtenerUsuarioId(request);
        if (usuarioId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"status\": \"error\", \"message\": \"Sesión no iniciada\"}");
            out.flush();
            return;
        }

        String productoIdStr = request.getParameter("productoId");

        if (productoIdStr != null) {
            try {
                int productoId = Integer.parseInt(productoIdStr);
                boolean eliminado = wishlistController.eliminarProducto(usuarioId, productoId);

                if (eliminado) {
                    out.print("{\"status\": \"success\", \"message\": \"Producto removido de la lista de deseos.\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"status\": \"error\", \"message\": \"No se encontró el registro para eliminar.\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\": \"error\", \"message\": \"productoId inválido\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\": \"error\", \"message\": \"Parámetros insuficientes para la eliminación.\"}");
        }
        out.flush();
    }
}