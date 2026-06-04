package com.didistore.controller.sales.servlet;

import com.didistore.controller.sales.CarritoController;
import com.didistore.controller.sales.PedidoController;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/sales/checkout")
public class CheckoutServlet extends HttpServlet {

    private static final double COSTO_ENVIO_BASE = 10000d;
    private final PedidoController pedidoController = new PedidoController();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> resultado = new HashMap<>();

        try {
            JsonObject body = JsonParser.parseReader(request.getReader()).getAsJsonObject();
            String nombre = obtenerTexto(body, "first-name");
            String apellido = obtenerTexto(body, "last-name");
            String email = obtenerTexto(body, "email");
            List<CarritoController.ItemSolicitudCarrito> items = convertirItems(body.getAsJsonArray("items"));

            if (nombre.isBlank() || apellido.isBlank() || email.isBlank() || items.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resultado.put("success", false);
                resultado.put("message", "Datos de checkout incompletos");
                response.getWriter().write(gson.toJson(resultado));
                return;
            }

            double subtotal = calcularSubtotal(items);
            double costoEnvio = subtotal > 0 ? COSTO_ENVIO_BASE : 0d;
            Map<String, Object> pedido = pedidoController.registrarPedido(
                    (nombre + " " + apellido).trim(),
                    email,
                    items,
                    0d,
                    costoEnvio
            );

            if (pedido == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resultado.put("success", false);
                resultado.put("message", "No fue posible construir el pedido");
                response.getWriter().write(gson.toJson(resultado));
                return;
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            resultado.put("success", true);
            resultado.put("message", "Pedido confirmado correctamente");
            resultado.put("pedido", pedido);
            response.getWriter().write(gson.toJson(resultado));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado.put("success", false);
            resultado.put("message", "No se pudo procesar el checkout");
            response.getWriter().write(gson.toJson(resultado));
        }
    }

    private List<CarritoController.ItemSolicitudCarrito> convertirItems(JsonArray itemsArray) {
        List<CarritoController.ItemSolicitudCarrito> items = new ArrayList<>();
        if (itemsArray == null) {
            return items;
        }

        for (JsonElement itemElement : itemsArray) {
            if (!itemElement.isJsonObject()) {
                continue;
            }

            JsonObject itemJson = itemElement.getAsJsonObject();
            CarritoController.ItemSolicitudCarrito item = new CarritoController.ItemSolicitudCarrito();
            item.setProductoId(obtenerEntero(itemJson, "id"));
            item.setNombreProducto(obtenerTexto(itemJson, "name"));
            item.setCantidad(Math.max(1, obtenerEntero(itemJson, "quantity", 1)));
            item.setPrecioUnitario(obtenerDecimal(itemJson, "price"));
            items.add(item);
        }
        return items;
    }

    private double calcularSubtotal(List<CarritoController.ItemSolicitudCarrito> items) {
        double subtotal = 0d;
        for (CarritoController.ItemSolicitudCarrito item : items) {
            subtotal += item.getPrecioUnitario() * item.getCantidad();
        }
        return subtotal;
    }

    private String obtenerTexto(JsonObject body, String propiedad) {
        return body != null && body.has(propiedad) && !body.get(propiedad).isJsonNull()
                ? body.get(propiedad).getAsString().trim()
                : "";
    }

    private int obtenerEntero(JsonObject body, String propiedad) {
        return obtenerEntero(body, propiedad, 0);
    }

    private int obtenerEntero(JsonObject body, String propiedad, int valorPorDefecto) {
        try {
            String valor = obtenerTexto(body, propiedad);
            return valor.isBlank() ? valorPorDefecto : Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return valorPorDefecto;
        }
    }

    private double obtenerDecimal(JsonObject body, String propiedad) {
        try {
            String valor = obtenerTexto(body, propiedad);
            return valor.isBlank() ? 0d : Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return 0d;
        }
    }
}
