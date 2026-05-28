
package com.didistore.model.catalog;

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
    
    public int getIdImagen() { return idImagen; }
    public void setIdImagen(int idImagen) { this.idImagen = idImagen; }
    
    public int getProductoId() { return productoId; }
    public void setProductoId(int productoId) { this.productoId = productoId; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }
}