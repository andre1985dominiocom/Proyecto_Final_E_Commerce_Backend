
package com.didistore.servlet.sales;

import com.didistore.controller.sales.CarritoController;
import com.didistore.model.sales.CarritoCompras;
import com.didistore.model.sales.ItemCarritos;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Servlet para manejar las operaciones del carrito de compras
@WebServlet("/sales/carrito")
public class CarritoComprasServlet extends HttpServlet {
    
    private final CarritoController carritoController = new CarritoController();
    private final Gson gson = new Gson();

    // Configuración de cabeceras comunes para responder JSON
    private void configurarCabecerasJSON(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    // Manejo de solicitudes GET para ver el carrito
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

                // Incluir cupón aplicado en sesión (RF-09) para el panel de resumen
                String cuponCodigo = session.getAttribute("cuponCodigo") != null
                        ? session.getAttribute("cuponCodigo").toString() : null;
                double descuento = 0.0;
                if (cuponCodigo != null) {
                    com.didistore.controller.marketing.CuponesController cuponesController =
                            new com.didistore.controller.marketing.CuponesController();
                    java.util.Map<String, Object> validacion =
                            cuponesController.validarYCalcular(cuponCodigo, total);
                    if (Boolean.TRUE.equals(validacion.get("success"))) {
                        descuento = (Double) validacion.get("descuento");
                    } else {
                        session.removeAttribute("cuponCodigo");
                        session.removeAttribute("cuponId");
                        cuponCodigo = null;
                    }
                }

                String jsonResponse = construirJsonCarrito(items, total, cuponCodigo, descuento);
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

    // Manejo de solicitudes POST para modificar el carrito
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

                boolean exito = carritoController.agregarProductoAlCarrito(carrito.getidCarrito(), usuarioId, productoId, cantidad, precioUnitario);
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
                session.removeAttribute("cuponCodigo");
                session.removeAttribute("cuponId");
                out.print("{\"success\": " + exito + ", \"message\": \"Carrito vaciado\"}");

            } else if ("aplicar-cupon".equals(accion)) {
                // RF-09: validar cupón y guardarlo en sesión para el checkout
                String codigo = request.getParameter("codigo");
                String subtotalParam = request.getParameter("subtotal");
                double subtotal = 0.0;
                try {
                    if (subtotalParam != null && !subtotalParam.isBlank()) {
                        subtotal = Double.parseDouble(subtotalParam);
                    } else {
                        subtotal = carritoController.calcularTotalCarrito(carrito.getidCarrito());
                    }
                } catch (NumberFormatException ignored) {}

                com.didistore.controller.marketing.CuponesController cuponesController =
                        new com.didistore.controller.marketing.CuponesController();
                java.util.Map<String, Object> validacion =
                        cuponesController.validarYCalcular(codigo, subtotal);

                if (Boolean.TRUE.equals(validacion.get("success"))) {
                    session.setAttribute("cuponCodigo", codigo.trim().toUpperCase());
                    Object cupon = validacion.get("cupon");
                    if (cupon instanceof com.didistore.model.marketing.Cupones c) {
                        session.setAttribute("cuponId", c.getidCupon());
                    }
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    session.removeAttribute("cuponCodigo");
                    session.removeAttribute("cuponId");
                }
                out.print(gson.toJson(validacion));

            } else if ("quitar-cupon".equals(accion)) {
                session.removeAttribute("cuponCodigo");
                session.removeAttribute("cuponId");
                out.print("{\"success\": true, \"message\": \"Cupón retirado\"}");

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
     * Construye la respuesta JSON del carrito usando Gson para un encoding seguro.
     * Incluye cupón aplicado y descuento (RF-09) para el resumen del panel.
     */
    private String construirJsonCarrito(List<ItemCarritos> items, double total,
            String cuponCodigo, double descuento) {
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("total", total);
        resultado.put("items", items);
        resultado.put("cuponCodigo", cuponCodigo);
        resultado.put("descuento", descuento);
        resultado.put("totalConDescuento", Math.max(0, total - descuento));
        return gson.toJson(resultado);
    }
}
