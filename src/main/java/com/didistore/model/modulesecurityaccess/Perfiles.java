
package com.didistore.model.modulesecurityaccess;

/**
 *
 * @author Sergio Andrés álvarez Lache
 */
public class Perfiles {    
    private int id_Perfil;
    private String nombre_Perfil;
    private String descripcion_Perfil;
    
    public Perfiles() {}
    
    public Perfiles (int id_Perfil,
            String nombre_Perfil,
            String descripcion_Perfil) {
        
        this.id_Perfil = id_Perfil;
        this.nombre_Perfil = nombre_Perfil;
        this.descripcion_Perfil = descripcion_Perfil;
    }
    
    public int getid_Perfil() { return id_Perfil; }
    public void setid_Perfil (int id_Perfil) { this.id_Perfil = id_Perfil; }
    
    public String getnombre_Perfil() { return nombre_Perfil; }
    public void setnombre_Perfil (String nombre_Perfil) { this.nombre_Perfil = nombre_Perfil; }
    
    public String getdescripcion_Perfil() { return descripcion_Perfil; }
    public void setdescripcion_Perfil (String descripcion_Perfil) { this.descripcion_Perfil = descripcion_Perfil; }    
}