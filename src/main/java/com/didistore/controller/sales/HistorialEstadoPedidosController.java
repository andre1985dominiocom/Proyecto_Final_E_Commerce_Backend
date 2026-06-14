
package com.didistore.controller.sales;

import com.didistore.dao.impl.sales.HistorialEstadoPedidosDAOImpl;
import com.didistore.dao.interfaces.sales.IHistorialEstadoPedidosDAO;
import com.didistore.model.sales.HistorialEstadoPedidos;
import com.didistore.model.sales.enums.EstadoPedidos;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class HistorialEstadoPedidosController {
    
    private final IHistorialEstadoPedidosDAO historialDAO = new HistorialEstadoPedidosDAOImpl();

    /**
     * Procesa las consultas GET (Listar línea de tiempo o ver el estado actual)
     */
    public void procesarConsulta(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            String accion = request.getParameter("accion");

            if ("listarPorPedido".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                List<HistorialEstadoPedidos> lista = historialDAO.listarHistorialPorPedido(pedidoId);
                
                // Respuesta rápida estructurada para verificar el envío de la lista
                out.print("{\"status\":\"success\", \"totalRegistros\":" + lista.size() + "}");
                
            } else if ("obtenerUltimo".equals(accion)) {
                int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
                HistorialEstadoPedidos ultimo = historialDAO.obtenerUltimoEstado(pedidoId);
                
                if (ultimo != null) {
                    out.print("{\"status\":\"success\", \"idHistorial\":" + ultimo.getidHistorial() 
                            + ", \"estadoActual\":\"" + ultimo.getestadoNuevo() + "\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"status\":\"error\", \"message\":\"No hay historial para este pedido\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("{\"status\":\"error\", \"message\":\"Error al procesar la consulta\"}");
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    /**
     * Procesa las acciones POST (Registrar de forma explícita una auditoría)
     */
    public void procesarAccion(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            String accion = request.getParameter("accion");

            if ("registrarCambio".equals(accion)) {
                HistorialEstadoPedidos nuevoHistorial = new HistorialEstadoPedidos();
                
                nuevoHistorial.setpedidoId(Integer.parseInt(request.getParameter("pedidoId")));
                nuevoHistorial.setusuarioId(Integer.parseInt(request.getParameter("usuarioId")));
                nuevoHistorial.setnotas(request.getParameter("notas"));
                
                // Mapeo seguro de enums (Validando que el estado anterior pueda ser nulo si es pedido nuevo)
                String estAntStr = request.getParameter("estadoAnterior");
                nuevoHistorial.setestadoAnterior(estAntStr != null && !estAntStr.isEmpty() ? EstadoPedidos.valueOf(estAntStr) : null);
                nuevoHistorial.setestadoNuevo(EstadoPedidos.valueOf(request.getParameter("estadoNuevo")));
                
                // Timestamp del sistema
                nuevoHistorial.setfechaCambio(new Timestamp(System.currentTimeMillis()));

                boolean exito = historialDAO.registrarCambioEstado(nuevoHistorial);

                if (exito) {
                    out.print("{\"status\":\"success\", \"message\":\"Auditoría de estado registrada correctamente\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"status\":\"error\", \"message\":\"No se pudo insertar el registro en la BD\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print("{\"status\":\"error\", \"message\":\"Datos inválidos en el formulario\"}");
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }   
}