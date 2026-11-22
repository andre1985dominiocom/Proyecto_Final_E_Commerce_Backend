package com.didistore.model;

import com.didistore.model.sales.enums.EstadoPedidos;
import com.didistore.model.marketing.enums.TipoDescuentos;
import com.didistore.model.marketing.enums.EstadoCupones;
import com.didistore.model.catalog.enums.EstadoResenas;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Regresión de enums del dominio (RF-12, RF-09, RF-21).
 */
class EnumsDominioTest {

    @Test
    void estadoPedidosTieneLosOchoValores() {
        assertEquals(8, EstadoPedidos.values().length);
        assertNotNull(EstadoPedidos.valueOf("Pendiente_Pago"));
        assertNotNull(EstadoPedidos.valueOf("En_Transito"));
        assertNotNull(EstadoPedidos.valueOf("Devuelto"));
        assertEquals("Entregado", EstadoPedidos.Entregado.name());
    }

    @Test
    void tipoDescuentos() {
        assertEquals(2, TipoDescuentos.values().length);
        assertNotNull(TipoDescuentos.valueOf("Porcentaje"));
        assertNotNull(TipoDescuentos.valueOf("MontoFijo"));
    }

    @Test
    void estadoCupones() {
        assertNotNull(EstadoCupones.Activo);
        assertNotNull(EstadoCupones.Inactivo);
    }

    @Test
    void estadoResenas() {
        assertEquals(3, EstadoResenas.values().length);
        assertNotNull(EstadoResenas.valueOf("Pendiente"));
        assertNotNull(EstadoResenas.valueOf("Aprobada"));
        assertNotNull(EstadoResenas.valueOf("Rechazada"));
    }
}
