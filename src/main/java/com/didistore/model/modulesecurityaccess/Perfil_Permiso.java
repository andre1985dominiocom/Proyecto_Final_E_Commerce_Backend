
package com.didistore.model.modulesecurityaccess;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Perfil_Permiso {
    private int id_Perfil;
    private int id_Permiso;
    
    public Perfil_Permiso() {}
    
    public Perfil_Permiso (int id_Perfil,
            int id_Permiso) {
        
        this.id_Perfil = id_Perfil;
        this.id_Permiso = id_Permiso;
    }
    
    public int getid_Perfil() { return id_Perfil; }
    public void setid_Perfil (int id_Perfil) { this.id_Perfil = id_Perfil; }
    
    public int getid_Permiso() { return id_Permiso; }
    public void setid_Permiso (int id_Permiso) { this.id_Permiso = id_Permiso; }    
}