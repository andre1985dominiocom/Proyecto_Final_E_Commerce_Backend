
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

// Servlet que actúa como intermediario entre el cliente (front-end)
// y el controlador de negocio (back-end) para la gestión de pagos.
@WebServlet("/sales/pagos")
public class PagosServlet extends HttpServlet {
    
    private final PagosController controller = new PagosController();

    // Maneja las solicitudes GET y POST delegando la lógica al controlador correspondiente.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Delega la petición GET al controlador
        controller.procesarConsulta(request, response);
    }

    // Maneja las solicitudes POST y delega la lógica al controlador correspondiente.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Delega la petición POST al controlador
        controller.procesarAccion(request, response);
    }
}