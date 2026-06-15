
package com.didistore.controller.admin;

import com.didistore.dao.impl.auth.UsuariosDAOImpl;
import com.didistore.dao.impl.catalog.ProductosDAOImpl;
import com.didistore.dao.impl.sales.PedidosDAOImpl;
import com.didistore.dao.interfaces.auth.IUsuariosDAO;
import com.didistore.dao.interfaces.catalog.IProductosDAO;
import com.didistore.dao.interfaces.sales.IPedidosDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class AdminDashboardController {
    
    private final IPedidosDAO pedidosDAO = new PedidosDAOImpl();
    private final IUsuariosDAO usuariosDAO = new UsuariosDAOImpl();
    private final IProductosDAO productosDAO = new ProductosDAOImpl();
    
    public void obtenerMetricas(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();

            // Aquí llamarías a los métodos de tus DAOs para traer datos reales
            double ventasMes = 0;  pedidosDAO.calcularVentasMesActual();
            int pedidosNuevos = 0; pedidosDAO.contarPedidosNuevos();
            int totalUsuarios = 0; usuariosDAO.listarUsuarios();
            int productosActivos = 0; productosDAO.listarProductos();

            // Construcción manual de respuesta JSON estándar
            String json = String.format(
                "{\"status\":\"success\", \"ventasMes\":%.2f, \"pedidosNuevos\":%d, \"totalUsuarios\":%d, \"productosActivos\":%d}",
                ventasMes, pedidosNuevos, totalUsuarios, productosActivos
            );
            out.print(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}