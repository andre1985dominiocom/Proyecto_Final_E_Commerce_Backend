package com.didistore.controller.catalog;

import com.didistore.model.catalog.Resenas;
import com.didistore.model.catalog.enums.EstadoResenas;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * RF-21: validación de reseñas sin BD (calificación, longitud, lenguaje,
 * moderación con IDs inválidos).
 */
class ResenasControllerTest {

    private final ResenasController controller = new ResenasController();

    private Resenas resenaValidaBase() {
        Resenas r = new Resenas();
        r.setusuarioId(1);
        r.setproductoId(10);
        r.setcalificacion(5);
        r.setcomentario("Excelente producto, llegó muy bien.");
        return r;
    }

    // ---------- registrarResena: validaciones previas a BD ----------
    @Test
    void resenaNulaRechazada() {
        assertNotNull(controller.registrarResena(null));
    }

    @Test
    void calificacionFueraDeRangoRechazada() {
        Resenas baja = resenaValidaBase();
        baja.setcalificacion(0);
        assertNotNull(controller.registrarResena(baja));

        Resenas alta = resenaValidaBase();
        alta.setcalificacion(6);
        assertNotNull(controller.registrarResena(alta));

        Resenas negativa = resenaValidaBase();
        negativa.setcalificacion(-1);
        assertNotNull(controller.registrarResena(negativa));
    }

    @Test
    void comentarioCortoRechazado() {
        Resenas r = resenaValidaBase();
        r.setcomentario("corto");
        String err = controller.registrarResena(r);
        assertNotNull(err);
        assertTrue(err.contains("10"));
    }

    @Test
    void comentarioLargoRechazado() {
        Resenas r = resenaValidaBase();
        r.setcomentario("x".repeat(501));
        assertNotNull(controller.registrarResena(r));
    }

    @Test
    void comentarioConPalabrasProhibidasRechazado() {
        Resenas r = resenaValidaBase();
        r.setcomentario("Esto es una estafa clara del vendedor");
        String err = controller.registrarResena(r);
        assertNotNull(err);
        assertTrue(err.toLowerCase().contains("inapropiado")
                || err.toLowerCase().contains("estafa"));
    }

    @Test
    void comentarioNuloRechazado() {
        Resenas r = resenaValidaBase();
        r.setcomentario(null);
        assertNotNull(controller.registrarResena(r));
    }

    // ---------- moderarResena ----------
    @Test
    void moderarIdInvalidoRechazado() {
        assertFalse(controller.moderarResena(0, EstadoResenas.Aprobada));
        assertFalse(controller.moderarResena(-1, EstadoResenas.Rechazada));
    }

    @Test
    void moderarEstadoNuloRechazado() {
        assertFalse(controller.moderarResena(1, null));
    }

    @Test
    void moderarHaciaPendienteRechazado() {
        // La moderación solo aprueba/rechaza; no debe volver a Pendiente
        assertFalse(controller.moderarResena(1, EstadoResenas.Pendiente));
    }

    // ---------- modificarResenaPropia: validaciones previas ----------
    @Test
    void editarCalificacionInvalidaRechazada() {
        assertFalse(controller.modificarResenaPropia(1, 1, "Comentario válido aquí", 0));
        assertFalse(controller.modificarResenaPropia(1, 1, "Comentario válido aquí", 6));
        assertFalse(controller.modificarResenaPropia(0, 1, "Comentario válido aquí", 5));
    }

    @Test
    void editarComentarioCortoRechazado() {
        assertFalse(controller.modificarResenaPropia(1, 1, "corto", 4));
    }

    @Test
    void editarComentarioConFiltroRechazado() {
        assertFalse(controller.modificarResenaPropia(
                1, 1, "Comentario con basura ofensiva dentro", 3));
    }

    // ---------- eliminarResena ----------
    @Test
    void eliminarIdInvalidoRechazado() {
        assertFalse(controller.eliminarResena(0));
        assertFalse(controller.eliminarResena(-3));
    }

    // ---------- puedeResenar ----------
    @Test
    void puedeResenarIdsInvalidosFalso() {
        assertFalse(controller.puedeResenar(0, 1));
        assertFalse(controller.puedeResenar(1, 0));
        assertFalse(controller.puedeResenar(-1, -1));
    }

    // ---------- consultar ----------
    @Test
    void consultarPorProductoInvalidoDevuelveNull() {
        assertNull(controller.consultarResenasPorProducto(0));
        assertNull(controller.consultarResenasPorProducto(-5));
    }
}
