
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
        
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (usuarioId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Usuario no autenticado\"}");
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
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Integer usuarioId = (Integer) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            switch (action) {
                case "agregar":
                    Direcciones nuevaDir = new Direcciones();
                    nuevaDir.setusuarioId(usuarioId);
                    nuevaDir.setdireccion(request.getParameter("direccion"));
                    nuevaDir.setbarrio(request.getParameter("barrio"));
                    nuevaDir.setreferencia(request.getParameter("referencia"));
                    nuevaDir.setciudadId(Integer.parseInt(request.getParameter("ciudadId")));
                    nuevaDir.setestado(EstadoDirecciones.Activa);
                    controller.agregarDireccion(nuevaDir);
                    break;

                case "eliminar":
                    int idEliminar = Integer.parseInt(request.getParameter("idDireccion"));
                    controller.eliminarDireccion(idEliminar);
                    break;

                case "principal":
                    int idPrincipal = Integer.parseInt(request.getParameter("idDireccion"));
                    controller.establecerPrincipal(usuarioId, idPrincipal); 
                    break;
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }    
}