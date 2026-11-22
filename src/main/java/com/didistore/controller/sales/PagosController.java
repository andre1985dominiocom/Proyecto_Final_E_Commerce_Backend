package com.didistore.controller.sales;

import com.didistore.dao.impl.sales.PagosDAOImpl;
import com.didistore.dao.impl.sales.PedidosDAOImpl;
import com.didistore.dao.interfaces.sales.IPagosDAO;
import com.didistore.dao.interfaces.sales.IPedidosDAO;
import com.didistore.model.sales.Pagos;
import com.didistore.model.sales.Pedidos;
import com.didistore.model.sales.enums.EstadoPagos;
import com.didistore.model.sales.enums.EstadoPedidos;
import com.didistore.model.sales.enums.MetodoPagos;
import com.didistore.util.AuditLog;
import com.didistore.util.EstadoPedidoMaquina;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Pasarela de pago simulada (mock) + estados de pago.
 * RF-14/RF-03: al aprobar el pago, el pedido pasa Pendiente_Pago → Pagado
 * y se registra en el historial de estados (RF-12).
 */
public class PagosController {

    private final IPagosDAO pagosDAO = new PagosDAOImpl();
    private final IPedidosDAO pedidosDAO = new PedidosDAOImpl();
    private final EstadoPedidoController estadoPedidoController = new EstadoPedidoController();

    /**
     * Paga un pedido con la pasarela mock. Solo el dueño, solo estado Pendiente_Pago.
     * Resultado simulado: Aprobado (o Rechazado si resultadoParam=Rechazado, para pruebas).
     */
    public Map<String, Object> pagarPedido(int pedidoId, int usuarioId, String metodoPagoStr,
            String resultadoParam, String ip) {

        Pedidos pedido = pedidosDAO.buscarPorId(pedidoId);
        if (pedido == null) {
            return Map.of("success", false, "message", "Pedido no encontrado", "status", 404);
        }
        if (pedido.getusuarioId() != usuarioId) {
            return Map.of("success", false, "message", "No autorizado", "status", 403);
        }
        if (pedido.getestadoPedido() != EstadoPedidos.Pendiente_Pago) {
            return Map.of("success", false, "message",
                    "El pedido no está pendiente de pago (estado: "
                    + pedido.getestadoPedido().name() + ")", "status", 409);
        }

        MetodoPagos metodo;
        try {
            metodo = MetodoPagos.valueOf(metodoPagoStr);
        } catch (Exception e) {
            return Map.of("success", false, "message", "Método de pago inválido", "status", 400);
        }

        // Mock de pasarela: simula aprobación/rechazo
        boolean aprobado = !"Rechazado".equalsIgnoreCase(resultadoParam);
        String referencia = "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        String datosPasarela = "{\"gateway\":\"mock\",\"metodo\":\"" + metodo.name()
                + "\",\"resultado\":\"" + (aprobado ? "APROBADO" : "RECHAZADO") + "\"}";

        Timestamp ahora = new Timestamp(System.currentTimeMillis());

        Pagos pago = new Pagos();
        pago.setpedidoId(pedidoId);
        pago.setmetodoPago(metodo);
        pago.setestadoPago(aprobado ? EstadoPagos.Aprobado : EstadoPagos.Rechazado);
        pago.setmonto(pedido.getmontoTotal());
        pago.setreferenciaTransaccion(referencia);
        pago.setreferenciaInterna("INT-" + pedido.getnumeroPedido());
        pago.setdatosPasarela(datosPasarela);
        pago.setfechaPago(aprobado ? ahora : null);
        pago.setfechaCreacion(ahora);

        boolean registrado = pagosDAO.registrarPago(pago);
        if (!registrado) {
            return Map.of("success", false, "message", "No se pudo registrar el pago", "status", 500);
        }

        if (!aprobado) {
            AuditLog.registrar(usuarioId, null, "PAGO_RECHAZADO", "sales", "pagos",
                    null, null, referencia, ip, "RECHAZADO");
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", false);
            resp.put("message", "Pago rechazado por la pasarela (simulado)");
            resp.put("estadoPago", EstadoPagos.Rechazado.name());
            resp.put("referencia", referencia);
            resp.put("status", 402);
            return resp;
        }

        // Transición del pedido: Pendiente_Pago → Pagado (RF-12)
        Map<String, Object> transicion = estadoPedidoController.transicionar(
                pedidoId, EstadoPedidos.Pagado, usuarioId, false,
                "Pago aprobado ref " + referencia, ip);

        boolean okTransicion = Boolean.TRUE.equals(transicion.get("success"));

        AuditLog.registrar(usuarioId, null, "PAGO_APROBADO", "sales", "pagos",
                null, null, referencia, ip, "EXITO");

        Map<String, Object> resp = new HashMap<>();
        resp.put("success", okTransicion);
        resp.put("message", okTransicion
                ? "Pago aprobado y pedido actualizado a Pagado"
                : "Pago registrado pero no se pudo actualizar el pedido");
        resp.put("estadoPago", EstadoPagos.Aprobado.name());
        resp.put("estadoPedido", okTransicion ? EstadoPedidos.Pagado.name()
                : pedido.getestadoPedido().name());
        resp.put("referencia", referencia);
        resp.put("monto", pedido.getmontoTotal());
        resp.put("metodoPago", metodo.name());
        resp.put("status", okTransicion ? 200 : 500);
        return resp;
    }

    /** Historial de pagos de un pedido (dueño o staff). */
    public List<Map<String, Object>> listarPagos(int pedidoId) {
        List<Pagos> pagos = pagosDAO.listarPagosPorVenta(pedidoId);
        List<Map<String, Object>> out = new ArrayList<>();
        for (Pagos p : pagos) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getidPago());
            m.put("pedidoId", p.getpedidoId());
            m.put("metodoPago", p.getmetodoPago() != null ? p.getmetodoPago().name() : null);
            m.put("estadoPago", p.getestadoPago() != null ? p.getestadoPago().name() : null);
            m.put("monto", p.getmonto());
            m.put("referenciaTransaccion", p.getreferenciaTransaccion());
            m.put("fechaPago", p.getfechaPago() != null ? p.getfechaPago().toString() : null);
            out.add(m);
        }
        return out;
    }

    /**
     * Reembolso (staff): marca pago Reembolsado y permite Devuelto/Cancelado
     * vía máquina de estados en el flujo de cancelación.
     */
    public Map<String, Object> reembolsarPago(int pedidoId, Integer staffId, String ip) {
        List<Pagos> pagos = pagosDAO.listarPagosPorVenta(pedidoId);
        boolean alguno = false;
        for (Pagos p : pagos) {
            if (p.getestadoPago() == EstadoPagos.Aprobado) {
                pagosDAO.actualizarEstadoPago(p.getidPago(), EstadoPagos.Reembolsado);
                alguno = true;
            }
        }
        if (alguno) {
            AuditLog.registrar(staffId, null, "PAGO_REEMBOLSADO", "sales", "pagos",
                    pedidoId, null, null, ip, "EXITO");
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", alguno);
        resp.put("message", alguno ? "Pago reembolsado" : "No había pagos aprobados");
        return resp;
    }

    public boolean pedidoPerteneceAUsuario(int pedidoId, int usuarioId) {
        Pedidos p = pedidosDAO.buscarPorId(pedidoId);
        return p != null && p.getusuarioId() == usuarioId;
    }
}
