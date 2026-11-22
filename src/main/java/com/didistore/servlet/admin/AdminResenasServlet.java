
package com.didistore.servlet.admin;

import com.didistore.controller.catalog.ResenasController;
import com.didistore.model.catalog.Resenas;
import com.didistore.model.catalog.enums.EstadoResenas;
import com.didistore.util.AuditLog;
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

// RF-21 / CU-CLI-08: moderación de reseñas (ADMIN/EMPLEADO vía RoleFilter /admin/*).
// GET ?estado=Pendiente|Aprobada|Rechazada  ·  PUT ?accion=aprobar|rechazar&idResena=N
@WebServlet("/admin/resenas")
public class AdminResenasServlet extends HttpServlet {

    private final ResenasController resenasController = new ResenasController();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String estado = request.getParameter("estado");
        if (estado == null || estado.isBlank()) {
            estado = "Pendiente";
        }

        List<Resenas> lista = resenasController.consultarResenasPorEstado(estado);
        Map<String, Object> payload = new HashMap<>();
        payload.put("success", true);
        payload.put("resenas", lista != null ? lista : List.of());
        out.print(gson.toJson(payload));
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        jakarta.servlet.http.HttpSession s = request.getSession(false);
        Integer staffId = s != null ? (Integer) s.getAttribute("usuarioId") : null;
        String email = null;
        if (s != null && s.getAttribute("email") != null) {
            email = s.getAttribute("email").toString();
        }

        String accion = request.getParameter("accion");
        String idParam = request.getParameter("idResena");

        try {
            int idResena = Integer.parseInt(idParam);
            EstadoResenas nuevo;

            if ("aprobar".equals(accion)) {
                nuevo = EstadoResenas.Aprobada;
            } else if ("rechazar".equals(accion)) {
                nuevo = EstadoResenas.Rechazada;
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"success\":false,\"message\":\"accion debe ser aprobar o rechazar\"}");
                out.flush();
                return;
            }

            boolean ok = resenasController.moderarResena(idResena, nuevo);

            AuditLog.registrar(staffId, email,
                    ok ? "UPDATE" : "UPDATE", "catalog", "resenas", idResena,
                    null, "estado→" + nuevo.name(), request.getRemoteAddr(),
                    ok ? "EXITO" : "FALLIDO");

            if (ok) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"success\":true,\"message\":\"Reseña "
                        + (nuevo == EstadoResenas.Aprobada ? "aprobada" : "rechazada") + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.print("{\"success\":false,\"message\":\"No se pudo moderar "
                        + "(solo reseñas Pendientes)\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"message\":\"idResena inválido\"}");
        }
        out.flush();
    }
}
