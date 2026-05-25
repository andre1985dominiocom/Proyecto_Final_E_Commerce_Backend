
package com.didistore.dao.interfaces.auth;

import com.didistore.model.auth.TokensRecuperacion;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public interface ITokensRecuperacionDAO {
    
    void insertarToken(TokensRecuperacion token);
    
    TokensRecuperacion consultarTokenPorHash(String tokenHash);
    
    TokensRecuperacion consultarTokenPorUsuario (int usuarioId);
    
    List<TokensRecuperacion> listarToken();
        
    void actualizarTokens(TokensRecuperacion token);
    
    void eliminarTokensPorUsuario(int usuarioId);
    
    void eliminarTokenPorId(int idToken);
}
