
package com.didistore.model;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Permisos {
    private int id_Permiso;
    private String nombre_Permiso;
    private String descripcion_Permiso;
    
    public Permisos() {}
    
    public Permisos (int id_Permiso,
            String nombre_Permiso,
            String descripcion_Permido) {
        
        this.id_Permiso = id_Permiso;
        this.nombre_Permiso = nombre_Permiso;
        this.descripcion_Permiso = descripcion_Permido;
    }
    
    public int getid_Permiso() { return id_Permiso; }
    public void setid_Permiso (int id_Permiso) { this.id_Permiso = id_Permiso; }
    
    public String getnombre_Permiso() { return nombre_Permiso; }
    public void setnombre_Permiso (String nombre_Permiso) { this.nombre_Permiso = nombre_Permiso; }
    
    public String getdescripcion_Permiso() { return descripcion_Permiso; }
    public void setdescripcion_Permiso ( String descripcion_Permiso) { this.descripcion_Permiso = descripcion_Permiso; }
}