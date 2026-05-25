
package com.didistore.dao.interfaces.auth;

import com.didistore.model.auth.Perfiles;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public interface IPerfilesDAO {
    
    void insertarPerfiles(Perfiles perfil);
    
    List<Perfiles> listarPerfiles();
    
    Perfiles consultarPerfiles(int idPerfil);
    
    void actualizarPerfiles(Perfiles perfil);
    
    void eliminarPerfiles(int idperfil);
}