
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.TokensRecuperacionDAOImpl;
import com.didistore.dao.interfaces.auth.ITokensRecuperacionDAO;
import com.didistore.model.auth.TokensRecuperacion;
import java.sql.Timestamp;
import java.util.UUID;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class TokensRecuperacionController {

    private final ITokensRecuperacionDAO tokensDAO;
    
    public TokensRecuperacionController() {
        this.tokensDAO = new TokensRecuperacionDAOImpl();
    }
    
    public String generarToken() {
        return UUID.randomUUID().toString();
    }
    
    public TokensRecuperacion crearTokenParaUsuario(int usuarioId, Timestamp fechaExpiracion) {
        
        TokensRecuperacion token = new TokensRecuperacion();
        
        token.setusuarioId(usuarioId);
        token.settokenHash(generarToken());
        token.setfechaCreacion(new Timestamp(System.currentTimeMillis()));
        token.setfechaExpiracion(fechaExpiracion);
        token.setusado(false);
        token.setintentos(0);
        
        tokensDAO.insertarToken(token);

        return token;
    }
    
    public TokensRecuperacion consultarTokenPorHash(String tokenHash) {
        if (tokenHash == null || tokenHash.trim().isEmpty()) {
            return null;
        }

        return tokensDAO.consultarTokenPorHash(tokenHash);
    }
    
    
    
    public boolean tokenEsValido(String tokenHash) {
        TokensRecuperacion token = consultarTokenPorHash(tokenHash);
        
        if (token == null) {
            return false;
        }
        
        if (token.getusado()) {
            return false;
        } 
       
        Timestamp ahora = new Timestamp(System.currentTimeMillis());
        
        return token.getfechaExpiracion() != null && token.getfechaExpiracion().after(ahora);
    }
        
        public void marcarTokenComoUsado(String tokenHash) {
        
            TokensRecuperacion token = consultarTokenPorHash(tokenHash);

            if (token != null) {
            token.setusado(true);
            tokensDAO.actualizarTokens(token);
        }
    }
}