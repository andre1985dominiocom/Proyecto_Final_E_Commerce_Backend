
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.SesionesDAOImpl;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class SesionesController {

    private final SesionesDAOImpl sesionesDAO = new SesionesDAOImpl();
    
    public boolean validarCredenciales(String email, String contrasena) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        if (contrasena == null || email.trim().isEmpty()) {
            return false;
        }
        
        return sesionesDAO.validarCredenciales(email, contrasena);
    }
}