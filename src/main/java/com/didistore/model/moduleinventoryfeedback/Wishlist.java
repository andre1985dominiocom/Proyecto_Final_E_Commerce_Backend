
package com.didistore.model.moduleinventoryfeedback;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Wishlist {
    private int id_Wishlist;
    private int usuario_Id;
    private int producto_Id;
    
    public Wishlist() {}
    
    public Wishlist(int id_Wishlist,
            int usuario_Id,
            int producto_Id) {
        
        this.id_Wishlist = id_Wishlist;
        this.usuario_Id = usuario_Id;
        this.producto_Id = producto_Id;
    }
    
    public int getid_Wishlist() { return id_Wishlist; }
    public void setid_Wishlist(int id_Wishlist) { this.id_Wishlist = id_Wishlist; }
    
    public int getusuario_Id() { return usuario_Id; }
    public void setusuario_Id(int usuario_Id) { this.usuario_Id = usuario_Id; }
    
    public int getproducto_Id() { return producto_Id; }
    public void setproducto_Id(int producto_Id) { this.producto_Id = producto_Id; }
}