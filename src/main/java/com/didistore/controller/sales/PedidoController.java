package com.didistore.controller.sales;

import com.didistore.dao.impl.auth.UsuariosDAOImpl;
import com.didistore.dao.impl.sales.DetallesPedidosDAOImpl;
import com.didistore.dao.impl.sales.PedidosDAOImpl;
import com.didistore.dao.interfaces.auth.IUsuariosDAO;
import com.didistore.dao.interfaces.sales.IDetallesPedidosDAO;
import com.didistore.dao.interfaces.sales.IPedidosDAO;
import com.didistore.model.auth.Usuarios;
import com.didistore.model.sales.DetallesPedidos;
import com.didistore.model.sales.Pedidos;
import com.didistore.model.sales.enums.EstadoPedidos;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author DELL
 */
public class PedidoController {

    private static final double IVA_PORCENTAJE = 0.19d;
    private static final Map<Integer, ClientePedido> CLIENTES_PEDIDO = new ConcurrentHashMap<>();

    private final IPedidosDAO pedidosDAO;
    private final IDetallesPedidosDAO detallesPedidosDAO;
    private final IUsuariosDAO usuariosDAO;
    private final CarritoController carritoController;

    public PedidoController() {
        this.pedidosDAO = new PedidosDAOImpl();
        this.detallesPedidosDAO = new DetallesPedidosDAOImpl();
        this.usuariosDAO = new UsuariosDAOImpl();
        this.carritoController = new CarritoController();
    }

    public Map<String, Object> registrarPedido(String nombreCliente,
            String emailCliente,
            List<CarritoController.ItemSolicitudCarrito> items,
            double descuento,
            double costoEnvio) {

        List<DetallesPedidos> detalles = carritoController.convertirItemsEnDetalles(items);
        if (detalles.isEmpty()) {
            return null;
        }

        double subtotal = carritoController.calcularSubtotal(detalles);
        double iva = subtotal * IVA_PORCENTAJE;
        double montoTotal = subtotal - descuento + iva + costoEnvio;

        Pedidos pedido = new Pedidos();
        pedido.setnumeroPedido(generarNumeroPedido());
        pedido.setusuarioId(obtenerUsuarioId(emailCliente));
        pedido.setdireccionEnvioId(0);
        pedido.setestadoPedido(EstadoPedidos.Pendiente_Pago);
        pedido.setsubTotal(redondear(subtotal));
        pedido.setdescuento(redondear(descuento));
        pedido.setiva(redondear(iva));
        pedido.setcostoEnvio(redondear(costoEnvio));
        pedido.setmontoTotal(redondear(montoTotal));
        pedido.setcuponId(0);
        pedido.setfechaPedido(new Timestamp(System.currentTimeMillis()));

        Pedidos pedidoGuardado = pedidosDAO.guardarPedido(pedido);
        detallesPedidosDAO.guardarDetalles(pedidoGuardado.getidPedido(), detalles);
        CLIENTES_PEDIDO.put(pedidoGuardado.getidPedido(), new ClientePedido(nombreCliente, emailCliente));

        return construirPedidoDetallado(pedidoGuardado.getidPedido());
    }

    public List<Map<String, Object>> listarPedidos() {
        List<Map<String, Object>> respuesta = new ArrayList<>();
        for (Pedidos pedido : pedidosDAO.listarPedidos()) {
            respuesta.add(construirResumenPedido(pedido));
        }
        return respuesta;
    }

    public Map<String, Object> consultarPedido(int idPedido) {
        return construirPedidoDetallado(idPedido);
    }

    private Map<String, Object> construirPedidoDetallado(int idPedido) {
        Pedidos pedido = pedidosDAO.consultarPedidoPorId(idPedido);
        if (pedido == null) {
            return null;
        }

        List<DetallesPedidos> detalles = detallesPedidosDAO.listarDetallesPorPedido(idPedido);
        Map<String, Object> detalle = construirResumenPedido(pedido);
        detalle.put("detalles", detalles);
        return detalle;
    }

    private Map<String, Object> construirResumenPedido(Pedidos pedido) {
        List<DetallesPedidos> detalles = detallesPedidosDAO.listarDetallesPorPedido(pedido.getidPedido());
        ClientePedido clientePedido = CLIENTES_PEDIDO.get(pedido.getidPedido());

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("id", pedido.getidPedido());
        resumen.put("idPedido", pedido.getidPedido());
        resumen.put("numeroPedido", pedido.getnumeroPedido());
        resumen.put("cliente", clientePedido != null ? clientePedido.nombre() : "Cliente invitado");
        resumen.put("email", clientePedido != null ? clientePedido.email() : "");
        resumen.put("fecha", pedido.getfechaPedido());
        resumen.put("productos", carritoController.contarUnidades(detalles) + " producto(s)");
        resumen.put("items", carritoController.contarUnidades(detalles));
        resumen.put("subtotal", pedido.getsubTotal());
        resumen.put("iva", pedido.getiva());
        resumen.put("costoEnvio", pedido.getcostoEnvio());
        resumen.put("descuento", pedido.getdescuento());
        resumen.put("montoTotal", pedido.getmontoTotal());
        resumen.put("total", pedido.getmontoTotal());
        resumen.put("estado", formatearEstado(pedido.getestadoPedido()));
        resumen.put("status", formatearEstado(pedido.getestadoPedido()));
        return resumen;
    }

    private String generarNumeroPedido() {
        return "DIDI-" + DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
    }

    private int obtenerUsuarioId(String emailCliente) {
        if (emailCliente == null || emailCliente.isBlank()) {
            return 0;
        }

        Usuarios usuario = usuariosDAO.consultarUsuariosPorEmail(emailCliente.trim().toLowerCase());
        return usuario != null ? usuario.getidUsuario() : 0;
    }

    private String formatearEstado(EstadoPedidos estadoPedido) {
        if (estadoPedido == null) {
            return "Pendiente";
        }
        return estadoPedido.name().replace('_', ' ');
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0d) / 100.0d;
    }

    private record ClientePedido(String nombre, String email) {
    }
}
