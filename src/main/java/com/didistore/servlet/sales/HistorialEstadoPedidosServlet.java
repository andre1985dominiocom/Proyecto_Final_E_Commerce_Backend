
package com.didistore.servlet.sales;

import com.didistore.controller.sales.HistorialEstadoPedidosController;
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

@WebServlet("/sales/historial")
public class HistorialEstadoPedidosServlet extends HttpServlet {
    
    // Instancia el controlador puro (POJO) para delegarle el control
    private final HistorialEstadoPedidosController controller = new HistorialEstadoPedidosController();

    /**
     * Intercepta consultas de datos (Líneas de tiempo, estados actuales)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Servlet -> Controller (GET)
        controller.procesarConsulta(request, response);
    }

    /**
     * Intercepta inserciones de datos (Nuevos registros de auditoría manuales)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Servlet -> Controller (POST)
        controller.procesarAccion(request, response);
    }
}