
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.PermisosDAOImpl;
import com.didistore.dao.interfaces.auth.IPermisosDAO;
import com.didistore.model.auth.Permisos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

public class PermisosController {
    
    private final IPermisosDAO permisosDAO;

    public PermisosController() {
        this.permisosDAO = new PermisosDAOImpl();
    }

    public void agregarPermiso(Permisos permiso) {
        if (permiso == null) {
            throw new IllegalArgumentException("El permiso no puede ser null");
        }
        permisosDAO.insertarPermisos(permiso);
    }

    public Permisos consultarPermisoPorId(int permisoId) {
        if (permisoId <= 0) {
            return null;
        }
        return permisosDAO.consultarPermisos(permisoId);
    }

    public List<Permisos> listarPermisos() {
        return permisosDAO.listarPermisos();
    }

    public void actualizarPermiso(Permisos permiso) {
        if (permiso != null) {
            permisosDAO.actualizarPermisos(permiso);
        }
    }

    public void eliminarPermiso(int permisoId) {
        if (permisoId > 0) {
            permisosDAO.eliminarPermisos(permisoId);
        }
    }       
}