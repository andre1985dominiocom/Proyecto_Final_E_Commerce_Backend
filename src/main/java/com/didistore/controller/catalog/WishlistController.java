
package com.didistore.controller.catalog;

import com.didistore.dao.impl.catalog.WishlistDAOImpl;
import com.didistore.dao.interfaces.catalog.IWishlistDAO;
import com.didistore.model.catalog.Wishlist;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class WishlistController {
    
    private final IWishlistDAO wishlistDAO;

    public WishlistController() {
        this.wishlistDAO = new WishlistDAOImpl();
    }

    public boolean agregarProducto(Wishlist wishlist) {
        if (wishlist == null || wishlist.getusuarioId() <= 0 || wishlist.getproductoId() <= 0) {
            return false;
        }
        
        if (wishlistDAO.perteneceAlWishlist(wishlist.getusuarioId(), wishlist.getproductoId())) {
            System.out.println("El producto ya se encuentra en la lista de deseos.");
            return false; 
        }
        
        return wishlistDAO.agregarAlWishlist(wishlist);
    }

    public boolean eliminarProducto(int usuarioId, int productoId) {
        if (usuarioId <= 0 || productoId <= 0) return false;
        return wishlistDAO.eliminarDelWishlist(usuarioId, productoId);
    }

    public List<Wishlist> listarPorUsuario(int usuarioId) {
        if (usuarioId <= 0) return null;
        return wishlistDAO.obtenerWishlistPorUsuario(usuarioId);
    }

    public boolean comprobarEstado(int usuarioId, int productoId) {
        if (usuarioId <= 0 || productoId <= 0) return false;
        return wishlistDAO.perteneceAlWishlist(usuarioId, productoId);
    }    
}