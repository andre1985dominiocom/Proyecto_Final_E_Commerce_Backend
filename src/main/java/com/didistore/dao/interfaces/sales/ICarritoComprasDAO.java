
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
    
    CarritoCompras buscarPorUsuario(int usuarioId);
    
    CarritoCompras buscarPorSesion(String sesionId);
    
    boolean actualizarPorFecha(int idCarrito);
    
    boolean eliminarCarrito(int idCarrito);

    // Items de carrito
    boolean agregarItem(ItemCarritos item);
    
    boolean actualizarCantidad(int idItem, int cantidad);
    
    boolean eliminarItem(int itemId);
    
    List<ItemCarritos> listarItems(int carritoId);

    boolean vaciarCarrito(int carritoId);
    
    double obtenerTotal(int carritoId);
}