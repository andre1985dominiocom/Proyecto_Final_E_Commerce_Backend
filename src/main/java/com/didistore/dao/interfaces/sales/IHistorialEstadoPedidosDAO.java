
package com.didistore.dao.interfaces.sales;

import com.didistore.model.sales.HistorialEstadoPedidos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Interfaz para el DAO de HistorialEstadoPedidos, define los métodos que se deben implementar
// para interactuar con la base de datos.
public interface IHistorialEstadoPedidosDAO {
    
    boolean registrarCambioEstado(HistorialEstadoPedidos historial);
    
    List<HistorialEstadoPedidos> listarHistorialPorPedido(int pedidoId);
    
    HistorialEstadoPedidos obtenerUltimoEstado(int pedidoId);
}