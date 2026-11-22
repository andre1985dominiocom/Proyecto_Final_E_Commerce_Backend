
package com.didistore.controller.catalog;

import com.didistore.dao.impl.catalog.ResenasDAOImpl;
import com.didistore.dao.interfaces.catalog.IResenasDAO;
import com.didistore.model.catalog.Resenas;
import com.didistore.model.catalog.enums.EstadoResenas;
import java.util.List;

// Controlador para manejar las operaciones relacionadas con las reseñas de productos.
// RF-21: solo compradores verificados, una reseña por producto, moderación previa.
public class ResenasController {

    private static final String[] PALABRAS_PROHIBIDAS = {
        "estafa", "spam", "basura", "mierda", "idiota", "imbécil", "imbecil",
        "hackeo", "hack", "virus", "malísimo", "malisimo"
    };

    private final IResenasDAO resenasDAO;

    public ResenasController() {
        this.resenasDAO = new ResenasDAOImpl();
    }

    // Devuelve null si OK, o un mensaje de error.
    public String registrarResena(Resenas resena) {
        if (resena == null) return "Datos de reseña inválidos";

        if (resena.getcalificacion() < 1 || resena.getcalificacion() > 5) {
            return "La calificación debe estar entre 1 y 5";
        }

        String comentario = resena.getcomentario() != null ? resena.getcomentario().trim() : "";
        if (comentario.length() < 10 || comentario.length() > 500) {
            return "El comentario debe tener entre 10 y 500 caracteres";
        }
        resena.setcomentario(comentario);

        String filtro = filtroLenguaje(comentario);
        if (filtro != null) {
            return "La reseña contiene contenido inapropiado: " + filtro;
        }

        // RF-21: solo clientes con pedido Entregado
        if (!resenasDAO.haCompradoEntregado(resena.getusuarioId(), resena.getproductoId())) {
            return "Solo clientes verificados que han comprado este producto pueden dejar reseñas";
        }

        // Una reseña por producto
        if (resenasDAO.obtenerResenaPorUsuarioYProducto(
                resena.getusuarioId(), resena.getproductoId()) != null) {
            return "Ya has reseñado este producto. Puedes editar tu reseña existente";
        }

        resena.setestado(EstadoResenas.Pendiente);
        boolean ok = resenasDAO.agregarResena(resena);
        return ok ? null : "No se pudo registrar la reseña (¿ya la has dejado antes?)";
    }

    public List<Resenas> consultarResenasPorProducto(int productoId) {
        if (productoId <= 0) return null;
        return resenasDAO.obtenerResenasPorProducto(productoId);
    }

    public List<Resenas> consultarResenasPorEstado(String estado) {
        return resenasDAO.obtenerResenasPorEstado(estado);
    }

    public boolean moderarResena(int idResena, EstadoResenas nuevoEstado) {
        if (idResena <= 0 || nuevoEstado == null) return false;
        if (nuevoEstado == EstadoResenas.Pendiente) return false;
        return resenasDAO.actualizarEstadoResena(idResena, nuevoEstado.name());
    }

    // Editar reseña propia → vuelve a Pendiente (re-moderación).
    public boolean modificarResenaPropia(int idResena, int usuarioId, String comentario, int calificacion) {
        if (idResena <= 0 || calificacion < 1 || calificacion > 5) return false;
        String c = comentario != null ? comentario.trim() : "";
        if (c.length() < 10 || c.length() > 500) return false;
        if (filtroLenguaje(c) != null) return false;

        Resenas existente = resenasDAO.obtenerResenaPorId(idResena);
        if (existente == null || existente.getusuarioId() != usuarioId) return false;

        if (!resenasDAO.actualizarResena(idResena, c, calificacion)) return false;
        return resenasDAO.actualizarEstadoResena(idResena, EstadoResenas.Pendiente.name())
                || existente.getestado() == EstadoResenas.Pendiente;
    }

    public boolean eliminarResena(int idResena) {
        if (idResena <= 0) return false;
        return resenasDAO.eliminarResena(idResena);
    }

    public boolean puedeResenar(int usuarioId, int productoId) {
        if (usuarioId <= 0 || productoId <= 0) return false;
        if (resenasDAO.obtenerResenaPorUsuarioYProducto(usuarioId, productoId) != null) {
            return false;
        }
        return resenasDAO.haCompradoEntregado(usuarioId, productoId);
    }

    public double promedioAprobadas(int productoId) {
        return resenasDAO.promedioAprobadas(productoId);
    }

    public int contarAprobadas(int productoId) {
        return resenasDAO.contarAprobadas(productoId);
    }

    private String filtroLenguaje(String texto) {
        String bajo = texto.toLowerCase();
        for (String p : PALABRAS_PROHIBIDAS) {
            if (bajo.contains(p)) return p;
        }
        return null;
    }
}
