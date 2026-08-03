
package com.didistore.controller.sales;

import com.didistore.dao.impl.sales.HistorialEstadoPedidosDAOImpl;
import com.didistore.dao.impl.sales.PagosDAOImpl;
import com.didistore.dao.impl.sales.PedidosDAOImpl;
import com.didistore.dao.interfaces.sales.IHistorialEstadoPedidosDAO;
import com.didistore.dao.interfaces.sales.IPagosDAO;
import com.didistore.dao.interfaces.sales.IPedidosDAO;
import com.didistore.model.sales.Pagos;
import com.didistore.model.sales.enums.EstadoPagos;
import com.didistore.model.sales.enums.MetodoPagos;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.io.PrintWriter;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Controlador para manejar las solicitudes relacionadas con los pagos
public class PagosController {
    
    private final IPagosDAO pagosDAO;
    private final IPedidosDAO pedidosDAO;
    private final IHistorialEstadoPedidosDAO historialDAO;
    
    public PagosController() {
        this.pagosDAO = new PagosDAOImpl();
        this.pedidosDAO = new PedidosDAOImpl();
        this.historialDAO = new HistorialEstadoPedidosDAOImpl();
    }
    
    public void procesarConsulta(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            String accion = request.getParameter("accion");

            if ("buscar".equals(accion)) {
                int idPago = Integer.parseInt(request.getParameter("idPago"));
                Pagos pago = pagosDAO.buscarPorId(idPago); // Controller -> DAO -> DB
                
                if (pago != null) {
                    out.print("{\"status\":\"success\", \"id\":" + pago.getidPago() + ", \"monto\":" + pago.getmonto() + "}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"status\":\"error\", \"message\":\"No encontrado\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void procesarAccion(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            String accion = request.getParameter("accion");

            if ("registrar".equals(accion)) {
                // Controller mapea los datos del Request al Model
                Pagos nuevoPago = new Pagos();
                nuevoPago.setpedidoId(Integer.parseInt(request.getParameter("pedidoId")));
                nuevoPago.setmonto(Double.parseDouble(request.getParameter("monto")));
                nuevoPago.setmetodoPago(MetodoPagos.valueOf(request.getParameter("metodoPago")));
                nuevoPago.setestadoPago(EstadoPagos.Pendiente);
                
                Timestamp ahora = new Timestamp(System.currentTimeMillis());
                nuevoPago.setfechaPago(ahora);
                nuevoPago.setfechaCreacion(ahora);

                // Controller envía el Model al DAO para persistir en BD
                boolean exito = pagosDAO.registrarPago(nuevoPago);

                if (exito) {
                    out.print("{\"status\":\"success\", \"message\":\"Registrado\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"status\":\"error\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}