
package com.didistore.servlet.catalog;

import com.didistore.controller.catalog.ResenasController;
import com.didistore.model.catalog.Resenas;
import com.didistore.model.catalog.enums.EstadoResenas;
import com.didistore.util.AuditLog;
import com.didistore.util.SessionUtil;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// RF-21: GET público solo Aprobadas; POST requiere sesión + compra verificada.
// PUT editar propia reseña (vuelve a Pendiente); DELETE propia reseña.
// Moderación (aprobar/rechazar) está en /admin/resenas (staff).
@WebServlet("/catalog/resena")
public class ResenasServlet extends HttpServlet  {

    private final ResenasController resenasController = new ResenasController();
    private final Gson gson = new Gson();

    // GET: reseñas aprobadas de un producto (+ resumen rating).
    // ?puedeResenar=1 (con sesión) → flag de elegibilidad.
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String productoIdStr = request.getParameter("productoId");

        if (productoIdStr == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"Falta el parámetro productoId\"}");
            out.flush();
            return;
        }

        try {
            int productoId = Integer.parseInt(productoIdStr);
            List<Resenas> lista = resenasController.consultarResenasPorProducto(productoId);

            Map<String, Object> payload = new HashMap<>();
            payload.put("success", true);
            payload.put("resenas", lista != null ? lista : List.of());
            payload.put("promedio", resenasController.promedioAprobadas(productoId));
            payload.put("total", resenasController.contarAprobadas(productoId));

            Integer usuarioId = SessionUtil.obtenerUsuarioId(request);
            if (usuarioId != null) {
                payload.put("puedeResenar", resenasController.puedeResenar(usuarioId, productoId));
                Resenas mia = new com.didistore.dao.impl.catalog.ResenasDAOImpl()
                        .obtenerResenaPorUsuarioYProducto(usuarioId, productoId);
                payload.put("miResena", mia);
            }

            out.print(gson.toJson(payload));
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"error\": \"productoId inválido\"}");
        }
        out.flush();
    }

    // POST: registrar reseña (solo compradores verificados, estado Pendiente).
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Integer usuarioId = SessionUtil.obtenerUsuarioId(request);
        if (usuarioId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"status\": \"error\", \"message\": \"Debes iniciar sesión para dejar una reseña.\"}");
            out.flush();
            return;
        }

        try {
            Resenas nuevaResena = gson.fromJson(request.getReader(), Resenas.class);
            if (nuevaResena == null || nuevaResena.getproductoId() <= 0
                    || nuevaResena.getcalificacion() < 1 || nuevaResena.getcalificacion() > 5) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\": \"error\", \"message\": \"Datos de reseña inválidos.\"}");
                out.flush();
                return;
            }

            nuevaResena.setusuarioId(usuarioId);
            nuevaResena.setestado(EstadoResenas.Pendiente);
            nuevaResena.setidResena(0);

            String error = resenasController.registrarResena(nuevaResena);

            if (error == null) {
                AuditLog.registrar(usuarioId, null, "CREATE", "catalog", "resenas",
                        null, null, "producto=" + nuevaResena.getproductoId()
                        + " cal=" + nuevaResena.getcalificacion(), request.getRemoteAddr(), "EXITO");
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"status\": \"success\", \"message\": "
                        + "\"Gracias por tu reseña. Será publicada después de moderación (máximo 48 horas)\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(gson.toJson(Map.of("status", "error", "message", error)));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\": \"error\", \"message\": \"Error interno\"}");
        }
        out.flush();
    }

    // PUT: editar reseña propia (idResena + calificacion + comentario en body).
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Integer usuarioId = SessionUtil.obtenerUsuarioId(request);
        if (usuarioId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"status\":\"error\",\"message\":\"Sesión no iniciada\"}");
            out.flush();
            return;
        }

        try {
            Resenas body = gson.fromJson(request.getReader(), Resenas.class);
            if (body == null || body.getidResena() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"status\":\"error\",\"message\":\"idResena requerido\"}");
                out.flush();
                return;
            }

            boolean ok = resenasController.modificarResenaPropia(
                    body.getidResena(), usuarioId, body.getcomentario(), body.getcalificacion());

            if (ok) {
                AuditLog.registrar(usuarioId, null, "UPDATE", "catalog", "resenas",
                        body.getidResena(), null, null, request.getRemoteAddr(), "EXITO");
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"status\":\"success\",\"message\":\"Reseña actualizada. "
                        + "Volverá a pasar por moderación.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"status\":\"error\",\"message\":\"No puedes editar esta reseña\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"status\":\"error\",\"message\":\"Error interno\"}");
        }
        out.flush();
    }

    // DELETE: eliminar reseña propia (?idResena=N).
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        Integer usuarioId = SessionUtil.obtenerUsuarioId(request);
        if (usuarioId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"status\":\"error\",\"message\":\"Sesión no iniciada\"}");
            out.flush();
            return;
        }

        try {
            int idResena = Integer.parseInt(request.getParameter("idResena"));
            Resenas existente = new com.didistore.dao.impl.catalog.ResenasDAOImpl()
                    .obtenerResenaPorId(idResena);

            if (existente == null || existente.getusuarioId() != usuarioId) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                out.print("{\"status\":\"error\",\"message\":\"No autorizado\"}");
                out.flush();
                return;
            }

            boolean ok = resenasController.eliminarResena(idResena);
            if (ok) {
                AuditLog.registrar(usuarioId, null, "DELETE", "catalog", "resenas",
                        idResena, null, null, request.getRemoteAddr(), "EXITO");
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"status\":\"success\",\"message\":\"Reseña eliminada\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"status\":\"error\",\"message\":\"No se pudo eliminar\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"status\":\"error\",\"message\":\"idResena inválido\"}");
        }
        out.flush();
    }
}
