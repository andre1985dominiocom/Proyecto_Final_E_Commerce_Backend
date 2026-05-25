
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.SesionesDAOImpl;
import com.didistore.dao.interfaces.auth.ISesionesDAO;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class SesionesController {
    
    private final ISesionesDAO sesionesDAO;
    
    public SesionesController() {
        this.sesionesDAO = new SesionesDAOImpl();
    }

    public boolean validarCredenciales(String email, String contrasena) {
        if (email == null || email.trim().isEmpty()
                || contrasena == null || contrasena.trim().isEmpty()) {
            return false;
        }

        return sesionesDAO.validarCredenciales(email, contrasena);
    }
}