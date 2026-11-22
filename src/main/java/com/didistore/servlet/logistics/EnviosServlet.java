
package com.didistore.servlet.logistics;

import com.didistore.controller.logistics.EnviosController;
import com.didistore.controller.sales.PedidoController;
import com.didistore.model.logistics.Envios;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Servlet de Envíos (Fase 3):
// - GET opciones/costo: cotización de envío Estandar/Express según destino
// - GET rastreo: seguimiento del envío de un pedido (propietario o staff)
// - POST despacho/estado: registro de guía y cambios de estado (ADMIN/EMPLEADO)
@WebServlet("/logistics/envio")
public class EnviosServlet extends HttpServlet {

    private final EnviosController enviosController = new EnviosController();
    private final PedidoController pedidoController = new PedidoController();
    private final Gson gson = new Gson();

    private void jsonHeaders(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    private boolean esStaff(HttpSession session) {
        if (session == null) return false;
        String rol = String.valueOf(session.getAttribute("rol")).trim().toUpperCase();
        return "ADMIN".equals(rol) || "EMPLEADO".equals(rol);
    }

    // GET: opciones de envío, costo o rastreo
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        jsonHeaders(response);
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        Integer usuarioId = (session != null) ? (Integer) session.getAttribute("usuarioId") : null;
        String accion = request.getParameter("accion");

        try {
            if ("opciones".equals(accion) || accion == null) {
                // Cotización pública (carrito) o con sesión (checkout)
                double subtotal = 0.0;
                try {
                    subtotal = Double.parseDouble(request.getParameter("subtotal"));
                } catch (Exception ignored) {}

                Integer ciudadId = null;
                String ciudadParam = request.getParameter("ciudadId");
                if (ciudadParam != null && !ciudadParam.isBlank()) {
                    try {
                        ciudadId = Integer.parseInt(ciudadParam.trim());
                    } catch (NumberFormatException ignored) {}
                }

                String direccionParam = request.getParameter("direccionId");
                if (ciudadId == null && direccionParam != null && !direccionParam.isBlank() && usuarioId != null) {
                    try {
                        ciudadId = enviosController.ciudadDeDireccion(Integer.parseInt(direccionParam.trim()));
                    } catch (NumberFormatException ignored) {}
                }

                List<Map<String, Object>> opciones = enviosController.listarOpcionesEnvio(subtotal, ciudadId);
                out.print("{\"success\":true,\"opciones\":" + gson.toJson(opciones) + "}");

            } else if ("rastreo".equals(accion)) {
                if (usuarioId == null) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.print("{\"success\":false,\"message\":\"Debes iniciar sesión\"}");
                    return;
                }
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                boolean staff = esStaff(session);

                if (!staff && !pedidoController.pedidoPerteneceAUsuario(pedidoId, usuarioId)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\":false,\"message\":\"No tienes acceso a este pedido\"}");
                    return;
                }

                Envios envio = enviosController.buscarPorPedido(pedidoId);
                if (envio == null) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"success\":false,\"message\":\"Envío no disponible aún. "
                            + "El número de rastreo estará disponible cuando el pedido sea despachado.\"}");
                    return;
                }
                out.print("{\"success\":true,\"envio\":" + gson.toJson(envio) + "}");

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"Acción no válida\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"Error interno del servidor\"}");
        }
        out.flush();
    }

    // POST: despacho (guía) o actualización de estado (solo staff)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        jsonHeaders(response);
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        Integer usuarioId = (session != null) ? (Integer) session.getAttribute("usuarioId") : null;

        if (usuarioId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\":false,\"message\":\"Sesión no iniciada\"}");
            return;
        }
        if (!esStaff(session)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"success\":false,\"message\":\"Solo administradores o empleados pueden gestionar envíos\"}");
            return;
        }

        String action = request.getParameter("action");
        String ip = request.getRemoteAddr();

        try {
            boolean resultado = false;
            switch (action != null ? action : "") {
                case "despacho": {
                    int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                    String transportadora = request.getParameter("transportadora");
                    String numeroGuia = request.getParameter("numeroGuia");
                    if (numeroGuia == null || numeroGuia.isBlank()) {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.print("{\"success\":false,\"message\":\"Número de guía requerido\"}");
                        return;
                    }
                    resultado = enviosController.registrarDespacho(pedidoId, transportadora,
                            numeroGuia.trim(), usuarioId, ip);
                    break;
                }
                case "estado": {
                    int idEnvio = Integer.parseInt(request.getParameter("idEnvio"));
                    String estado = request.getParameter("estado");
                    resultado = enviosController.actualizarEstadoEnvio(idEnvio, estado, usuarioId, ip);
                    break;
                }
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"success\":false,\"message\":\"Acción no reconocida\"}");
                    return;
            }

            response.setStatus(resultado ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            if (resultado) {
                out.print("{\"success\":true}");
            } else {
                out.print("{\"success\":false,\"message\":\"No se pudo actualizar el envío\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Error al procesar los datos\"}");
        }
        out.flush();
    }
}
