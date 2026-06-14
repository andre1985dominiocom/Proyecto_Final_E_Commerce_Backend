
package com.didistore.dao.interfaces.sales;

import com.didistore.model.sales.HistorialEstadoPedidos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public interface IHistorialEstadoPedidosDAO {
    
    boolean registrarCambioEstado(HistorialEstadoPedidos historial);
    
    List<HistorialEstadoPedidos> listarHistorialPorPedido(int pedidoId);
    
    HistorialEstadoPedidos obtenerUltimoEstado(int pedidoId);
}