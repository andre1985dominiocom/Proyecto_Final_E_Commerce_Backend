
package com.didistore.model.auth;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase que representa los permisos de acceso a las funcionalidades del sistema
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

    public void setidPerfil(int aInt) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}