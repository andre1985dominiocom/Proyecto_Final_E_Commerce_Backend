package com.didistore.controller.sales.servlet;

import com.didistore.controller.sales.PedidoController;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/sales/pedidos")
public class PedidosServlet extends HttpServlet {

    private final PedidoController pedidoController = new PedidoController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idPedidoParam = request.getParameter("idPedido");
        if (idPedidoParam != null && !idPedidoParam.isBlank()) {
            Map<String, Object> pedido = obtenerPedido(idPedidoParam);
            if (pedido == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\":\"Pedido no encontrado\"}");
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(pedido));
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(pedidoController.listarPedidos()));
    }

    private Map<String, Object> obtenerPedido(String idPedidoParam) {
        try {
            int idPedido = Integer.parseInt(idPedidoParam);
            return pedidoController.consultarPedido(idPedido);
        } catch (NumberFormatException e) {
            return new HashMap<>();
        }
    }
}
