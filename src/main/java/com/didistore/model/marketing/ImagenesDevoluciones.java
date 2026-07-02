
package com.didistore.model.marketing;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase que representa una imagen asociada a una devolución en el sistema de marketing de la tienda.
public class ImagenesDevoluciones {
    private int idImagen;
    private int devolucionId;
    private String url;
    
    // Constructor vacío para permitir la creación de instancias sin parámetros.
    public ImagenesDevoluciones() {}
    
    // Constructor que inicializa los atributos de la clase con los valores proporcionados.
    public ImagenesDevoluciones(int idImagen,
            int devolucionId,
            String url) {
        
        this.idImagen = idImagen;
        this.devolucionId = devolucionId;
        this.url = url;
    }
    
    // Métodos getter y setter para acceder y modificar los atributos de la clase.
    public int getidImagen() { return idImagen; }
    public void setidImagen(int idImagen) { this.idImagen = idImagen; }
    
    public int getdevolucionId() { return devolucionId; }
    public void setdevolucionId(int devolucionId) { this.devolucionId = devolucionId; }
    
    public String geturl() { return url; }
    public void seturl(String url) { this.url = url; }
}