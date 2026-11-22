package com.didistore.controller.sales;

import com.didistore.dao.impl.sales.HistorialEstadoPedidosDAOImpl;
import com.didistore.dao.interfaces.sales.IHistorialEstadoPedidosDAO;
import com.didistore.model.sales.HistorialEstadoPedidos;
import com.didistore.model.sales.enums.EstadoPedidos;
import com.didistore.util.AuditLog;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Consulta del historial de estados de pedidos (RF-12 / RF-13 admin).
 * El registro de transiciones se hace vía EstadoPedidoController.
 */
public class HistorialEstadoPedidosController {

    private final IHistorialEstadoPedidosDAO historialDAO = new HistorialEstadoPedidosDAOImpl();

    /** Lista el historial como lista de mapas JSON-friendly. */
    public List<Map<String, Object>> listarPorPedido(int pedidoId) {
        List<HistorialEstadoPedidos> lista = historialDAO.listarHistorialPorPedido(pedidoId);
        List<Map<String, Object>> out = new ArrayList<>();
        for (HistorialEstadoPedidos h : lista) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", h.getidHistorial());
            m.put("pedidoId", h.getpedidoId());
            m.put("estadoAnterior", h.getestadoAnterior() != null ? h.getestadoAnterior().name() : null);
            m.put("estadoNuevo", h.getestadoNuevo() != null ? h.getestadoNuevo().name() : null);
            m.put("usuarioId", h.getusuarioId());
            m.put("fechaCambio", h.getfechaCambio() != null ? h.getfechaCambio().toString() : null);
            m.put("notas", h.getnotas());
            out.add(m);
        }
        return out;
    }

    public Map<String, Object> obtenerUltimo(int pedidoId) {
        HistorialEstadoPedidos ultimo = historialDAO.obtenerUltimoEstado(pedidoId);
        if (ultimo == null) {
            return Map.of("success", false, "message", "No hay historial para este pedido");
        }
        Map<String, Object> m = new HashMap<>();
        m.put("success", true);
        m.put("idHistorial", ultimo.getidHistorial());
        m.put("estadoAnterior", ultimo.getestadoAnterior() != null
                ? ultimo.getestadoAnterior().name() : null);
        m.put("estadoNuevo", ultimo.getestadoNuevo() != null
                ? ultimo.getestadoNuevo().name() : null);
        m.put("fechaCambio", ultimo.getfechaCambio() != null
                ? ultimo.getfechaCambio().toString() : null);
        m.put("notas", ultimo.getnotas());
        return m;
    }

    /**
     * Registro manual de cambio (solo staff; normalmente lo hace EstadoPedidoController).
     * Mantenido por compatibilidad.
     */
    public Map<String, Object> registrarCambioManual(int pedidoId, int usuarioId,
            EstadoPedidos anterior, EstadoPedidos nuevo, String notas, String ip) {
        HistorialEstadoPedidos h = new HistorialEstadoPedidos();
        h.setpedidoId(pedidoId);
        h.setestadoAnterior(anterior);
        h.setestadoNuevo(nuevo);
        h.setusuarioId(usuarioId);
        h.setfechaCambio(new Timestamp(System.currentTimeMillis()));
        h.setnotas(notas);

        boolean ok = historialDAO.registrarCambioEstado(h);
        if (ok) {
            AuditLog.registrar(usuarioId, null, "HISTORIAL_ESTADO", "sales",
                    "historial_estados_pedido", pedidoId, null,
                    nuevo != null ? nuevo.name() : null, ip, "EXITO");
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", ok);
        resp.put("message", ok ? "Historial registrado" : "No se pudo registrar");
        return resp;
    }
}
