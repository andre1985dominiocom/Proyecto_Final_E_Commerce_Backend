
package com.didistore.dao.interfaces.sales;

import com.didistore.model.sales.Pagos;
import com.didistore.model.sales.enums.EstadoPagos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Interfaz que define los métodos para la gestión de pagos en la base de datos.
public interface IPagosDAO {
    
    boolean registrarPago(Pagos pago);
    
    boolean cancelarPago(int idPago);
    
    boolean actualizarEstadoPago(int idPago, EstadoPagos nuevoEstado);
    
    Pagos buscarPorId(int idPago);
    
    List<Pagos> listarPagosPorVenta(int pedidoId);
}