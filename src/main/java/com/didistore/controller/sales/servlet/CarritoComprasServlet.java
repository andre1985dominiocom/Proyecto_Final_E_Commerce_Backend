
package com.didistore.controller.sales.servlet;

import com.didistore.controller.sales.CarritoController;
import com.didistore.model.sales.CarritoCompras;
import com.didistore.model.sales.ItemCarritos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/sales/carrito")
public class CarritoComprasServlet extends HttpServlet {
    
    private final CarritoController carritoController = new CarritoController();

    // Configuración de cabeceras comunes para responder JSON
    private void configurarCabecerasJSON(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        configurarCabecerasJSON(response);
        PrintWriter out = response.getWriter();
        
        // Identificación del usuario en la sesión
        HttpSession session = request.getSession(true);
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        String sesionId = session.getId();

        CarritoCompras carrito = carritoController.obtenerOGenerarCarrito(usuarioId, sesionId);
        String accion = request.getParameter("accion");
        if (accion == null) accion = "ver";

        try {
            if ("ver".equals(accion)) {
                List<ItemCarritos> items = carritoController.obtenerItemsDelCarrito(carrito.getidCarrito());
                double total = carritoController.calcularTotalCarrito(carrito.getidCarrito());

                // Construimos una respuesta JSON estructurada manualmente (o usa Gson/Jackson)
                String jsonResponse = construirJsonCarrito(items, total);
                out.print(jsonResponse);
            } else {
                out.print("{\"success\": false, \"message\": \"Acción GET no válida\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Error interno en el servidor\"}");
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        configurarCabecerasJSON(response);
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(true);
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");
        String sesionId = session.getId();

        CarritoCompras carrito = carritoController.obtenerOGenerarCarrito(usuarioId, sesionId);
        String accion = request.getParameter("accion");

        try {
            if ("agregar".equals(accion)) {
                int productoId = Integer.parseInt(request.getParameter("productoId"));
                int cantidad = Integer.parseInt(request.getParameter("cantidad"));
                double precioUnitario = Double.parseDouble(request.getParameter("precioUnitario"));

                boolean exito = carritoController.agregarProductoAlCarrito(carrito.getidCarrito(), productoId, cantidad, precioUnitario);
                out.print("{\"success\": " + exito + ", \"message\": \"Producto procesado\"}");

            } else if ("actualizar".equals(accion)) {
                int itemId = Integer.parseInt(request.getParameter("idItem"));
                int nuevaCantidad = Integer.parseInt(request.getParameter("cantidad"));

                boolean exito = carritoController.modificarCantidadItem(itemId, nuevaCantidad);
                out.print("{\"success\": " + exito + ", \"message\": \"Cantidad modificada\"}");

            } else if ("eliminar".equals(accion)) {
                int itemId = Integer.parseInt(request.getParameter("idItem"));
                boolean exito = carritoController.eliminarItemDelCarrito(itemId);
                out.print("{\"success\": " + exito + ", \"message\": \"Item eliminado\"}");

            } else if ("vaciar".equals(accion)) {
                boolean exito = carritoController.vaciarCarritoCompleto(carrito.getidCarrito());
                out.print("{\"success\": " + exito + ", \"message\": \"Carrito vaciado\"}");

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Acción POST desconocida\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Error en el formato de los datos enviados\"}");
        }
        out.flush();
    }

    /**
     * Generador manual de JSON para no depender de librerías externas.
     * Convierte la lista de items y el total en una cadena JSON válida.
     */
    private String construirJsonCarrito(List<ItemCarritos> items, double total) {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"total\": ").append(total).append(",");
        json.append("\"items\": [");
        
        for (int i = 0; i < items.size(); i++) {
            ItemCarritos item = items.get(i);
            json.append("{");
            json.append("\"itemId\": ").append(item.getidItem()).append(",");
            json.append("\"productoId\": ").append((char) item.getproductoId()).append(",");
            json.append("\"cantidad\": ").append(item.getcantidad()).append(",");
            json.append("\"precioUnitario\": ").append(item.getprecioUnitario());
            json.append("}");
            if (i < items.size() - 1) {
                json.append(",");
            }
        }        
        json.append("]");
        json.append("}");
        return json.toString();
    }   
}