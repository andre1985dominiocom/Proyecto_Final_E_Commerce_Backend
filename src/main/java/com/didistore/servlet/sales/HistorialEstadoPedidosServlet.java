package com.didistore.servlet.sales;

import com.didistore.controller.sales.HistorialEstadoPedidosController;
import com.didistore.controller.sales.PedidoController;
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
 * Historial de estados de pedidos (RF-12). Registro de transiciones
 * se hace vía /sales/estados-pedido.
 */
@WebServlet("/sales/historial")
public class HistorialEstadoPedidosServlet extends HttpServlet {

    private final HistorialEstadoPedidosController controller =
            new HistorialEstadoPedidosController();
    private final PedidoController pedidoController = new PedidoController();

    private boolean esStaff(HttpSession session) {
        String rol = session.getAttribute("rol") != null
                ? String.valueOf(session.getAttribute("rol")).trim().toUpperCase() : "";
        return "ADMIN".equals(rol) || "EMPLEADO".equals(rol);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
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

        try {
            if ("listarPorPedido".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                if (!esStaff && !pedidoController.pedidoPerteneceAUsuario(pedidoId, usuarioId)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\":false,\"message\":\"No autorizado\"}");
                    return;
                }
                List<Map<String, Object>> lista = controller.listarPorPedido(pedidoId);
                out.print("{\"success\":true,\"totalRegistros\":" + lista.size()
                        + ",\"historial\":" + new com.google.gson.Gson().toJson(lista) + "}");

            } else if ("obtenerUltimo".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                if (!esStaff && !pedidoController.pedidoPerteneceAUsuario(pedidoId, usuarioId)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\":false,\"message\":\"No autorizado\"}");
                    return;
                }
                Map<String, Object> ultimo = controller.obtenerUltimo(pedidoId);
                if (!Boolean.TRUE.equals(ultimo.get("success"))) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
                out.print(new com.google.gson.Gson().toJson(ultimo));

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"Acción GET no válida\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Error al procesar la consulta\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Registro manual de historial: solo staff
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"success\":false,\"message\":\"Debes iniciar sesión\"}");
            return;
        }
        if (!esStaff(session)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("{\"success\":false,\"message\":\"Solo personal autorizado\"}");
            return;
        }

        int usuarioId = (Integer) session.getAttribute("usuarioId");
        String accion = request.getParameter("accion");

        try {
            if ("registrarCambio".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                String estAnt = request.getParameter("estadoAnterior");
                String estNue = request.getParameter("estadoNuevo");
                String notas = request.getParameter("notas");

                Map<String, Object> res = controller.registrarCambioManual(
                        pedidoId,
                        usuarioId,
                        estAnt != null && !estAnt.isEmpty()
                                ? com.didistore.model.sales.enums.EstadoPedidos.valueOf(estAnt)
                                : null,
                        com.didistore.model.sales.enums.EstadoPedidos.valueOf(estNue),
                        notas,
                        request.getRemoteAddr());
                if (!Boolean.TRUE.equals(res.get("success"))) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
                out.print(new com.google.gson.Gson().toJson(res));
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"Acción POST no válida\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"Datos inválidos\"}");
        }
    }
}
