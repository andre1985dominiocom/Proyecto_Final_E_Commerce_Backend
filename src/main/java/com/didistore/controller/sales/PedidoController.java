
package com.didistore.controller.sales;

import com.didistore.dao.impl.sales.PedidosDAOImpl;
import com.didistore.dao.interfaces.sales.IPedidosDAO;
import com.didistore.model.sales.DetallesPedidos;
import com.didistore.model.sales.Pedidos;
import com.didistore.model.sales.enums.EstadoPedidos;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PedidoController {
    
    private final IPedidosDAO pedidosDAO;

    public PedidoController() {
        this.pedidosDAO = new PedidosDAOImpl();
    }

    /**
     * Lógica central del Checkout: Convierte un resumen de compra y sus detalles
     * en un Pedido real persistido en la base de datos.
     */
    public boolean procesarCheckout(int usuarioId, int direccionEnvioId, double subtotal, double costoEnvio, List<DetallesPedidos> detalles, int cuponId) {
        
        // Validaciones de negocio preventivas
        if (detalles == null || detalles.isEmpty()) {
            return false; 
        }
        if (subtotal < 0 || costoEnvio < 0) {
            return false;
        }

        Pedidos nuevoPedido = new Pedidos();
        
        // 1. Generar un número de pedido único y amigable para el cliente (Ej: PED-A1B2C3D4)
        nuevoPedido.setnumeroPedido("PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        // 2. Asignación de datos relacionales
        nuevoPedido.setusuarioId(usuarioId);
        nuevoPedido.setdireccionEnvioId(direccionEnvioId);
        nuevoPedido.setestadoPedido(EstadoPedidos.Pendiente_Pago); // Estado inicial por defecto
        
        if (cuponId > 0) {
            nuevoPedido.setcuponId(cuponId);
        }

        // 3. Lógica Financiera (Snapshots para la BD)
        double descuento = calcularDescuento(subtotal, cuponId); // Método auxiliar interno
        double iva = (subtotal - descuento) * 0.19; // Asumiendo un IVA del 19%
        double montoTotal = (subtotal - descuento) + iva + costoEnvio;

        nuevoPedido.setsubTotal(subtotal);
        nuevoPedido.setdescuento(descuento);
        nuevoPedido.setiva(iva);
        nuevoPedido.setcostoEnvio(costoEnvio);
        nuevoPedido.setmontoTotal(montoTotal);

        // 4. Inyección del DAO: Ejecuta la transacción (Guarda Cabecera + Detalles)
        return pedidosDAO.crearPedido(nuevoPedido, detalles);
    }

    /**
     * Obtiene todos los pedidos realizados por un usuario específico.
     */
    public List<Pedidos> obtenerHistorialUsuario(int usuarioId) {
        return pedidosDAO.listarPorUsuario(usuarioId);
    }
    
    /**
     * Obtiene la información cabecera de un pedido puntual.
     */
    public Pedidos obtenerDetallePedido(int pedidoId) {
        return pedidosDAO.buscarPorId(pedidoId);
    }
    
    /**
     * Obtiene la lista de productos que fueron comprados dentro de un pedido.
     */
    public List<DetallesPedidos> obtenerItemsPedido(int pedidoId) {
        return pedidosDAO.listarDetallesPorPedido(pedidoId);
    }

    /**
     * Permite cambiar el estado de un pedido (Ej: de 'Pendiente_Pago' a 'Pagado' o 'En_Transito').
     */
    public boolean actualizarEstadoPedido(int pedidoId, EstadoPedidos nuevoEstado) {
        if (nuevoEstado == null) return false;
        return pedidosDAO.actualizarEstado(pedidoId, nuevoEstado);
    }

    // =========================================================================
    // Métodos Auxiliares Internos
    // =========================================================================

    /**
     * Simulación de lógica de negocio para descuentos.
     * En un entorno real, aquí consultarías a tu CuponesDAO.
     */
    private double calcularDescuento(double subtotal, int cuponId) {
        if (cuponId > 0) {
            // Ejemplo: si hay cupón, se aplica un 10% de descuento ficticio
            return subtotal * 0.10; 
        }
        return 0.0;
    }  
}