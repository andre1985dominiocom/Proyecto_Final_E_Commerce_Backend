
package com.didistore.dao.interfaces.sales;

import com.didistore.model.sales.DetallesPedidos;
import com.didistore.model.sales.Pedidos;
import com.didistore.model.sales.enums.EstadoPedidos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Interfaz para la gestión de Pedidos y Detalles de Pedidos
public interface IPedidosDAO {
    
    // Métodos para Pedidos
    boolean crearPedido(Pedidos pedido, List<DetallesPedidos> detalles);
    
    Pedidos buscarPorId(int idPedido);
    
    Pedidos buscarPorNumeroPedido(String numeroPedido);
    
    List<Pedidos> listarPorUsuario(int usuarioId);
    
    boolean actualizarEstado(int idPedido, EstadoPedidos nuevoEstado);
    
    // Métodos para Detalles de Pedidos
    List<DetallesPedidos> listarDetallesPorPedido(int pedidoId);  
    
    double calcularVentasMesActual();
    
    int contarPedidosNuevos();
}