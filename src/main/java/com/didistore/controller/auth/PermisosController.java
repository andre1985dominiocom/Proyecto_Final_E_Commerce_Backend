
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.PermisosDAOImpl;
import com.didistore.dao.interfaces.auth.IPermisosDAO;
import com.didistore.model.auth.Permisos;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PermisosController {

    public static void main(String[] args) {
        
        IPermisosDAO permisoDAO = new PermisosDAOImpl();
        
        Permisos nuevoPermiso = new Permisos();
        
        nuevoPermiso.setnombrePermiso("");
        nuevoPermiso.setdescripcionPermiso("");
        
        System.out.println("Intentado registrar permiso en MySQL... ");
        permisoDAO.insertarPermisos(nuevoPermiso);
        System.out.println("¡Proceso de inserción finalizado con éxito!");
    }
}