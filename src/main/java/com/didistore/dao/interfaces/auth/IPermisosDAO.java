
package com.didistore.dao.interfaces.auth;

import com.didistore.model.auth.Permisos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public interface IPermisosDAO {
    
    void insertarPermisos(Permisos permiso);
    
    List<Permisos> listarPermisos();
    
    Permisos consultarPermisos(int idPermiso);
    
    void actualizarPermisos(Permisos permiso);
    
    void eliminarPermisos(int idPermiso );  
}