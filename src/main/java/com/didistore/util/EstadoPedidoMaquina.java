package com.didistore.util;

import com.didistore.model.sales.enums.EstadoPedidos;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Máquina de estados del pedido (RF-12).
 * Ciclo: Pendiente_Pago → Pagado → En_Preparacion → Despachado → En_Transito → Entregado
 * Finales: Cancelado, Devuelto.
 */
public final class EstadoPedidoMaquina {

    private static final Map<EstadoPedidos, Set<EstadoPedidos>> TRANSICIONES =
            new EnumMap<>(EstadoPedidos.class);

    static {
        TRANSICIONES.put(EstadoPedidos.Pendiente_Pago,
                EnumSet.of(EstadoPedidos.Pagado, EstadoPedidos.Cancelado));
        TRANSICIONES.put(EstadoPedidos.Pagado,
                EnumSet.of(EstadoPedidos.En_Preparacion, EstadoPedidos.Cancelado));
        TRANSICIONES.put(EstadoPedidos.En_Preparacion,
                EnumSet.of(EstadoPedidos.Despachado, EstadoPedidos.Cancelado));
        TRANSICIONES.put(EstadoPedidos.Despachado,
                EnumSet.of(EstadoPedidos.En_Transito, EstadoPedidos.Cancelado));
        TRANSICIONES.put(EstadoPedidos.En_Transito,
                EnumSet.of(EstadoPedidos.Entregado, EstadoPedidos.Cancelado));
        TRANSICIONES.put(EstadoPedidos.Entregado,
                EnumSet.of(EstadoPedidos.Devuelto));
        TRANSICIONES.put(EstadoPedidos.Cancelado, EnumSet.noneOf(EstadoPedidos.class));
        TRANSICIONES.put(EstadoPedidos.Devuelto, EnumSet.noneOf(EstadoPedidos.class));
    }

    private EstadoPedidoMaquina() {
    }

    public static boolean esValida(EstadoPedidos desde, EstadoPedidos hacia) {
        if (desde == null || hacia == null) {
            return false;
        }
        if (desde == hacia) {
            return false;
        }
        Set<EstadoPedidos> permitidas = TRANSICIONES.get(desde);
        return permitidas != null && permitidas.contains(hacia);
    }

    public static Set<EstadoPedidos> siguientesPermitidos(EstadoPedidos desde) {
        if (desde == null) {
            return Collections.emptySet();
        }
        Set<EstadoPedidos> set = TRANSICIONES.get(desde);
        return set == null ? Collections.emptySet() : Collections.unmodifiableSet(set);
    }

    /** Cancelar solo el cliente: únicamente antes de pagar. */
    public static boolean clientePuedeCancelar(EstadoPedidos actual) {
        return actual == EstadoPedidos.Pendiente_Pago;
    }

    /** Staff puede cancelar hasta antes de que el pedido salga a despacho real (En_Transito+). */
    public static boolean staffPuedeCancelar(EstadoPedidos actual) {
        return actual == EstadoPedidos.Pagado
                || actual == EstadoPedidos.En_Preparacion
                || actual == EstadoPedidos.Despachado;
    }

    public static boolean esTerminal(EstadoPedidos actual) {
        return actual == EstadoPedidos.Cancelado || actual == EstadoPedidos.Devuelto;
    }
}
