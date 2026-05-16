
package com.didistore.model.catalog;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Wishlist {
    private int idWishlist;
    private int usuarioId;
    private int productoId;
    
    public Wishlist() {}
    
    public Wishlist(int idWishlist,
            int usuarioId,
            int productoId) {
        
        this.idWishlist = idWishlist;
        this.usuarioId = usuarioId;
        this.productoId = productoId;
    }
    
    public int getidWishlist() { return idWishlist; }
    public void setidWishlist(int idWishlist) { this.idWishlist = idWishlist; }
    
    public int getusuarioId() { return usuarioId; }
    public void setusuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    
    public int getproductoId() { return productoId; }
    public void setproductoId(int productoId) { this.productoId = productoId; }
}