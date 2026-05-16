
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.PerfilPermisosDAOImpl;
import com.didistore.dao.interfaces.auth.IPerfilPermisos;
import com.didistore.model.auth.PerfilPermisos;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PerfilPermisosController {

    public static void main(String[] args) {
        
        IPerfilPermisos perfilPermisoDAO = new PerfilPermisosDAOImpl();
        PerfilPermisos nuevoPerfilPermiso = new PerfilPermisos();
        
        nuevoPerfilPermiso.setidPerfil(0);
        nuevoPerfilPermiso.setidPermiso(0);
        
        System.out.println("Intentado registrar perfiles permisos en MySQL... ");
        perfilPermisoDAO.insertarPerfilPermisos(nuevoPerfilPermiso);                
        System.out.println("¡Proceso de inserción finalizado con éxito!");
    }
    
}
