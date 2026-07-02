
package com.didistore.dao.interfaces.catalog;

import com.didistore.model.catalog.Categorias;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Interfaz para la gestión de categorías en el catálogo de productos.
// Define los métodos necesarios para insertar, actualizar, eliminar y obtener categorías,
// así como para buscar subcategorías y categorías raíz.
public interface ICategoriasDAO {
    
    boolean insertarCategorias(Categorias categoria);
    
    boolean actualizarCategorias(Categorias categoria);
    
    boolean eliminarCategorias(int idCategoria);
    
    Categorias obtenerCategoriaPorId(int idCategoria);
    
    Categorias buscarCategoriaPorNombre(String nombreCategoria);
    
    List<Categorias> obtenerTodasLasCategorias();
    
    List<Categorias> obtenerCategoriasRaiz();
    
    List<Categorias> obtenerSubcategorias(int categoriaPadreId);
}