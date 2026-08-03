
package com.didistore.dao.interfaces.catalog;

import java.util.List;

import com.didistore.model.catalog.Resenas;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Interfaz para la gestión de reseñas en la base de datos
public interface IResenasDAO {

    void agregarResena(Resenas resena);

    boolean eliminarResena(int idResena);

    List<Resenas> obtenerResenasPorProducto(int productoId);

    List<Resenas> obtenerResenasPorUsuario(int usuarioId);

    List<Resenas> obtenerResenasPorEstado(String estado);

    boolean actualizarResena(int idResena, String nuevoComentario, int nuevaCalificacion);

    Resenas obtenerResenaPorId(int idResena);

    Resenas obtenerResenaPorUsuarioYProducto(int usuarioId, int productoId);

    Resenas consultarResena(int idResena);
}