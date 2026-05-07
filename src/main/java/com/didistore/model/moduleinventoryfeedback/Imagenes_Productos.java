
package com.didistore.model.moduleinventoryfeedback;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Imagenes_Productos {
    private int id_Imagen;
    private int producto_Id;
    private String url;
    private String formato;
    
    public Imagenes_Productos() {}
    
    public Imagenes_Productos (int id_Imagen,
            int producto_Id,
            String url,
            String formato) {
        
        this.id_Imagen = id_Imagen;
        this.producto_Id = producto_Id;
        this.url = url;
        this.formato = formato;
    }
    
    public int getid_Imagen() { return id_Imagen; }
    public void setid_Imagen(int id_Imagen) { this.id_Imagen = id_Imagen; }
    
    public int getproducto_Id() { return producto_Id; }
    public void setproducto_Id(int producto_Id) { this.producto_Id = producto_Id; }
    
    public String geturl() { return url; }
    public void seturl(String url) { this.url = url; }
    
    public String getformato() { return formato; }
    public void setformato(String formato) { this.formato = formato; }
}