
package com.didistore.servlet.sales;

import com.didistore.controller.logistics.EnviosController;
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

// PedidoServlet maneja las operaciones relacionadas con los pedidos de los usuarios,
// incluyendo la visualización del historial de pedidos y el procesamiento del checkout.
@WebServlet("/sales/pedido")
public class PedidoServlet extends HttpServlet {
    
    private final PedidoController pedidoController = new PedidoController();
    private final CarritoController carritoController = new CarritoController(); // Necesario para leer el carrito
    private final EnviosController enviosController = new EnviosController();

    private void configurarCabecerasJSON(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    // GET: solicita el historial de pedidos o el detalle de un pedido específico
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

        String rolSesion = session.getAttribute("rol") != null
                ? String.valueOf(session.getAttribute("rol")).trim().toUpperCase() : "";
        boolean esStaff = "ADMIN".equals(rolSesion) || "EMPLEADO".equals(rolSesion);

        try {
            if ("historial".equals(accion)) {
                // RF-17: filtros opcionales fechaDesde / fechaHasta / estado
                String fechaDesde = request.getParameter("fechaDesde");
                String fechaHasta = request.getParameter("fechaHasta");
                String estado = request.getParameter("estado");
                List<Pedidos> historial = pedidoController.obtenerHistorialUsuario(
                        usuarioId, fechaDesde, fechaHasta, estado);
                String jsonResponse = construirJsonHistorial(historial);
                out.print(jsonResponse);

            } else if ("todos".equals(accion)) {
                // Panel admin: listado completo (solo staff)
                if (!esStaff) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\":false,\"message\":\"Solo personal autorizado\"}");
                    out.flush();
                    return;
                }
                out.print(construirJsonHistorial(pedidoController.listarTodosPedidos()));

            } else if ("detalle".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                Pedidos pedido = pedidoController.obtenerDetallePedido(pedidoId);

                if (pedido == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"succes\":false,\"message\":\"Pedido no encontrado\"}");
                    return;
                }

                // Anti-IDOR: dueño o staff
                if (!esStaff && pedido.getusuarioId() != usuarioId) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\":false,\"message\":\"No autorizado\"}");
                    return;
                }

                List<DetallesPedidos> items = pedidoController.obtenerItemsPedido(pedidoId);
                String jsonResponse = construirJsonDetallePedido(pedido, items, session);
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

    // POST: procesa el checkout del carrito de compras y genera un pedido
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
                String dirParam = request.getParameter("direccionEnvioId");
                int direccionEnvioId = 0;
                if (dirParam != null && !dirParam.trim().isEmpty()) {
                    try { direccionEnvioId = Integer.parseInt(dirParam.trim()); } catch (NumberFormatException ignored) {}
                }
                
                String cuponParam = request.getParameter("cuponId");
                int cuponId = (cuponParam != null && !cuponParam.isEmpty()) ? Integer.parseInt(cuponParam) : 0;

                // RF-09: también se acepta el código del cupón (aplicado en carrito/checkout)
                String cuponCodigo = request.getParameter("cuponCodigo");
                if (cuponCodigo != null && cuponCodigo.isBlank()) {
                    cuponCodigo = null;
                }
                // Fallback: cupón guardado en sesión al aplicarlo desde el carrito
                if (cuponId <= 0 && cuponCodigo == null) {
                    Object cuponSesion = session.getAttribute("cuponCodigo");
                    if (cuponSesion != null) {
                        cuponCodigo = cuponSesion.toString();
                    }
                }

                // Método de envío (Fase 3): Estandar | Express
                String metodoEnvio = request.getParameter("metodoEnvio");
                if (metodoEnvio == null || metodoEnvio.isBlank()) {
                    metodoEnvio = EnviosController.METODO_ESTANDAR;
                }
                metodoEnvio = metodoEnvio.trim();
                if (!EnviosController.METODO_ESTANDAR.equalsIgnoreCase(metodoEnvio)
                        && !EnviosController.METODO_EXPRESS.equalsIgnoreCase(metodoEnvio)) {
                    metodoEnvio = EnviosController.METODO_ESTANDAR;
                }

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

                // 3b. Costo de envío real según método y destino (Fase 3)
                Integer ciudadId = enviosController.ciudadDeDireccion(direccionEnvioId);
                double costoEnvio = enviosController.calcularCostoEnvio(metodoEnvio, subtotalCarrito, ciudadId);

                // 4. Ejecutar la lógica de negocio central (con validación real de cupón RF-09)
                boolean exito = pedidoController.procesarCheckout(
                        usuarioId, direccionEnvioId, subtotalCarrito, costoEnvio, detalles,
                        cuponId, cuponCodigo, metodoEnvio);

                // 5. Si todo salió bien, vaciamos el carrito y limpiamos cupón de sesión
                if (exito) {
                    carritoController.vaciarCarritoCompleto(carrito.getidCarrito());
                    session.removeAttribute("cuponCodigo");
                    session.removeAttribute("cuponId");
                    int pedidoId = 0;
                    try {
                        var creado = pedidoController.buscarUltimoPedidoUsuario(usuarioId);
                        if (creado != null) {
                            pedidoId = creado.getidPedido();
                        }
                    } catch (Exception ignored) {}
                    out.print("{\"success\": true, \"message\": \"¡Pedido procesado con éxito!\", \"pedidoId\": "
                            + pedidoId + "}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"success\": false, \"message\": \"Hubo un problema al guardar el pedido (revisa el cupón o el stock).\"}");
                }
            } else if ("recompra".equals(accion)) {
                // RF-17: recompra rápida — vuelve a agregar items de un pedido al carrito
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                Pedidos pedido = pedidoController.obtenerDetallePedido(pedidoId);

                if (pedido == null || pedido.getusuarioId() != usuarioId) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\":false,\"message\":\"Pedido no autorizado\"}");
                    out.flush();
                    return;
                }

                List<DetallesPedidos> items = pedidoController.obtenerItemsPedido(pedidoId);
                if (items.isEmpty()) {
                    out.print("{\"success\":false,\"message\":\"El pedido no tiene productos\"}");
                    out.flush();
                    return;
                }

                CarritoCompras carrito = carritoController.obtenerOGenerarCarrito(usuarioId, sesionId);
                int agregados = 0;
                for (DetallesPedidos item : items) {
                    boolean ok = carritoController.agregarProductoAlCarrito(
                            carrito.getidCarrito(), usuarioId,
                            item.getproductoId(), item.getcantidad(),
                            item.getprecioUnitario());
                    if (ok) agregados++;
                }

                if (agregados > 0) {
                    out.print("{\"success\":true,\"message\":\"Productos agregados al carrito ("
                            + agregados + ")\",\"agregados\":" + agregados + "}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"success\":false,\"message\":\"No se pudieron agregar productos "
                            + "(revisa el stock)\"}");
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

    // Cabeceras enriquecidas del historial (RF-17).
    private String construirJsonHistorial(List<Pedidos> pedidos) {
        StringBuilder json = new StringBuilder();
        json.append("{\"success\": true, \"pedidos\": [");
        for (int i = 0; i < pedidos.size(); i++) {
            Pedidos p = pedidos.get(i);
            json.append("{");
            json.append("\"id\": ").append(p.getidPedido()).append(",");
            json.append("\"numeroPedido\": \"").append(escapar(p.getnumeroPedido())).append("\",");
            json.append("\"estado\": \"").append(p.getestadoPedido() != null ? p.getestadoPedido().name() : "").append("\",");
            json.append("\"fecha\": \"").append(p.getfechaPedido() != null ? p.getfechaPedido().toString() : "").append("\",");
            json.append("\"subtotal\": ").append(p.getsubTotal()).append(",");
            json.append("\"descuento\": ").append(p.getdescuento()).append(",");
            json.append("\"iva\": ").append(p.getiva()).append(",");
            json.append("\"costoEnvio\": ").append(p.getcostoEnvio()).append(",");
            json.append("\"montoTotal\": ").append(p.getmontoTotal());
            json.append("}");
            if (i < pedidos.size() - 1) {
                json.append(",");
            }
        }
        json.append("]}");
        return json.toString();
    }

    // Detalle con items enriquecidos (nombre producto) + fecha + metodo de pago (RF-17).
    private String construirJsonDetallePedido(Pedidos pedido, List<DetallesPedidos> items,
            HttpSession session) {

        var productosController = new com.didistore.controller.catalog.ProductosController();
        var pagosController = new com.didistore.controller.sales.PagosController();

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"success\":true,");

        String fecha = pedido.getfechaPedido() != null ? pedido.getfechaPedido().toString() : "";
        String metodoPago = "";
        try {
            var pagos = pagosController.listarPagos(pedido.getidPedido());
            if (pagos != null && !pagos.isEmpty()) {
                Object m = pagos.get(0).get("metodoPago");
                metodoPago = m != null ? String.valueOf(m) : "";
            }
        } catch (Exception ignored) {}

        json.append("\"pedido\":{");
        json.append("\"id\":").append(pedido.getidPedido()).append(",");
        json.append("\"numeroPedido\":\"").append(escapar(pedido.getnumeroPedido())).append("\",");
        json.append("\"estado\":\"").append(pedido.getestadoPedido() != null ? pedido.getestadoPedido().name() : "").append("\",");
        json.append("\"fecha\":\"").append(escapar(fecha)).append("\",");
        json.append("\"metodoPago\":\"").append(escapar(metodoPago)).append("\",");
        json.append("\"direccionEnvioId\":").append(pedido.getdireccionEnvioId()).append(",");
        json.append("\"usuarioId\":").append(pedido.getusuarioId()).append(",");
        json.append("\"subtotal\":").append(pedido.getsubTotal()).append(",");
        json.append("\"descuento\":").append(pedido.getdescuento()).append(",");
        json.append("\"iva\":").append(pedido.getiva()).append(",");
        json.append("\"costoEnvio\":").append(pedido.getcostoEnvio()).append(",");
        json.append("\"montoTotal\":").append(pedido.getmontoTotal());
        json.append("},");

        json.append("\"items\":[");
        for (int i = 0; i < items.size(); i++) {
            DetallesPedidos item = items.get(i);
            String nombre = "Producto #" + item.getproductoId();
            try {
                var prod = productosController.consultarProductosPorId(item.getproductoId());
                if (prod != null && prod.getnombreProducto() != null) {
                    nombre = prod.getnombreProducto();
                }
            } catch (Exception ignored) {}

            json.append("{");
            json.append("\"productoId\":").append(item.getproductoId()).append(",");
            json.append("\"nombre\":\"").append(escapar(nombre)).append("\",");
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

    private static String escapar(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
