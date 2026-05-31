
package com.didistore.controller.catalog;

import com.didistore.dao.impl.catalog.ResenasDAOImpl;
import com.didistore.dao.interfaces.catalog.IResenasDAO;
import com.didistore.model.catalog.Resenas;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class ResenasController {
    
    private final IResenasDAO resenasDAO;

    public ResenasController() {
        this.resenasDAO = new ResenasDAOImpl();
    }

    public boolean registrarResena(Resenas resena) {
        if (resena == null) return false;
        
        if (resena.getcalificacion() < 1 || resena.getcalificacion() > 5) {
            System.out.println("Controlador: Calificación inválida (Debe ser entre 1 y 5)");
            return false;
        }

        resenasDAO.agregarResena(resena);
        return true;
    }

    public List<Resenas> consultarResenasPorProducto(int productoId) {
        if (productoId <= 0) return null;
        return resenasDAO.obtenerResenasPorProducto(productoId);
    }

    public boolean modificarResena(int idResena, String comentario, int calificacion) {
        if (idResena <= 0 || comentario == null || comentario.trim().isEmpty()) {
            return false;
        }
        if (calificacion < 1 || calificacion > 5) return false;

        return resenasDAO.actualizarResena(idResena, comentario, calificacion);
    }

    public boolean eliminarResena(int idResena) {
        if (idResena <= 0) return false;
        return resenasDAO.eliminarResena(idResena);
    }
}