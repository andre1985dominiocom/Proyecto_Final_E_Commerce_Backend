
package com.didistore.model.auth;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase que representa la relación entre un perfil y un permiso en el sistema de autenticación.
public class PerfilPermisos {
    private int idPerfil;
    private int idPermiso;
    
    // Constructor por defecto vacío, necesario para frameworks que requieren un constructor sin parámetros.
    public PerfilPermisos() {}
    
    // Constructor que inicializa los atributos idPerfil e idPermiso con los valores proporcionados.
    public PerfilPermisos (int id_Perfil,
            int id_Permiso) {
        
        this.idPerfil = id_Perfil; // Inicializa el atributo idPerfil con el valor proporcionado.
        this.idPermiso = id_Permiso; // Inicializa el atributo idPermiso con el valor proporcionado.
    }
    
    // Métodos getter y setter para acceder y modificar los atributos idPerfil e idPermiso.
    public int getidPerfil() { return idPerfil; }
    public void setidPerfil (int idPerfil) { this.idPerfil = idPerfil; }
    
    public int getidPermiso() { return idPermiso; }
    public void setidPermiso (int idPermiso) { this.idPermiso = idPermiso; }
}