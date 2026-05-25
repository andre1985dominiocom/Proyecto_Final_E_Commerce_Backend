
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.PerfilesDAOImpl;
import com.didistore.dao.interfaces.auth.IPerfilesDAO;
import com.didistore.model.auth.Perfiles;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PerfilesController {

     private final IPerfilesDAO perfilesDAO;

    public PerfilesController() {
        this.perfilesDAO = new PerfilesDAOImpl();
    }

    public void agregarPerfil(Perfiles perfil) {
        if (perfil == null) {
            throw new IllegalArgumentException("El perfil no puede ser null");
        }
        perfilesDAO.insertarPerfiles(perfil);
    }

    public Perfiles consultarPerfilPorId(int perfilId) {
        if (perfilId <= 0) {
            return null;
        }
        return perfilesDAO.consultarPerfiles(perfilId);
    }

    public List<Perfiles> listarPerfiles() {
        return perfilesDAO.listarPerfiles();
    }

    public void actualizarPerfil(Perfiles perfil) {
        if (perfil != null) {
            perfilesDAO.actualizarPerfiles(perfil);
        }
    }

    public void eliminarPerfil(int perfilId) {
        if (perfilId > 0) {
            perfilesDAO.eliminarPerfiles(perfilId);
        }
    }
}                              