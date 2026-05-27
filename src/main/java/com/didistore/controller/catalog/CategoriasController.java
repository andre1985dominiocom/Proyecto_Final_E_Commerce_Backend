
package com.didistore.controller.catalog;

import com.didistore.dao.impl.catalog.CategoriasDAOImpl;
import com.didistore.dao.interfaces.catalog.ICategoriasDAO;
import com.didistore.model.catalog.Categorias;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class CategoriasController  {
    
    private final ICategoriasDAO categoriasDAO;

    public CategoriasController() {
        this.categoriasDAO = new CategoriasDAOImpl();
    }

    public List<Categorias> listarCategorias() {
        return categoriasDAO.obtenerTodasLasCategorias();
    }

    public Categorias obtenerCategoriaPorId(int idCategoria) {
        if (idCategoria <= 0) {
            return null;
        }
        return categoriasDAO.obtenerCategoriaPorId(idCategoria);
    }

    public List<Categorias> obtenerCategoriaRaiz() {
        return categoriasDAO.obtenerCategoriasRaiz();
    }

    public List<Categorias> obtenerSubcategorias(int categoriaPadreId) {
        if (categoriaPadreId <= 0) {
            return null;
        }
        return categoriasDAO.obtenerSubcategorias(categoriaPadreId);
    }
    
    public Categorias buscarCategoriaPorNombre(String nombreCategoria) {
        if (nombreCategoria == null || nombreCategoria.trim().isEmpty()) {
            return null;
        }
        return categoriasDAO.buscarCategoriaPorNombre(nombreCategoria.trim());
    }
}