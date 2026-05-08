
package com.didistore.model.modulesecurityaccess;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PerfilPermiso {
    private int idPerfil;
    private int idPermiso;
    
    public PerfilPermiso() {}
    
    public PerfilPermiso (int id_Perfil,
            int id_Permiso) {
        
        this.idPerfil = id_Perfil;
        this.idPermiso = id_Permiso;
    }
    
    public int getidPerfil() { return idPerfil; }
    public void setidPerfil (int idPerfil) { this.idPerfil = idPerfil; }
    
    public int getidPermiso() { return idPermiso; }
    public void setidPermiso (int idPermiso) { this.idPermiso = idPermiso; }    
}