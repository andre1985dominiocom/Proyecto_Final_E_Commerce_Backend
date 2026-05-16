
package com.didistore.model.auth;

/**
 *
 * @author Sergio Andrés álvarez Lache
 */
public class Perfiles {    
    private int idPerfil;
    private String nombrePerfil;
    private String descripcionPerfil;
    
    public Perfiles() {}
    
    public Perfiles (int idPerfil,
            String nombrePerfil,
            String descripcionPerfil) {
        
        this.idPerfil = idPerfil;
        this.nombrePerfil = nombrePerfil;
        this.descripcionPerfil = descripcionPerfil;
    }
    
    public int getidPerfil() { return idPerfil; }
    public void setidPerfil (int idPerfil) { this.idPerfil = idPerfil; }
    
    public String getnombrePerfil() { return nombrePerfil; }
    public void setnombrePerfil (String nombrePerfil) { this.nombrePerfil = nombrePerfil; }
    
    public String getdescripcionPerfil() { return descripcionPerfil; }
    public void setdescripcionPerfil (String descripcionPerfil) { this.descripcionPerfil = descripcionPerfil; }    
}