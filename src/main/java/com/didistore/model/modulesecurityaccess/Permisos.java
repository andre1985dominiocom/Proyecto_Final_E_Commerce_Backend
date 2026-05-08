
package com.didistore.model.modulesecurityaccess;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Permisos {
    private int idPermiso;
    private String nombrePermiso;
    private String descripcionPermiso;
    
    public Permisos() {}
    
    public Permisos (int idPermiso,
            String nombrePermiso,
            String descripcionPermido) {
        
        this.idPermiso = idPermiso;
        this.nombrePermiso = nombrePermiso;
        this.descripcionPermiso = descripcionPermido;
    }
    
    public int getidPermiso() { return idPermiso; }
    public void setidPermiso (int idPermiso) { this.idPermiso = idPermiso; }
    
    public String getnombrePermiso() { return nombrePermiso; }
    public void setnombrePermiso (String nombrePermiso) { this.nombrePermiso = nombrePermiso; }
    
    public String getdescripcionPermiso() { return descripcionPermiso; }
    public void setdescripcionPermiso ( String descripcionPermiso) { this.descripcionPermiso = descripcionPermiso; }
}