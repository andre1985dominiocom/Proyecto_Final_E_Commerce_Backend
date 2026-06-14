
package com.didistore.servlet.sales;

import com.didistore.controller.sales.PagosController;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/sales/pagos")
public class PagosServlet extends HttpServlet {
    
    private final PagosController controller = new PagosController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Delega la petición GET al controlador
        controller.procesarConsulta(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Delega la petición POST al controlador
        controller.procesarAccion(request, response);
    }
}