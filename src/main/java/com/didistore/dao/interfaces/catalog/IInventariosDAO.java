
package com.didistore.dao.interfaces.catalog;

import com.didistore.model.catalog.Inventarios;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public interface IInventariosDAO {
    
    List<Inventarios> listarInventario();
    
    List<Inventarios> listarPorProducto(int productoId);
    
    Inventarios consultarInventarioPorId(int idInventario);
    
    void insertarInventario(Inventarios inventario);
    
    boolean actualizarInventario(Inventarios inventario);
    
    boolean eliminarInventario(int idInventario);
}