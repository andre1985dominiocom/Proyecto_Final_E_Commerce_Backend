
package com.didistore.dao.interfaces.sales;

import com.didistore.model.sales.Pagos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Interfaz que define los métodos para la gestión de pagos en la base de datos.
public interface IPagosDAO {
    
    boolean registrarPago(Pagos pago);
    
    boolean cancelarPago(int idPago);
    
    Pagos buscarPorId(int idPago);
    
    List<Pagos> listarPagosPorVenta(int pedidoId);
}