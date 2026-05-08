
package com.didistore.model.moduleinventoryfeedback;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class ImagenesProductos {
    private int idImagen;
    private int productoId;
    private String url;
    private String formato;
    
    public ImagenesProductos() {}
    
    public ImagenesProductos (int idImagen,
            int productoId,
            String url,
            String formato) {
        
        this.idImagen = idImagen;
        this.productoId = productoId;
        this.url = url;
        this.formato = formato;
    }
    
    public int getidImagen() { return idImagen; }
    public void setidImagen(int idImagen) { this.idImagen = idImagen; }
    
    public int getproductoId() { return productoId; }
    public void setproductoId(int productoId) { this.productoId = productoId; }
    
    public String geturl() { return url; }
    public void seturl(String url) { this.url = url; }
    
    public String getformato() { return formato; }
    public void setformato(String formato) { this.formato = formato; }
}