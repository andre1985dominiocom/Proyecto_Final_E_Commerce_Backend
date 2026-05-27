
package com.didistore.controller.admin;

import com.didistore.dao.impl.catalog.CategoriasDAOImpl;
import com.didistore.dao.interfaces.catalog.ICategoriasDAO;
import com.didistore.model.catalog.Categorias;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class AdminCategoriasController {
    
    private final ICategoriasDAO categoriasDAO;

    public AdminCategoriasController() {
        this.categoriasDAO = new CategoriasDAOImpl();
    }

    public void insertarCategorias(Categorias categoria) {
        categoriasDAO.insertarCategorias(categoria);
    }

    public List<Categorias> listarCategorias() {
        return categoriasDAO.obtenerTodasLasCategorias();
    }

    public Categorias consultarCategoriaPorId(int idCategoria) {
        return categoriasDAO.obtenerCategoriaPorId(idCategoria);
    }

    public Categorias buscarcategoriasPorNombre(String nombreCategoria) {
        return categoriasDAO.buscarCategoriaPorNombre(nombreCategoria);
    }

    public void actualizarCategorias(Categorias categoria) {
        categoriasDAO.actualizarCategorias(categoria);
    }

    public void eliminarCategorias(int idCategoria) {
        categoriasDAO.eliminarCategorias(idCategoria);
    }
}