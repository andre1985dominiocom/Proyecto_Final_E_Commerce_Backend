
package com.didistore.dao.interfaces.sales;

import com.didistore.model.sales.Pagos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public interface IPagosDAO {
    
    boolean registrarPago(Pagos pago);
    
    boolean cancelarPago(int idPago);
    
    Pagos buscarPorId(int idPago);
    
    List<Pagos> listarPagosPorVenta(int pedidoId);
}