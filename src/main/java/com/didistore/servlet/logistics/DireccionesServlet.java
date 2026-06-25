
package com.didistore.servlet.logistics;

import com.didistore.controller.logistics.DireccionesController;
import com.didistore.model.logistics.Direcciones;
import com.didistore.model.logistics.enums.EstadoDirecciones;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

@WebServlet("/logistics/direccion")
public class DireccionesServlet extends HttpServlet {
    
    private final DireccionesController controller = new DireccionesController();
    private final Gson gson = new Gson();

    // GET: Obtener lista de direcciones (devuelve JSON)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Integer usuarioId = (session != null) ? (Integer) session.getAttribute("usuarioId") : null;

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (usuarioId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"error\": \"Usuario no autenticado\"}");
            return;
        }

        List<Direcciones> lista = controller.obtenerDireccionesUsuario(usuarioId);
        response.getWriter().write(gson.toJson(lista));
    }

    // POST: Agregar, eliminar o marcar principal (procesa datos del formulario)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        Integer usuarioId = (session != null) ? (Integer) session.getAttribute("usuarioId") : null;

        if (usuarioId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"success\":false,\"message\":\"Usuario no autenticado\"}");
            return;
        }

        if (action == null || action.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\":false,\"message\":\"Parámetro 'action' requerido\"}");
            return;
        }

        try {
            boolean resultado = false;
            switch (action) {
                case "agregar":
                    Direcciones nuevaDir = new Direcciones();
                    nuevaDir.setusuarioId(usuarioId);
                    nuevaDir.setdireccion(request.getParameter("direccion"));
                    nuevaDir.setbarrio(request.getParameter("barrio"));
                    nuevaDir.setreferencia(request.getParameter("referencia"));
                    String ciudadParam = request.getParameter("ciudadId");
                    nuevaDir.setciudadId(ciudadParam != null && !ciudadParam.isEmpty() ? Integer.parseInt(ciudadParam) : 0);
                    nuevaDir.setestado(EstadoDirecciones.Activa);
                    resultado = controller.agregarDireccion(nuevaDir);
                    break;

                case "eliminar":
                    int idEliminar = Integer.parseInt(request.getParameter("idDireccion"));
                    resultado = controller.eliminarDireccion(idEliminar);
                    break;

                case "principal":
                    int idPrincipal = Integer.parseInt(request.getParameter("idDireccion"));
                    resultado = controller.establecerPrincipal(usuarioId, idPrincipal);
                    break;

                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"success\":false,\"message\":\"Acción no reconocida\"}");
                    return;
            }
            response.setStatus(resultado ? HttpServletResponse.SC_OK : HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":" + resultado + "}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"success\":false,\"message\":\"Error interno\"}");
        }
    }    
}