
package com.didistore.dao.moduleinventoryfeedback;

import com.didistore.model.moduleinventoryfeedback.Productos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public interface IProductosDAO {
    void insertarProducto(Productos producto);
    
    List<Productos> listar();
    
    void actualizarProductos(Productos producto);
    
    void eliminarProductos(int idProducto);
}
