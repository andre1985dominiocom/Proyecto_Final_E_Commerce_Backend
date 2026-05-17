
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.TokensRecuperacioDAOImpl;
import com.didistore.dao.interfaces.auth.ITokensRecuperacionDAO;
import com.didistore.model.auth.TokensRecuperacion;
import java.sql.Timestamp;


/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class TokensRecuperacionController {

    public static void main(String[] args) {
        
        ITokensRecuperacionDAO tokenDAO = new TokensRecuperacioDAOImpl();
        
        TokensRecuperacion nuevoToken = new TokensRecuperacion();
        
        nuevoToken.setusuarioId(0);
        nuevoToken.settokenHash("");
        Timestamp fechaActual = new Timestamp(System.currentTimeMillis());
        nuevoToken.setfechaCreacion(fechaActual);
        nuevoToken.setfechaExpiracion(fechaActual);
        nuevoToken.setusado(false);
        nuevoToken.setintentos(0);
       
        System.out.println("Intentado registrar token en MySQL... ");
        tokenDAO.insertarToken(nuevoToken);
        System.out.println("¡Proceso de inserción finalizado con éxito!");        
    }   
}