
package com.didistore.servlet.admin;

import com.didistore.controller.admin.AdminInventariosController;
import com.didistore.model.catalog.Inventarios;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Permitir que el servlet maneje solicitudes desde cualquier origen (CORS)
@WebServlet("/admin/inventarios")
public class AdminInventariosServlet extends HttpServlet {
    
    private AdminInventariosController inventarioController;
    private Gson gson;

    // --- Inicialización del servlet ---
    @Override
    public void init() throws ServletException {
        this.inventarioController = new AdminInventariosController();
        this.gson = new Gson();
    }
    
    // --- NUEVO: Interceptar la petición de seguridad del navegador ---
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        configurarCabecerasJSON(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * 1. GET: Listar todo o consultar por ID
     * JS invocación: fetch('/didistore/admin/inventarios') o fetch('/didistore/admin/inventarios?id=1')
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        configurarCabecerasJSON(response);
        String idParam = request.getParameter("id");

        try {
            if (idParam != null && !idParam.trim().isEmpty()) {
                int idInventario = Integer.parseInt(idParam);
                Inventarios inventario = inventarioController.consultarInventariosPorId(idInventario);
                
                if (inventario != null) {
                    response.getWriter().write(gson.toJson(inventario));
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                    response.getWriter().write("{\"error\": \"Inventario no encontrado\"}");
                }
            } else {
                List<Inventarios> lista = inventarioController.listarInventario();
                response.getWriter().write(gson.toJson(lista));
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
            response.getWriter().write("{\"error\": \"El ID provisto no es válido\"}");
        }
    }

    /**
     * 2. POST: Crear un nuevo registro de inventario (Opcional, si tu lógica lo requiere)
     * JS invocación: fetch('/didistore/admin/inventarios', { method: 'POST', body: JSON })
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> resultado = new HashMap<>();
        
        try {
            BufferedReader reader = request.getReader();
            
            Inventarios inventario = gson.fromJson(reader, Inventarios.class);
            
            if (inventario == null || inventario.getproductoId() <= 0 || inventario.getstockActual() < 0
                    || inventario.getstockMinimo() < 0 || inventario.getstockReservado() < 0) {
                
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                
                resultado.put(
                    "success",
                    false );
                
                resultado.put(
                    "message",
                    "Datos de inventario inválidos" );
                
                response.getWriter().write(gson.toJson(resultado));
                
                return;
            }
            
            boolean creado = inventarioController.crearInventario(inventario);
            
            if (creado) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                
                resultado.put(
                    "success",
                    true );
                
                resultado.put(
                    "message",
                    "Inventario creado correctamente" );
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                
                resultado.put(
                    "success",
                    false );
                
                resultado.put(
                    "message",
                    "El producto ya tiene inventario o no existe" );
            } 
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            
            resultado.put(
                "success",
                false );
            
            resultado.put(
                "message",
                e.getMessage() );
        }
        response.getWriter().write(gson.toJson(resultado));
    }

    /**
     * 3. PUT: Actualizar un inventario existente
     * JS invocación: fetch('/didistore/admin/inventarios', { method: 'PUT', body: JSON })
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        configurarCabecerasJSON(response);
        
        try {
            // Convertimos el JSON que envía JavaScript directamente a nuestro objeto Java
            Inventarios inventarioEditar = leerJsonDesdeCuerpo(request, Inventarios.class);
            
            boolean exito = inventarioController.actualizarInventario(inventarioEditar);
            
            if (exito) {
                response.setStatus(HttpServletResponse.SC_OK); // 200
                response.getWriter().write("{\"success\": true, \"mensaje\": \"Inventario actualizado con éxito\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                response.getWriter().write("{\"success\": false, \"error\": \"Validación de negocio fallida o ID no encontrado\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500
            response.getWriter().write("{\"success\": false, \"error\": \"Error al procesar los datos\"}");
        }
    }

    /**
     * 4. DELETE: Eliminar un registro de inventario
     * JS invocación: fetch('/didistore/admin/inventarios?id=1', { method: 'DELETE' })
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        configurarCabecerasJSON(response);
        String idParam = request.getParameter("id");
        
        if (idParam == null || idParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"error\": \"Falta el parámetro ID\"}");
            return;
        }
        
        try {
            int idInventario = Integer.parseInt(idParam);
            boolean eliminado = inventarioController.eliminarInventario(idInventario);
            
            if (eliminado) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"success\": true, \"mensaje\": \"Inventario eliminado con éxito\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"success\": false, \"error\": \"No se pudo eliminar el inventario\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"success\": false, \"error\": \"ID inválido\"}");
        }
    }

    /**
     * Método utilitario para configurar respuestas JSON uniformes
     */
    private void configurarCabecerasJSON(HttpServletResponse response) {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
    }

    /**
     * Método utilitario para leer el JSON crudo del cuerpo de la petición (para PUT y POST)
     */
    private <T> T leerJsonDesdeCuerpo(HttpServletRequest request, Class<T> claseDestino) throws IOException {
        StringBuilder sb = new StringBuilder();
        String linea;
        try (BufferedReader reader = request.getReader()) {
            while ((linea = reader.readLine()) != null) {
                sb.append(linea);
            }
        }
        return gson.fromJson(sb.toString(), claseDestino);
    }
}