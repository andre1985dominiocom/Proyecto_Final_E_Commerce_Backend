
package com.didistore.servlet.catalog;

import com.didistore.controller.catalog.ResenasController;
import com.didistore.model.catalog.Resenas;
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
 * @author Sergio Sergio Andrés Álvarez Lache
 */

@WebServlet("/catalog/resena")
public class ResenasServlet extends HttpServlet  {
    
    private final ResenasController resenasController = new ResenasController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String productoIdStr = request.getParameter("productoId");

        if (productoIdStr != null) {
            int productoId = Integer.parseInt(productoIdStr);
            
            List<Resenas> lista = resenasController.consultarResenasPorProducto(productoId);
            
            out.print(this.gson.toJson(lista));
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Falta el parámetro productoId\"}");
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
            Resenas nuevaResena = gson.fromJson(request.getReader(), Resenas.class);
            
            boolean exito = resenasController.registrarResena(nuevaResena);

            if (exito) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"status\": \"success\", \"message\": \"¡Reseña agregada con éxito!\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\": \"error\", \"message\": \"No se pudo registrar la reseña. Verifique los datos.\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\": \"error\", \"message\": \"" + e.getMessage() + "\"}");
        }
        out.flush();
    }
}