
package com.didistore.dao.interfaces.catalog;

import com.didistore.model.catalog.Categorias;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public interface ICategoriasDAO {
    
    void insertarCategorias(Categorias categoria);
    
    List<Categorias> listar();
    
    Categorias consultarCategoriasPorId(int idCategoria);
    
    void actualizarCategoria(Categorias categoria);
    
    void eliminarCategorias(int idCategoria);
}