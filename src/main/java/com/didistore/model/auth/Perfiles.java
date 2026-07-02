
package com.didistore.model.auth;

/**
 *
 * @author Sergio Andrés álvarez Lache
 */

// Clase que representa los perfiles de usuario en el sistema
public class Perfiles {
    private int idPerfil;
    private String nombrePerfil;
    private String descripcionPerfil;
    
    // Constructor vacío que permite crear un objeto Perfiles sin inicializar sus atributos
    public Perfiles() {}
    
    // Constructor que permite crear un objeto Perfiles con los atributos inicializados
    public Perfiles (int idPerfil,
            String nombrePerfil,
            String descripcionPerfil) {
        
        this.idPerfil = idPerfil;
        this.nombrePerfil = nombrePerfil;
        this.descripcionPerfil = descripcionPerfil;
    }
    
    // Métodos getter y setter para acceder y modificar los atributos de la clase Perfiles
    public int getidPerfil() { return idPerfil; }
    public void setidPerfil (int idPerfil) { this.idPerfil = idPerfil; }
    
    public String getnombrePerfil() { return nombrePerfil; }
    public void setnombrePerfil (String nombrePerfil) { this.nombrePerfil = nombrePerfil; }
    
    public String getdescripcionPerfil() { return descripcionPerfil; }
    public void setdescripcionPerfil (String descripcionPerfil) { this.descripcionPerfil = descripcionPerfil; }    
}