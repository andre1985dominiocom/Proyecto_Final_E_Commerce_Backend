package com.didistore.servlet.sales;

import com.didistore.controller.sales.EstadoPedidoController;
import com.didistore.controller.sales.HistorialEstadoPedidosController;
import com.didistore.controller.sales.PagosController;
import com.didistore.controller.sales.PedidoController;
import com.didistore.model.sales.enums.EstadoPedidos;
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
 * Endpoints de estados de pedido (RF-12): pagar (vía mock), cancelar,
 * cambiar estado (staff) y consultar historial.
 */
@WebServlet("/sales/estados-pedido")
public class EstadosPedidoServlet extends HttpServlet {

    private final PedidoController pedidoController = new PedidoController();
    private final EstadoPedidoController estadoController = new EstadoPedidoController();
    private final PagosController pagosController = new PagosController();
    private final HistorialEstadoPedidosController historialController =
            new HistorialEstadoPedidosController();

    private void jsonHeaders(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        jsonHeaders(response);
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\":false,\"message\":\"Debes iniciar sesión\"}");
            return;
        }

        int usuarioId = (Integer) session.getAttribute("usuarioId");
        boolean esStaff = esStaff(session);
        String accion = request.getParameter("accion");
        String ip = request.getRemoteAddr();

        try {
            if ("historial".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                if (!puedeVerPedido(pedidoId, usuarioId, esStaff)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\":false,\"message\":\"No autorizado\"}");
                    return;
                }
                List<Map<String, Object>> lista = historialController.listarPorPedido(pedidoId);
                out.print("{\"success\":true,\"historial\":" + toJson(lista) + "}");

            } else if ("actual".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                if (!puedeVerPedido(pedidoId, usuarioId, esStaff)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\":false,\"message\":\"No autorizado\"}");
                    return;
                }
                Map<String, Object> ultimo = historialController.obtenerUltimo(pedidoId);
                out.print(toJson(ultimo));

            } else if ("transiciones".equals(accion)) {
                String estado = request.getParameter("estado");
                EstadoPedidos actual = EstadoPedidos.valueOf(estado);
                java.util.Set<EstadoPedidos> sig =
                        com.didistore.util.EstadoPedidoMaquina.siguientesPermitidos(actual);
                StringBuilder sb = new StringBuilder("{\"success\":true,\"siguientes\":[");
                boolean first = true;
                for (EstadoPedidos e : sig) {
                    if (!first) sb.append(",");
                    sb.append("\"").append(e.name()).append("\"");
                    first = false;
                }
                sb.append("]}");
                out.print(sb.toString());

            } else if ("pagos".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                if (!puedeVerPedido(pedidoId, usuarioId, esStaff)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\":false,\"message\":\"No autorizado\"}");
                    return;
                }
                out.print("{\"success\":true,\"pagos\":" + toJson(pagosController.listarPagos(pedidoId)) + "}");

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"Acción GET no válida\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Parámetros inválidos\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"Error interno\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        jsonHeaders(response);
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\":false,\"message\":\"Debes iniciar sesión\"}");
            return;
        }

        int usuarioId = (Integer) session.getAttribute("usuarioId");
        boolean esStaff = esStaff(session);
        String accion = request.getParameter("accion");
        String ip = request.getRemoteAddr();

        try {
            if ("pagar".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                String metodo = request.getParameter("metodoPago");
                String resultado = request.getParameter("resultado"); // mock: Aprobado|Rechazado
                Map<String, Object> res = pagosController.pagarPedido(
                        pedidoId, usuarioId, metodo, resultado, ip);
                response.setStatus(respStatus(res));
                out.print(toJson(stripStatus(res)));

            } else if ("cancelar".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                // Cliente solo cancela lo suyo; staff cualquier pedido
                if (!esStaff && !pedidoController.pedidoPerteneceAUsuario(pedidoId, usuarioId)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\":false,\"message\":\"No autorizado\"}");
                    return;
                }
                // Si estaba pagado, reembolsar (staff) o rechazar cancelación de cliente
                var pedido = pedidoController.obtenerDetallePedido(pedidoId);
                if (pedido != null && pedido.getestadoPedido() != EstadoPedidos.Pendiente_Pago
                        && !esStaff) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.print("{\"success\":false,\"message\":"
                            + "\"Solo puedes cancelar pedidos aún no pagados. "
                            + "Contacta soporte para pedidos pagados.\"}");
                    return;
                }
                if (pedido != null && pedido.getestadoPedido() == EstadoPedidos.Pagado && esStaff) {
                    pagosController.reembolsarPago(pedidoId, usuarioId, ip);
                }
                Map<String, Object> res = estadoController.transicionar(
                        pedidoId, EstadoPedidos.Cancelado, usuarioId, esStaff,
                        esStaff ? "Cancelado por personal" : "Cancelado por cliente", ip);
                response.setStatus(respStatus(res));
                out.print(toJson(stripStatus(res)));

            } else if ("cambiar".equals(accion)) {
                // Solo staff (ADMIN/EMPLEADO)
                if (!esStaff) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\":false,\"message\":\"Solo personal autorizado\"}");
                    return;
                }
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                EstadoPedidos nuevo = EstadoPedidos.valueOf(request.getParameter("estadoNuevo"));
                String notas = request.getParameter("notas");
                Map<String, Object> res = estadoController.transicionar(
                        pedidoId, nuevo, usuarioId, true, notas, ip);

                // Al despachar/enviar también se refleja en Envios si existe
                if (Boolean.TRUE.equals(res.get("success"))) {
                    reflejarEnEnvioSiAplica(pedidoId, nuevo, usuarioId, ip);
                }

                response.setStatus(respStatus(res));
                out.print(toJson(stripStatus(res)));

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"Acción POST no válida\"}");
            }
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Datos inválidos: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"success\":false,\"message\":\"Error interno\"}");
        }
    }

    private void reflejarEnEnvioSiAplica(int pedidoId, EstadoPedidos nuevo, Integer staffId, String ip) {
        try {
            if (nuevo != EstadoPedidos.Despachado && nuevo != EstadoPedidos.En_Transito
                    && nuevo != EstadoPedidos.Entregado && nuevo != EstadoPedidos.Cancelado) {
                return;
            }
            var enviosController = new com.didistore.controller.logistics.EnviosController();
            var envio = enviosController.buscarPorPedido(pedidoId);
            if (envio == null) {
                return;
            }
            String estadoEnvio = switch (nuevo) {
                case Despachado -> "Despachado";
                case En_Transito -> "En_Transito";
                case Entregado -> "Entregado";
                case Cancelado -> "Cancelado";
                default -> null;
            };
            if (estadoEnvio != null) {
                enviosController.actualizarEstadoEnvio(envio.getidEnvio(), estadoEnvio, staffId, ip);
            }
        } catch (Exception e) {
            // best-effort: el pedido ya cambió; el envío puede no existir aún
        }
    }

    private boolean esStaff(HttpSession session) {
        String rol = session.getAttribute("rol") != null
                ? String.valueOf(session.getAttribute("rol")).trim().toUpperCase() : "";
        return "ADMIN".equals(rol) || "EMPLEADO".equals(rol);
    }

    private boolean puedeVerPedido(int pedidoId, int usuarioId, boolean esStaff) {
        if (esStaff) {
            return true;
        }
        return pedidoController.pedidoPerteneceAUsuario(pedidoId, usuarioId);
    }

    private int respStatus(Map<String, Object> res) {
        Object st = res.get("status");
        if (st instanceof Integer i) {
            return i;
        }
        return Boolean.TRUE.equals(res.get("success"))
                ? HttpServletResponse.SC_OK
                : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }

    private Map<String, Object> stripStatus(Map<String, Object> res) {
        Map<String, Object> copy = new java.util.HashMap<>(res);
        copy.remove("status");
        return copy;
    }

    private String toJson(Object obj) {
        return new com.google.gson.Gson().toJson(obj);
    }
}
