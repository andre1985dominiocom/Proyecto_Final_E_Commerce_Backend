package com.didistore.servlet.sales;

import com.didistore.controller.sales.PagosController;
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
 * Pagos (mock RF-14/RF-03): pagar pedido, listar pagos.
 * Estados de pedido se manejan en /sales/estados-pedido.
 */
@WebServlet("/sales/pagos")
public class PagosServlet extends HttpServlet {

    private final PagosController controller = new PagosController();

    private void jsonHeaders(HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");
    }

    private boolean esStaff(HttpSession session) {
        String rol = session.getAttribute("rol") != null
                ? String.valueOf(session.getAttribute("rol")).trim().toUpperCase() : "";
        return "ADMIN".equals(rol) || "EMPLEADO".equals(rol);
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

        try {
            if ("porPedido".equals(accion) || "listar".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                if (!esStaff && !controller.pedidoPerteneceAUsuario(pedidoId, usuarioId)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\":false,\"message\":\"No autorizado\"}");
                    return;
                }
                List<Map<String, Object>> pagos = controller.listarPagos(pedidoId);
                out.print("{\"success\":true,\"pagos\":"
                        + new com.google.gson.Gson().toJson(pagos) + "}");

            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"Acción GET no válida\"}");
            }
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
        String accion = request.getParameter("accion");
        String ip = request.getRemoteAddr();

        try {
            if ("pagar".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                String metodo = request.getParameter("metodoPago");
                String resultado = request.getParameter("resultado");
                Map<String, Object> res = controller.pagarPedido(
                        pedidoId, usuarioId, metodo, resultado, ip);
                Object st = res.get("status");
                if (st instanceof Integer i) {
                    response.setStatus(i);
                } else if (!Boolean.TRUE.equals(res.get("success"))) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
                Map<String, Object> copy = new java.util.HashMap<>(res);
                copy.remove("status");
                out.print(new com.google.gson.Gson().toJson(copy));

            } else if ("reembolsar".equals(accion)) {
                // Solo staff
                if (!esStaff(session)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"success\":false,\"message\":\"Solo personal autorizado\"}");
                    return;
                }
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                Map<String, Object> res = controller.reembolsarPago(pedidoId, usuarioId, ip);
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
