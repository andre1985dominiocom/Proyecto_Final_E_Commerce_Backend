
package com.didistore.controller.catalog;

import com.didistore.dao.impl.catalog.ImagenesProductosDAOImpl;
import com.didistore.dao.interfaces.catalog.IImagenesProductosDAO;
import com.didistore.model.catalog.ImagenesProductos;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Controlador para manejar las operaciones relacionadas con las imágenes de productos.
public class ImagenesProductosController {
    
    private final IImagenesProductosDAO imagenesDAO;

    public ImagenesProductosController() {
        this.imagenesDAO = new ImagenesProductosDAOImpl();
    }

    public List<ImagenesProductos> listarImagenes() {
        return imagenesDAO.listarImagenes();
    }

    public List<ImagenesProductos> listarPorProducto(int productoId) {
        if (productoId <= 0) {
            return new ArrayList<>();
        }
        return imagenesDAO.listarPorProducto(productoId);
    }

    public ImagenesProductos consultarImagenPorId(int idImagen) {
        if (idImagen <= 0) {
            return null;
        }
        return imagenesDAO.consultarImagenPorId(idImagen);
    }

    public boolean insertarImagen(ImagenesProductos imagen) {
        if (imagen == null) {
            return false;
        }

        if (imagen.getProductoId() <= 0) {
            return false;
        }

        if (imagen.getUrl() == null || imagen.getUrl().trim().isEmpty()) {
            return false;
        }

        if (imagen.getFormato() == null || imagen.getFormato().trim().isEmpty()) {
            return false;
        }

        return imagenesDAO.insertarImagen(imagen);
    }

    public boolean actualizarImagen(ImagenesProductos imagen) {
        if (imagen == null) {
            return false;
        }

        if (imagen.getIdImagen() <= 0) {
            return false;
        }

        if (imagen.getProductoId() <= 0) {
            return false;
        }

        if (imagen.getUrl() == null || imagen.getUrl().trim().isEmpty()) {
            return false;
        }

        if (imagen.getFormato() == null || imagen.getFormato().trim().isEmpty()) {
            return false;
        }

        return imagenesDAO.actualizarImagen(imagen);
    }

    public boolean eliminarImagen(int idImagen) {
        if (idImagen <= 0) {
            return false;
        }
        return imagenesDAO.eliminarImagen(idImagen);
    }
}