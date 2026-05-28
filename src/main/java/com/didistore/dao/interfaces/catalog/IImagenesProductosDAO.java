
package com.didistore.dao.interfaces.catalog;

import com.didistore.model.catalog.ImagenesProductos;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public interface IImagenesProductosDAO {
    
    List<ImagenesProductos> listarImagenes();
    
    List<ImagenesProductos> listarPorProducto(int productoId);
    
     ImagenesProductos consultarImagenPorId(int idImagen);
    
    boolean insertarImagen(ImagenesProductos imagen);
    
    boolean actualizarImagen(ImagenesProductos imagen);
    
    boolean eliminarImagen(int idImagen);
}