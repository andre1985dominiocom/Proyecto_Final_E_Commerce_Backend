package com.didistore.servlet.admin;

import com.didistore.controller.marketing.PromocionesController;
import com.didistore.model.marketing.Promociones;
import com.didistore.model.marketing.enums.EstadoPromociones;
import com.didistore.model.marketing.enums.TipoDescuentos;
import com.didistore.util.AuditLog;
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
import java.util.List;
import java.util.Map;

// CRUD de promociones (RF-09 / CU-ADM-05). Protegido por RoleFilter (/admin/*).
@WebServlet("/admin/promociones")
public class AdminPromocionesServlet extends HttpServlet {

    private final PromocionesController promocionesController = new PromocionesController();
    private final Gson gson = new com.google.gson.GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonSerializer<LocalDateTime>) (src, type, context) ->
                            new com.google.gson.JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonDeserializer<LocalDateTime>) (json, type, context) ->
                            LocalDateTime.parse(json.getAsString()))
            .create();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("idPromocion");
        Map<String, Object> resultado = new HashMap<>();

        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int idPromocion = Integer.parseInt(idParam);
                Promociones promocion = promocionesController.buscarPorId(idPromocion);
                if (promocion != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(gson.toJson(promocion));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resultado.put("success", false);
                    resultado.put("message", "Promoción no encontrada");
                    response.getWriter().write(gson.toJson(resultado));
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resultado.put("success", false);
                resultado.put("message", "idPromocion inválido");
                response.getWriter().write(gson.toJson(resultado));
            }
        } else {
            List<Promociones> promociones = promocionesController.listarPromociones();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(promociones));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        Promociones promocion;
        try {
            promocion = parsearPromocion(request);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"Datos inválidos\"}");
            return;
        }

        Map<String, Object> resultado = promocionesController.crearPromocion(promocion);

        if (Boolean.TRUE.equals(resultado.get("success"))) {
            AuditLog.registrar(obtenerUsuarioId(request), obtenerEmail(request),
                    "CREATE", "marketing", "promociones", null, null,
                    gson.toJson(promocion), request.getRemoteAddr(), "EXITO");
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        response.getWriter().write(gson.toJson(resultado));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        Promociones promocion;
        Map<String, Object> resultado;
        try {
            promocion = parsearPromocion(request);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"Datos inválidos\"}");
            return;
        }

        if (promocion.getidPromocion() <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado = new HashMap<>();
            resultado.put("success", false);
            resultado.put("message", "idPromocion requerido para actualizar");
        } else {
            Promociones antes = promocionesController.buscarPorId(promocion.getidPromocion());
            resultado = promocionesController.actualizarPromocion(promocion);
            if (Boolean.TRUE.equals(resultado.get("success"))) {
                AuditLog.registrar(obtenerUsuarioId(request), obtenerEmail(request),
                        "UPDATE", "marketing", "promociones", promocion.getidPromocion(),
                        antes != null ? gson.toJson(antes) : null, gson.toJson(promocion),
                        request.getRemoteAddr(), "EXITO");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        response.getWriter().write(gson.toJson(resultado));
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> resultado;
        String idParam = request.getParameter("idPromocion");

        try {
            int idPromocion = Integer.parseInt(idParam);
            Promociones antes = promocionesController.buscarPorId(idPromocion);
            resultado = promocionesController.eliminarPromocion(idPromocion);
            if (Boolean.TRUE.equals(resultado.get("success"))) {
                AuditLog.registrar(obtenerUsuarioId(request), obtenerEmail(request),
                        "DELETE", "marketing", "promociones", idPromocion,
                        antes != null ? gson.toJson(antes) : null, null,
                        request.getRemoteAddr(), "EXITO");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado = new HashMap<>();
            resultado.put("success", false);
            resultado.put("message", "idPromocion inválido");
        }
        response.getWriter().write(gson.toJson(resultado));
    }

    private Promociones parsearPromocion(HttpServletRequest request) throws IOException {
        Promociones promocion = null;

        String contentType = request.getContentType() != null
                ? request.getContentType().toLowerCase() : "";
        if (contentType.contains("application/json")) {
            BufferedReader reader = request.getReader();
            promocion = gson.fromJson(reader, Promociones.class);
        } else {
            promocion = new Promociones();
            String id = request.getParameter("idPromocion");
            if (id != null && !id.isBlank()) {
                promocion.setidPromocion(Integer.parseInt(id));
            }
            promocion.setnombrePromocion(request.getParameter("nombrePromocion"));
            promocion.setdescripcion(request.getParameter("descripcion"));
            String tipo = request.getParameter("tipoDescuento");
            if (tipo != null && !tipo.isBlank()) {
                promocion.settipoDescuento(TipoDescuentos.valueOf(tipo));
            }
            String valor = request.getParameter("valorDescuento");
            if (valor != null && !valor.isBlank()) {
                promocion.setvalorDescuento(Double.parseDouble(valor));
            }
            String estado = request.getParameter("estado");
            if (estado != null && !estado.isBlank()) {
                promocion.setestado(EstadoPromociones.valueOf(estado));
            }
        }

        if (promocion == null) {
            promocion = new Promociones();
        }

        String fechaInicio = request.getParameter("fechaInicio");
        String fechaFin = request.getParameter("fechaFin");
        if (promocion.getfechaInicio() == null && fechaInicio != null && !fechaInicio.isBlank()) {
            promocion.setfechaInicio(LocalDateTime.parse(fechaInicio));
        }
        if (promocion.getfechaFin() == null && fechaFin != null && !fechaFin.isBlank()) {
            promocion.setfechaFin(LocalDateTime.parse(fechaFin));
        }

        if (promocion.gettipoDescuento() == null && request.getParameter("tipoDescuento") != null) {
            promocion.settipoDescuento(TipoDescuentos.valueOf(request.getParameter("tipoDescuento")));
        }
        if (promocion.getestado() == null && request.getParameter("estado") != null) {
            promocion.setestado(EstadoPromociones.valueOf(request.getParameter("estado")));
        }

        return promocion;
    }

    private Integer obtenerUsuarioId(HttpServletRequest request) {
        return (Integer) request.getSession(false).getAttribute("usuarioId");
    }

    private String obtenerEmail(HttpServletRequest request) {
        Object email = request.getSession(false).getAttribute("email");
        return email != null ? email.toString() : null;
    }
}
