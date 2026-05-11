
package com.didistore.model.modulemarketingpostsales;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class ImagenesDevoluciones {
    private int idImagen;
    private int devolucionId;
    private String url;
    
    public ImagenesDevoluciones() {}
    
    public ImagenesDevoluciones(int idImagen,
            int devolucionId,
            String url) {
        
        this.idImagen = idImagen;
        this.devolucionId = devolucionId;
        this.url = url;
    }
    
    public int getidImagen() { return idImagen; }
    public void setidImagen(int idImagen) { this.idImagen = idImagen; }
    
    public int getdevolucionId() { return devolucionId; }
    public void setdevolucionId(int devolucionId) { this.devolucionId = devolucionId; }
    
    public String geturl() { return url; }
    public void seturl(String url) { this.url = url; }
}