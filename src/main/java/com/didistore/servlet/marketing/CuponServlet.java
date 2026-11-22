package com.didistore.servlet.marketing;

import com.didistore.controller.marketing.CuponesController;
import com.didistore.model.marketing.Cupones;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// Validación de cupones para carrito/checkout (RF-09 / CU-CLI-02 / CU-CLI-03).
// Público: el anónimo también puede previsualizar el descuento.
@WebServlet("/catalog/cupon")
public class CuponServlet extends HttpServlet {

    private final CuponesController cuponesController = new CuponesController();
    private final Gson gson = new Gson();

    // GET /catalog/cupon?codigo=XXX&subtotal=NNN → validar cupón
    // GET /catalog/cupon?accion=listar → cupones activos (público, solo datos seguros)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String accion = request.getParameter("accion");
        if ("listar".equals(accion)) {
            response.getWriter().write(gson.toJson(listarActivosSeguros()));
            return;
        }

        String codigo = request.getParameter("codigo");
        String subtotalParam = request.getParameter("subtotal");
        double subtotal = 0.0;

        try {
            if (subtotalParam != null && !subtotalParam.isBlank()) {
                subtotal = Double.parseDouble(subtotalParam);
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"subtotal inválido\"}");
            return;
        }

        Map<String, Object> resultado = cuponesController.validarYCalcular(codigo, subtotal);
        if (!Boolean.TRUE.equals(resultado.get("success"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        response.getWriter().write(gson.toJson(resultado));
    }

    /** Cupones activos sin campos sensibles (usos internos, IDs). */
    private java.util.List<Map<String, Object>> listarActivosSeguros() {
        java.util.List<Map<String, Object>> out = new java.util.ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();
        for (Cupones c : cuponesController.listarCupones()) {
            if (c.getestado() == null || !"Activo".equals(c.getestado().name())) {
                continue;
            }
            if (c.getfechaInicio() != null && c.getfechaInicio().isAfter(ahora)) {
                continue;
            }
            if (c.getfechaFin() != null && c.getfechaFin().isBefore(ahora)) {
                continue;
            }
            if (c.getcantidadMaximaUsos() > 0
                    && c.getcantidadUsosActuales() >= c.getcantidadMaximaUsos()) {
                continue;
            }
            Map<String, Object> m = new java.util.HashMap<>();
            m.put("codigo", c.getcodigo());
            m.put("tipoDescuento", c.gettipoDescuento() != null ? c.gettipoDescuento().name() : null);
            m.put("valorDescuento", c.getvalorDescuento());
            m.put("montoMinimo", c.getmontoMinimo());
            m.put("fechaFin", c.getfechaFin() != null ? c.getfechaFin().toString() : null);
            out.add(m);
        }
        return out;
    }

    // POST body {"codigo":"...","subtotal":N} → validar cupón
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        CuponRequest datos;
        try {
            BufferedReader reader = request.getReader();
            datos = gson.fromJson(reader, CuponRequest.class);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"JSON inválido\"}");
            return;
        }

        if (datos == null || datos.getCodigo() == null || datos.getCodigo().isBlank()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"codigo requerido\"}");
            return;
        }

        Map<String, Object> resultado = cuponesController.validarYCalcular(
                datos.getCodigo(), datos.getSubtotal());

        if (!Boolean.TRUE.equals(resultado.get("success"))) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        response.getWriter().write(gson.toJson(resultado));
    }

    public static class CuponRequest {
        private String codigo;
        private double subtotal;

        public String getCodigo() { return codigo; }
        public void setCodigo(String codigo) { this.codigo = codigo; }
        public double getSubtotal() { return subtotal; }
        public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    }
}
