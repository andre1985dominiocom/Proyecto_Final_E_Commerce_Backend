
package com.didistore.dao.interfaces.catalog;

import java.util.List;

import com.didistore.model.catalog.Resenas;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Interfaz para la gestión de reseñas en la base de datos
public interface IResenasDAO {

    boolean agregarResena(Resenas resena);

    boolean eliminarResena(int idResena);

    List<Resenas> obtenerResenasPorProducto(int productoId);

    List<Resenas> obtenerResenasPorUsuario(int usuarioId);

    List<Resenas> obtenerResenasPorEstado(String estado);

    boolean actualizarResena(int idResena, String nuevoComentario, int nuevaCalificacion);

    Resenas obtenerResenaPorId(int idResena);

    Resenas obtenerResenaPorUsuarioYProducto(int usuarioId, int productoId);

    Resenas consultarResena(int idResena);

    // RF-21: moderación — cambia estado (Pendiente/Aprobada/Rechazada).
    boolean actualizarEstadoResena(int idResena, String nuevoEstado);

    // RF-21: solo compradores con pedido Entregado pueden reseñar.
    boolean haCompradoEntregado(int usuarioId, int productoId);

    // RF-21: rating promedio de reseñas aprobadas.
    double promedioAprobadas(int productoId);

    int contarAprobadas(int productoId);
}