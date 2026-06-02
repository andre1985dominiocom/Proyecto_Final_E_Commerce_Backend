
package com.didistore.dao.interfaces.sales;

import com.didistore.model.sales.CarritoCompras;
import com.didistore.model.sales.ItemCarritos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public interface ICarritoComprasDAO {
    
     // Carrito de compras
    boolean crearCarrito(CarritoCompras carrito);
    CarritoCompras obtenerCarritoPorId(int idCarrito);
    CarritoCompras obtenerCarritoPorUsuario(int usuarioId);

    // Items de carrito
    boolean agregarItem(ItemCarritos idItem);
    boolean actualizarItem(ItemCarritos idItem);
    boolean eliminarItem(int itemId);
    List<ItemCarritos> listarItems(int carritoId);

    boolean vaciarCarrito(int carritoId);
}