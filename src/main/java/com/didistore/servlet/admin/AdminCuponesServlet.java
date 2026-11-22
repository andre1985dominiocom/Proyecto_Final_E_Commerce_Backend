package com.didistore.servlet.admin;

import com.didistore.controller.marketing.CuponesController;
import com.didistore.model.marketing.Cupones;
import com.didistore.model.marketing.enums.EstadoCupones;
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

// CRUD de cupones (RF-09 / CU-ADM-05). Protegido por RoleFilter (/admin/*).
@WebServlet("/admin/cupones")
public class AdminCuponesServlet extends HttpServlet {

    private final CuponesController cuponesController = new CuponesController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("idCupon");
        Map<String, Object> resultado = new HashMap<>();

        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int idCupon = Integer.parseInt(idParam);
                Cupones cupon = cuponesController.buscarPorId(idCupon);
                if (cupon != null) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write(gson.toJson(cupon));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resultado.put("success", false);
                    resultado.put("message", "Cupón no encontrado");
                    response.getWriter().write(gson.toJson(resultado));
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resultado.put("success", false);
                resultado.put("message", "idCupon inválido");
                response.getWriter().write(gson.toJson(resultado));
            }
        } else {
            List<Cupones> cupones = cuponesController.listarCupones();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(gson.toJson(cupones));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");

        Cupones cupon = parsearCupon(request);
        Map<String, Object> resultado = cuponesController.crearCupon(cupon);

        if (Boolean.TRUE.equals(resultado.get("success"))) {
            AuditLog.registrar(obtenerUsuarioId(request), obtenerEmail(request),
                    "CREATE", "marketing", "cupones", null, null,
                    gson.toJson(cupon), request.getRemoteAddr(), "EXITO");
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

        Cupones cupon = parsearCupon(request);
        Map<String, Object> resultado;

        if (cupon.getidCupon() <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resultado = new HashMap<>();
            resultado.put("success", false);
            resultado.put("message", "idCupon requerido para actualizar");
        } else {
            Cupones antes = cuponesController.buscarPorId(cupon.getidCupon());
            resultado = cuponesController.actualizarCupon(cupon);
            if (Boolean.TRUE.equals(resultado.get("success"))) {
                AuditLog.registrar(obtenerUsuarioId(request), obtenerEmail(request),
                        "UPDATE", "marketing", "cupones", cupon.getidCupon(),
                        antes != null ? gson.toJson(antes) : null, gson.toJson(cupon),
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
        String idParam = request.getParameter("idCupon");

        try {
            int idCupon = Integer.parseInt(idParam);
            Cupones antes = cuponesController.buscarPorId(idCupon);
            resultado = cuponesController.eliminarCupon(idCupon);
            if (Boolean.TRUE.equals(resultado.get("success"))) {
                AuditLog.registrar(obtenerUsuarioId(request), obtenerEmail(request),
                        "DELETE", "marketing", "cupones", idCupon,
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
            resultado.put("message", "idCupon inválido");
        }
        response.getWriter().write(gson.toJson(resultado));
    }

    private Cupones parsearCupon(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        Cupones cupon = gson.fromJson(reader, Cupones.class);
        if (cupon == null) {
            cupon = new Cupones();
        }

        // Acepta fechas ISO (yyyy-MM-ddTHH:mm) desde el frontend
        String fechaInicio = request.getParameter("fechaInicio");
        String fechaFin = request.getParameter("fechaFin");
        if (cupon.getfechaInicio() == null && fechaInicio != null && !fechaInicio.isBlank()) {
            cupon.setfechaInicio(LocalDateTime.parse(fechaInicio));
        }
        if (cupon.getfechaFin() == null && fechaFin != null && !fechaFin.isBlank()) {
            cupon.setfechaFin(LocalDateTime.parse(fechaFin));
        }

        if (cupon.gettipoDescuento() == null && request.getParameter("tipoDescuento") != null) {
            cupon.settipoDescuento(TipoDescuentos.valueOf(request.getParameter("tipoDescuento")));
        }
        if (cupon.getestado() == null && request.getParameter("estado") != null) {
            cupon.setestado(EstadoCupones.valueOf(request.getParameter("estado")));
        }

        return cupon;
    }

    private Integer obtenerUsuarioId(HttpServletRequest request) {
        return (Integer) request.getSession(false).getAttribute("usuarioId");
    }

    private String obtenerEmail(HttpServletRequest request) {
        Object email = request.getSession(false).getAttribute("email");
        return email != null ? email.toString() : null;
    }
}
