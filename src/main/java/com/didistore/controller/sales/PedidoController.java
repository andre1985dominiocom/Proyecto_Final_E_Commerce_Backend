
package com.didistore.controller.sales;

import com.didistore.controller.logistics.EnviosController;
import com.didistore.controller.marketing.CuponesController;
import com.didistore.dao.impl.catalog.InventariosDAOImpl;
import com.didistore.dao.impl.sales.CarritoComprasDAOImpl;
import com.didistore.dao.impl.sales.PedidosDAOImpl;
import com.didistore.dao.interfaces.catalog.IInventariosDAO;
import com.didistore.dao.interfaces.sales.ICarritoComprasDAO;
import com.didistore.dao.interfaces.sales.IPedidosDAO;
import com.didistore.model.marketing.Cupones;
import com.didistore.model.sales.DetallesPedidos;
import com.didistore.model.sales.Pedidos;
import com.didistore.model.sales.enums.EstadoPedidos;
import com.didistore.util.AuditLog;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Controlador de Pedidos: Encapsula la lógica de negocio relacionada con la gestión de pedidos.
public class PedidoController {
    
    private final IPedidosDAO pedidosDAO;
    private final CuponesController cuponesController = new CuponesController();
    private final EnviosController enviosController = new EnviosController();

    public PedidoController() {
        this.pedidosDAO = new PedidosDAOImpl();
    }
    
    public ICarritoComprasDAO carritoDAO = new CarritoComprasDAOImpl(); 
    private final IInventariosDAO inventariosDAO = new InventariosDAOImpl();

    /**
     * Lógica central del Checkout: Convierte un resumen de compra y sus detalles
     * en un Pedido real persistido en la base de datos.
     * Acepta cuponId o cuponCodigo (RF-09); valida vigencia/usos y calcula el descuento real.
     * Crea el registro de Envío asociado (Fase 3).
     */
    public boolean procesarCheckout(int usuarioId, int direccionEnvioId, double subtotal,
            double costoEnvio, List<DetallesPedidos> detalles, int cuponId, String cuponCodigo) {
        return procesarCheckout(usuarioId, direccionEnvioId, subtotal, costoEnvio, detalles,
                cuponId, cuponCodigo, EnviosController.METODO_ESTANDAR);
    }

    /**
     * Checkout con método de envío explícito (Estandar/Express).
     */
    public boolean procesarCheckout(int usuarioId, int direccionEnvioId, double subtotal,
            double costoEnvio, List<DetallesPedidos> detalles, int cuponId, String cuponCodigo,
            String metodoEnvio) {

        // Validaciones de negocio preventivas
        if (detalles == null || detalles.isEmpty()) {
            return false; 
        }
        if (subtotal < 0 || costoEnvio < 0) {
            return false;
        }

        // Resolver cupón: por id o por código (RF-09)
        Cupones cupon = null;
        double descuento = 0.0;

        if (cuponId > 0) {
            cupon = cuponesController.buscarPorId(cuponId);
        } else if (cuponCodigo != null && !cuponCodigo.isBlank()) {
            Map<String, Object> validacion = cuponesController.validarYCalcular(cuponCodigo, subtotal);
            if (Boolean.TRUE.equals(validacion.get("success"))) {
                cupon = (Cupones) validacion.get("cupon");
                descuento = (Double) validacion.get("descuento");
            } else {
                System.err.println("Cupón rechazado en checkout: " + validacion.get("message"));
                return false;
            }
        }

        if (cupon != null && cuponId > 0) {
            Map<String, Object> validacion = cuponesController.validarYCalcular(cupon.getcodigo(), subtotal);
            if (!Boolean.TRUE.equals(validacion.get("success"))) {
                System.err.println("Cupón rechazado en checkout: " + validacion.get("message"));
                return false;
            }
            descuento = (Double) validacion.get("descuento");
        }

        Pedidos nuevoPedido = new Pedidos();
        
        // 1. Generar un número de pedido único y amigable para el cliente (Ej: PED-A1B2C3D4)
        nuevoPedido.setnumeroPedido("PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        // 2. Asignación de datos relacionales
        nuevoPedido.setusuarioId(usuarioId);
        nuevoPedido.setdireccionEnvioId(direccionEnvioId);
        nuevoPedido.setestadoPedido(EstadoPedidos.Pendiente_Pago); // Estado inicial por defecto
        
        if (cupon != null) {
            nuevoPedido.setcuponId(cupon.getidCupon());
        }

        // 3. Lógica Financiera (Snapshots para la BD)
        double iva = (subtotal - descuento) * 0.19; // IVA 19% Colombia
        double montoTotal = (subtotal - descuento) + iva + costoEnvio;

        nuevoPedido.setsubTotal(subtotal);
        nuevoPedido.setdescuento(descuento);
        nuevoPedido.setiva(iva);
        nuevoPedido.setcostoEnvio(costoEnvio);
        nuevoPedido.setmontoTotal(montoTotal);

        for (DetallesPedidos detalle : detalles) {
            
            if (!inventariosDAO.hayStockSuficiente(detalle.getproductoId(), detalle.getcantidad())) {
                System.err.println("Stock insuficiente para producto" + detalle.getproductoId());
                return false;
            }
        }
        // 4. Inyección del DAO: Ejecuta la transacción (Guarda Cabecera + Detalles)
        boolean pedidoCreado = pedidosDAO.crearPedido(nuevoPedido, detalles);
        
        if (!pedidoCreado) {
            return false;
        }

        // 5. Registrar envío asociado (Fase 3) y uso del cupón (RF-09) + auditoría
        Pedidos creado = pedidosDAO.buscarPorNumeroPedido(nuevoPedido.getnumeroPedido());
        if (creado != null) {
            enviosController.crearEnvioTrasCheckout(creado.getidPedido(), direccionEnvioId, metodoEnvio);
            // Historial inicial del pedido (RF-12): null → Pendiente_Pago
            new EstadoPedidoController().registrarCreacion(creado.getidPedido(), usuarioId);
        }

        if (cupon != null) {
            cuponesController.registrarUso(cupon.getidCupon());
            AuditLog.registrar(usuarioId, null, "CUPON_APLICADO", "sales", "cupones",
                    cupon.getidCupon(), null, cupon.getcodigo(), null, "EXITO");
        }
        AuditLog.registrar(usuarioId, null, "CHECKOUT", "sales", "pedidos",
                null, null, nuevoPedido.getnumeroPedido(), null, "EXITO");

        return true;
    }
    

    /**
     * Obtiene todos los pedidos realizados por un usuario específico.
     */
    public List<Pedidos> obtenerHistorialUsuario(int usuarioId) {
        return pedidosDAO.listarPorUsuario(usuarioId);
    }

    // RF-17: historial con filtros de fecha y estado.
    public List<Pedidos> obtenerHistorialUsuario(int usuarioId, String fechaDesde,
            String fechaHasta, String estado) {
        return pedidosDAO.listarPorUsuarioConFiltros(usuarioId, fechaDesde, fechaHasta, estado);
    }

    // Staff: listado completo de pedidos para panel admin.
    public List<Pedidos> listarTodosPedidos() {
        return pedidosDAO.listarTodos();
    }
    
    /**
     * Obtiene la información cabecera de un pedido puntual.
     */
    public Pedidos obtenerDetallePedido(int pedidoId) {
        return pedidosDAO.buscarPorId(pedidoId);
    }
    
    /**
     * Obtiene la lista de productos que fueron comprados dentro de un pedido.
     */
    public List<DetallesPedidos> obtenerItemsPedido(int pedidoId) {
        return pedidosDAO.listarDetallesPorPedido(pedidoId);
    }

    public boolean actualizarEstadoPedido(int pedidoId, EstadoPedidos nuevoEstado) {
        if (nuevoEstado == null) return false;
        return pedidosDAO.actualizarEstado(pedidoId, nuevoEstado);
    }

    // Compatibilidad con firmas anteriores (sin cuponCodigo).
    public boolean procesarCheckout(int usuarioId, int direccionEnvioId, double subtotal,
            double costoEnvio, List<DetallesPedidos> detalles, int cuponId) {
        return procesarCheckout(usuarioId, direccionEnvioId, subtotal, costoEnvio, detalles, cuponId, null);
    }

    /**
     * Verifica que el pedido pertenezca al usuario (anti-IDOR para rastreo).
     */
    public boolean pedidoPerteneceAUsuario(int pedidoId, int usuarioId) {
        Pedidos p = pedidosDAO.buscarPorId(pedidoId);
        return p != null && p.getusuarioId() == usuarioId;
    }

    /** Último pedido del usuario (para devolver pedidoId tras checkout). */
    public Pedidos buscarUltimoPedidoUsuario(int usuarioId) {
        List<Pedidos> lista = pedidosDAO.listarPorUsuario(usuarioId);
        return lista.isEmpty() ? null : lista.get(0);
    }
}