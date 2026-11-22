package com.didistore.util;

import com.didistore.model.sales.enums.EstadoPedidos;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * RF-12: máquina de estados del pedido — transiciones válidas/inválidas,
 * cancelación cliente/staff y estados terminales.
 */
class EstadoPedidoMaquinaTest {

    // ---------- Flujo principal feliz ----------
    @Test
    void flujoPrincipalPermitido() {
        assertTrue(EstadoPedidoMaquina.esValida(
                EstadoPedidos.Pendiente_Pago, EstadoPedidos.Pagado));
        assertTrue(EstadoPedidoMaquina.esValida(
                EstadoPedidos.Pagado, EstadoPedidos.En_Preparacion));
        assertTrue(EstadoPedidoMaquina.esValida(
                EstadoPedidos.En_Preparacion, EstadoPedidos.Despachado));
        assertTrue(EstadoPedidoMaquina.esValida(
                EstadoPedidos.Despachado, EstadoPedidos.En_Transito));
        assertTrue(EstadoPedidoMaquina.esValida(
                EstadoPedidos.En_Transito, EstadoPedidos.Entregado));
        assertTrue(EstadoPedidoMaquina.esValida(
                EstadoPedidos.Entregado, EstadoPedidos.Devuelto));
    }

    // ---------- Transiciones inválidas ----------
    @Test
    void noPermiteSaltarEstados() {
        assertFalse(EstadoPedidoMaquina.esValida(
                EstadoPedidos.Pendiente_Pago, EstadoPedidos.Entregado));
        assertFalse(EstadoPedidoMaquina.esValida(
                EstadoPedidos.Pagado, EstadoPedidos.Despachado));
        assertFalse(EstadoPedidoMaquina.esValida(
                EstadoPedidos.En_Preparacion, EstadoPedidos.Entregado));
    }

    @Test
    void noPermiteTransicionAlMismoEstado() {
        assertFalse(EstadoPedidoMaquina.esValida(
                EstadoPedidos.Pagado, EstadoPedidos.Pagado));
        assertFalse(EstadoPedidoMaquina.esValida(
                EstadoPedidos.Cancelado, EstadoPedidos.Cancelado));
    }

    @Test
    void estadosNulosSonInvalidos() {
        assertFalse(EstadoPedidoMaquina.esValida(null, EstadoPedidos.Pagado));
        assertFalse(EstadoPedidoMaquina.esValida(EstadoPedidos.Pagado, null));
        assertFalse(EstadoPedidoMaquina.esValida(null, null));
        assertTrue(EstadoPedidoMaquina.siguientesPermitidos(null).isEmpty());
    }

    // ---------- Estados terminales ----------
    @Test
    void estadosTerminalesNoTienenSalidas() {
        assertTrue(EstadoPedidoMaquina.esTerminal(EstadoPedidos.Cancelado));
        assertTrue(EstadoPedidoMaquina.esTerminal(EstadoPedidos.Devuelto));
        assertFalse(EstadoPedidoMaquina.esTerminal(EstadoPedidos.Pendiente_Pago));
        assertFalse(EstadoPedidoMaquina.esTerminal(EstadoPedidos.Entregado));

        assertTrue(EstadoPedidoMaquina.siguientesPermitidos(
                EstadoPedidos.Cancelado).isEmpty());
        assertTrue(EstadoPedidoMaquina.siguientesPermitidos(
                EstadoPedidos.Devuelto).isEmpty());
    }

    @Test
    void desdeTerminalNoHayTransiciones() {
        assertFalse(EstadoPedidoMaquina.esValida(
                EstadoPedidos.Cancelado, EstadoPedidos.Pagado));
        assertFalse(EstadoPedidoMaquina.esValida(
                EstadoPedidos.Devuelto, EstadoPedidos.Entregado));
    }

    // ---------- Cancelación ----------
    @Test
    void clienteSoloCancelaPendientePago() {
        assertTrue(EstadoPedidoMaquina.clientePuedeCancelar(EstadoPedidos.Pendiente_Pago));
        assertFalse(EstadoPedidoMaquina.clientePuedeCancelar(EstadoPedidos.Pagado));
        assertFalse(EstadoPedidoMaquina.clientePuedeCancelar(EstadoPedidos.En_Preparacion));
        assertFalse(EstadoPedidoMaquina.clientePuedeCancelar(EstadoPedidos.Despachado));
        assertFalse(EstadoPedidoMaquina.clientePuedeCancelar(EstadoPedidos.Entregado));
        assertFalse(EstadoPedidoMaquina.clientePuedeCancelar(null));
    }

    @Test
    void staffCancelaHastaDespachado() {
        assertTrue(EstadoPedidoMaquina.staffPuedeCancelar(EstadoPedidos.Pagado));
        assertTrue(EstadoPedidoMaquina.staffPuedeCancelar(EstadoPedidos.En_Preparacion));
        assertTrue(EstadoPedidoMaquina.staffPuedeCancelar(EstadoPedidos.Despachado));
        assertFalse(EstadoPedidoMaquina.staffPuedeCancelar(EstadoPedidos.En_Transito));
        assertFalse(EstadoPedidoMaquina.staffPuedeCancelar(EstadoPedidos.Entregado));
        assertFalse(EstadoPedidoMaquina.staffPuedeCancelar(EstadoPedidos.Cancelado));
        assertFalse(EstadoPedidoMaquina.staffPuedeCancelar(null));
    }

    @Test
    void cancelacionValidaDesdeFlujoNormal() {
        assertTrue(EstadoPedidoMaquina.esValida(
                EstadoPedidos.Pendiente_Pago, EstadoPedidos.Cancelado));
        assertTrue(EstadoPedidoMaquina.esValida(
                EstadoPedidos.Pagado, EstadoPedidos.Cancelado));
        assertTrue(EstadoPedidoMaquina.esValida(
                EstadoPedidos.En_Preparacion, EstadoPedidos.Cancelado));
        assertTrue(EstadoPedidoMaquina.esValida(
                EstadoPedidos.Despachado, EstadoPedidos.Cancelado));
        assertTrue(EstadoPedidoMaquina.esValida(
                EstadoPedidos.En_Transito, EstadoPedidos.Cancelado));
    }

    // ---------- Conjuntos de siguientes ----------
    @Test
    void siguientesPermitidosPagado() {
        Set<EstadoPedidos> siguientes =
                EstadoPedidoMaquina.siguientesPermitidos(EstadoPedidos.Pagado);
        assertEquals(2, siguientes.size());
        assertTrue(siguientes.contains(EstadoPedidos.En_Preparacion));
        assertTrue(siguientes.contains(EstadoPedidos.Cancelado));
        assertFalse(siguientes.contains(EstadoPedidos.Despachado));
    }

    @Test
    void siguientesPermitidosEntregadoSoloDevuelto() {
        Set<EstadoPedidos> siguientes =
                EstadoPedidoMaquina.siguientesPermitidos(EstadoPedidos.Entregado);
        assertEquals(1, siguientes.size());
        assertTrue(siguientes.contains(EstadoPedidos.Devuelto));
    }
}
