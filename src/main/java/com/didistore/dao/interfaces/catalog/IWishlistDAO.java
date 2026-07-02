
package com.didistore.dao.interfaces.catalog;

import com.didistore.model.catalog.Wishlist;
import java.util.List;

/**
 *
 * @author Sergio Andrés Älvarez Lache
 */

// Interfaz para el DAO de Wishlist, define los métodos que se implementarán en la clase concreta.
public interface IWishlistDAO {
    
    boolean agregarAlWishlist(Wishlist wishlist);
    
    boolean eliminarDelWishlist(int usuarioId, int productoId);
    
    List<Wishlist> obtenerWishlistPorUsuario(int usuarioId);
    
    boolean perteneceAlWishlist(int usuarioId, int productoId);
}