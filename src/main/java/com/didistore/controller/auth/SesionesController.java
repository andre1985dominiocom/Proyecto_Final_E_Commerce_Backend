
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

    public boolean validarCredenciales(String email, String contrasenaPlana) {
        
        String hashBD = sesionesDAO.obtenerContrasenaHasheadaPorEmail(email);
        
        if (hashBD == null) {
            return false;
    }
    return com.didistore.util.PasswordUtil.verify(contrasenaPlana, hashBD);
    }
}