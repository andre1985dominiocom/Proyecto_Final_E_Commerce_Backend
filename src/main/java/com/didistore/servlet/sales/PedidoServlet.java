
package com.didistore.servlet.sales;

import com.didistore.controller.sales.CarritoController;
import com.didistore.controller.sales.PedidoController;
import com.didistore.model.sales.CarritoCompras;
import com.didistore.model.sales.DetallesPedidos;
import com.didistore.model.sales.ItemCarritos;
import com.didistore.model.sales.Pedidos;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/sales/pedido")
public class PedidoServlet extends HttpServlet {
    
    private final PedidoController pedidoController = new PedidoController();
    private final CarritoController carritoController = new CarritoController(); // Necesario para leer el carrito

    private void configurarCabecerasJSON(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        configurarCabecerasJSON(response);
        PrintWriter out = response.getWriter();
        
        // Validación de seguridad estricta: Solo usuarios logueados tienen historial de pedidos
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"Debes iniciar sesión para ver tus pedidos\"}");
            out.flush();
            return;
        }

        int usuarioId = (Integer) session.getAttribute("usuarioId");
        String accion = request.getParameter("accion");

        try {
            if ("historial".equals(accion)) {
                List<Pedidos> historial = pedidoController.obtenerHistorialUsuario(usuarioId);
                String jsonResponse = construirJsonHistorial(historial);
                out.print(jsonResponse);               
            } else if ("detalle".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                Pedidos pedido = pedidoController.obtenerDetallePedido(pedidoId);
                
                if (pedido == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"succes\":false,\"message\":\"Pedido no encontrado\"}");
                    return;
                }
                
                List<DetallesPedidos> items = pedidoController.obtenerItemsPedido(pedidoId);
                
                String jsonResponse = construirJsonDetallePedido(pedido, items);
                out.print(jsonResponse);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Acción GET no válida\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\": false, \"message\": \"Error interno del servidor\"}");
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            
        configurarCabecerasJSON(response);
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\": false, \"message\": \"Debes iniciar sesión para realizar la compra\"}");
            out.flush();
            return;
        }

        int usuarioId = (Integer) session.getAttribute("usuarioId");
        String sesionId = session.getId();
        String accion = request.getParameter("accion");

        try {
            if ("checkout".equals(accion)) {
                // 1. Extraer datos del formulario de envío
                int direccionEnvioId = Integer.parseInt(request.getParameter("direccionEnvioId"));
                
                String cuponParam = request.getParameter("cuponId");
                int cuponId = (cuponParam != null && !cuponParam.isEmpty()) ? Integer.parseInt(cuponParam) : 0;
                
                double costoEnvio = 15000.0; // En un proyecto real, se calcula según la ciudad

                // 2. Obtener el carrito actual del usuario
                CarritoCompras carrito = carritoController.obtenerOGenerarCarrito(usuarioId, sesionId);
                List<ItemCarritos> itemsCarrito = carritoController.obtenerItemsDelCarrito(carrito.getidCarrito());

                if (itemsCarrito.isEmpty()) {
                    out.print("{\"success\": false, \"message\": \"Tu carrito está vacío\"}");
                    out.flush();
                    return;
                }

                // 3. Transformar los items del carrito a Detalles de Pedido y calcular subtotal
                List<DetallesPedidos> detalles = new ArrayList<>();
                double subtotalCarrito = 0.0;

                for (ItemCarritos item : itemsCarrito) {
                    DetallesPedidos detalle = new DetallesPedidos();
                    detalle.setproductoId(item.getproductoId());
                    detalle.setcantidad(item.getcantidad());
                    detalle.setprecioUnitario(item.getprecioUnitario());
                    
                    // Calculamos el snapshot del subtotal del ítem en el momento exacto de la compra
                    double subtotalItem = item.getcantidad() * item.getprecioUnitario();
                    detalle.setsubtotal(subtotalItem);
                    
                    detalles.add(detalle);
                    subtotalCarrito += subtotalItem;
                }

                // 4. Ejecutar la lógica de negocio central
                boolean exito = pedidoController.procesarCheckout(usuarioId, direccionEnvioId, subtotalCarrito, costoEnvio, detalles, cuponId);

                // 5. Si todo salió bien, vaciamos el carrito
                if (exito) {
                    carritoController.vaciarCarritoCompleto(carrito.getidCarrito());
                    out.print("{\"success\": true, \"message\": \"¡Pedido procesado con éxito!\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"success\": false, \"message\": \"Hubo un problema al guardar el pedido.\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\": false, \"message\": \"Acción POST desconocida\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\": false, \"message\": \"Error al procesar los datos enviados\"}");
        }
        out.flush();
    }

    /**
     * Construye un JSON básico para devolver el historial de pedidos.
     */
    private String construirJsonHistorial(List<Pedidos> pedidos) {
        StringBuilder json = new StringBuilder();
        json.append("{\"success\": true, \"pedidos\": [");
        
        for (int i = 0; i < pedidos.size(); i++) {
            Pedidos p = pedidos.get(i);
            json.append("{");
            json.append("\"id\": ").append(p.getidPedido()).append(",");
            json.append("\"numeroPedido\": \"").append(p.getnumeroPedido()).append("\",");
            json.append("\"estado\": \"").append(p.getestadoPedido().name()).append("\",");
            json.append("\"fecha\": \"").append(p.getfechaPedido() != null ? p.getfechaPedido().toString() : "").append("\",");
            json.append("\"montoTotal\": ").append(p.getmontoTotal());
            json.append("}");
            if (i < pedidos.size() - 1) {
                json.append(",");
            }
        }
        json.append("]}");
        return json.toString();
    }

    private String construirJsonDetallePedido(Pedidos pedido, List<DetallesPedidos> items) {
       
        StringBuilder json = new StringBuilder();

        json.append("{");
        json.append("\"success\":true,");

        json.append("\"pedido\":{");
        json.append("\"id\":").append(pedido.getidPedido()).append(",");
        json.append("\"numeroPedido\":\"").append(pedido.getnumeroPedido()).append("\",");
        json.append("\"estado\":\"").append(pedido.getestadoPedido()).append("\",");
        json.append("\"subtotal\":").append(pedido.getsubTotal()).append(",");
        json.append("\"descuento\":").append(pedido.getdescuento()).append(",");
        json.append("\"iva\":").append(pedido.getiva()).append(",");
        json.append("\"costoEnvio\":").append(pedido.getcostoEnvio()).append(",");
        json.append("\"montoTotal\":").append(pedido.getmontoTotal());
        json.append("},");

        json.append("\"items\":[");

        for (int i = 0; i < items.size(); i++) {

        DetallesPedidos item = items.get(i);

        json.append("{");
        json.append("\"productoId\":").append(item.getproductoId()).append(",");
        json.append("\"cantidad\":").append(item.getcantidad()).append(",");
        json.append("\"precioUnitario\":").append(item.getprecioUnitario()).append(",");
        json.append("\"subtotal\":").append(item.getsubtotal());
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