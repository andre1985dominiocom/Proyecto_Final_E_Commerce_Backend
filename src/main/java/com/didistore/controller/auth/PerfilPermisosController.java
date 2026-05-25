
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.PerfilPermisosDAOImpl;
import com.didistore.model.auth.PerfilPermisos;
import com.didistore.dao.interfaces.auth.IPerfilPermisosDAO;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PerfilPermisosController {

     private final IPerfilPermisosDAO perfilPermisosDAO;

    public PerfilPermisosController() {
        this.perfilPermisosDAO = new PerfilPermisosDAOImpl();
    }

    public boolean asignarPermisoAPerfil(int idPerfil, int idPermiso) {
        if (idPerfil <= 0 || idPermiso <= 0) {
            return false;
        }

        PerfilPermisos perfilPermiso = new PerfilPermisos();
        perfilPermiso.setidPerfil(idPerfil);
        perfilPermiso.setidPermiso(idPermiso);

        return perfilPermisosDAO.insertarPerfilPermisos(perfilPermiso);
    }

    public List<PerfilPermisos> listarPermisosPorPerfil(int idPerfil) {
        return perfilPermisosDAO.listarPermisosPorPerfil(idPerfil);
    }

    public List<PerfilPermisos> listarPerfilesPorPermiso(int idPermiso) {
        return perfilPermisosDAO.listarPerfilesPorPermiso(idPermiso);
    }

    public PerfilPermisos consultarPerfilPermiso(int idPerfil, int idPermiso) {
        return perfilPermisosDAO.consultarPerfilPermisosPorId(idPerfil, idPermiso);
    }

    public void actualizarPerfilPermiso(PerfilPermisos perfilPermiso) {
        perfilPermisosDAO.actualizarPerfilPermisos(perfilPermiso);
    }

    public void eliminarPerfilPermiso(int idPerfil, int idPermiso) {
        perfilPermisosDAO.eliminarPerfilPermisos(idPerfil, idPermiso);
    }
}
