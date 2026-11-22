package com.didistore.controller.sales;

import com.didistore.dao.impl.sales.HistorialEstadoPedidosDAOImpl;
import com.didistore.dao.impl.sales.PedidosDAOImpl;
import com.didistore.dao.interfaces.sales.IHistorialEstadoPedidosDAO;
import com.didistore.dao.interfaces.sales.IPedidosDAO;
import com.didistore.model.sales.HistorialEstadoPedidos;
import com.didistore.model.sales.Pedidos;
import com.didistore.model.sales.enums.EstadoPedidos;
import com.didistore.util.AuditLog;
import com.didistore.util.EstadoPedidoMaquina;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Orquesta transiciones de estado de pedidos (RF-12): valida con la máquina
 * de estados, persiste en Pedidos + Historial_Estados_Pedido y audita.
 */
public class EstadoPedidoController {

    private final IPedidosDAO pedidosDAO = new PedidosDAOImpl();
    private final IHistorialEstadoPedidosDAO historialDAO = new HistorialEstadoPedidosDAOImpl();

    /**
     * Valida y aplica una transición. Devuelve mapa con success/message.
     * Si esStaff=false solo se permiten transiciones de cliente (cancelar Pendiente_Pago).
     */
    public Map<String, Object> transicionar(int pedidoId, EstadoPedidos estadoNuevo,
            Integer usuarioActivo, boolean esStaff, String notas, String ip) {

        if (estadoNuevo == null) {
            return Map.of("success", false, "message", "Estado nuevo inválido");
        }

        Pedidos pedido = pedidosDAO.buscarPorId(pedidoId);
        if (pedido == null) {
            return Map.of("success", false, "message", "Pedido no encontrado");
        }

        EstadoPedidos actual = pedido.getestadoPedido();

        if (EstadoPedidoMaquina.esTerminal(actual)) {
            return Map.of("success", false, "message",
                    "El pedido está en estado terminal: " + actual.name());
        }

        // Permisos de cancelación
        if (estadoNuevo == EstadoPedidos.Cancelado) {
            if (!esStaff && !EstadoPedidoMaquina.clientePuedeCancelar(actual)) {
                return Map.of("success", false, "message",
                        "Solo puedes cancelar pedidos aún no pagados");
            }
            if (esStaff && !EstadoPedidoMaquina.staffPuedeCancelar(actual)) {
                return Map.of("success", false, "message",
                        "No se puede cancelar un pedido en estado " + actual.name());
            }
        }

        if (!EstadoPedidoMaquina.esValida(actual, estadoNuevo)) {
            return Map.of("success", false, "message",
                    "Transición no permitida: " + actual.name() + " → " + estadoNuevo.name()
                    + ". Permitidas: " + EstadoPedidoMaquina.siguientesPermitidos(actual));
        }

        boolean ok = pedidosDAO.actualizarEstado(pedidoId, estadoNuevo);
        if (!ok) {
            return Map.of("success", false, "message", "No se pudo actualizar el estado");
        }

        registrarHistorial(pedidoId, actual, estadoNuevo,
                usuarioActivo != null ? usuarioActivo : pedido.getusuarioId(),
                notas != null ? notas : ("Transición a " + estadoNuevo.name()));

        AuditLog.registrar(usuarioActivo, null, "ESTADO_PEDIDO", "sales", "pedidos",
                pedidoId, actual.name(), estadoNuevo.name(), ip, "EXITO");

        return Map.of(
                "success", true,
                "message", "Estado actualizado a " + estadoNuevo.name(),
                "estadoAnterior", actual.name(),
                "estadoNuevo", estadoNuevo.name()
        );
    }

    /** Registra la entrada inicial del historial al crear un pedido (null → Pendiente_Pago). */
    public void registrarCreacion(int pedidoId, int usuarioId) {
        registrarHistorial(pedidoId, null, EstadoPedidos.Pendiente_Pago, usuarioId,
                "Pedido creado");
    }

    public List<HistorialEstadoPedidos> historial(int pedidoId) {
        return historialDAO.listarHistorialPorPedido(pedidoId);
    }

    private void registrarHistorial(int pedidoId, EstadoPedidos anterior, EstadoPedidos nuevo,
            int usuarioId, String notas) {
        HistorialEstadoPedidos h = new HistorialEstadoPedidos();
        h.setpedidoId(pedidoId);
        h.setestadoAnterior(anterior);
        h.setestadoNuevo(nuevo);
        h.setusuarioId(usuarioId);
        h.setfechaCambio(new Timestamp(System.currentTimeMillis()));
        h.setnotas(notas);
        historialDAO.registrarCambioEstado(h);
    }
}
