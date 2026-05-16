
package com.didistore.dao.interfaces.auth;

import com.didistore.model.auth.PerfilPermisos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public interface IPerfilPermisos {
    
    boolean insertarPerfilPermisos(PerfilPermisos perfilPermiso);
    
    List<PerfilPermisos> listarPermisosPorPerfil(int idPerfil);
    
    List<PerfilPermisos> listarPerfilesPorPermiso(int idPermiso);
    
    void eliminarPerfilPermisos(int idPerfil, int idPermiso);    
}