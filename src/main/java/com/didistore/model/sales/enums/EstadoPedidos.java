
package com.didistore.model.sales.enums;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Enumeración que representa los diferentes estados de un pedido en el sistema de ventas.
public enum EstadoPedidos {
    Pendiente_Pago,
    Pagado,
    En_Preparacion,
    Despachado,
    En_Transito,
    Entregado,
    Cancelado,
    Devuelto
}