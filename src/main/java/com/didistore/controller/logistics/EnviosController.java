
package com.didistore.controller.logistics;

import com.didistore.dao.interfaces.logistics.CiudadesDAO;
import com.didistore.dao.impl.logistics.EnviosDAOImpl;
import com.didistore.dao.interfaces.logistics.IEnviosDAO;
import com.didistore.model.logistics.Ciudades;
import com.didistore.model.logistics.Direcciones;
import com.didistore.dao.impl.logistics.DireccionesDAOImpl;
import com.didistore.model.logistics.Envios;
import com.didistore.model.logistics.enums.EstadoEnvios;
import com.didistore.model.logistics.enums.TransportadoraEnvios;
import com.didistore.util.AuditLog;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Controlador de Envíos: cálculo de costos de envío (CU-CLI-03), registro de guías
// y seguimiento (rastreo) para el cliente. RF-12 (logística básica).
public class EnviosController {

    public static final String METODO_ESTANDAR = "Estandar";
    public static final String METODO_EXPRESS = "Express";

    // Envío estándar gratis en compras >= 100000 (alerta CU-CLI-02)
    public static final double UMBRAL_ENVIO_GRATIS = 100000.0;

    // Ciudades principales (tier 1): costos más bajos
    private static final java.util.Set<Integer> CIUDADES_TIER1 = java.util.Set.of(1, 2, 3, 4, 5, 7);

    // Costos base COP (mock razonable según destino y método)
    private static final double TIER1_ESTANDAR = 8000.0;
    private static final double TIER1_EXPRESS = 18000.0;
    private static final double TIER2_ESTANDAR = 12000.0;
    private static final double TIER2_EXPRESS = 22000.0;

    private final IEnviosDAO enviosDAO;
    private final CiudadesDAO ciudadesDAO;
    private final DireccionesDAOImpl direccionesDAO;

    public EnviosController() {
        this.enviosDAO = new EnviosDAOImpl();
        this.ciudadesDAO = new CiudadesDAO();
        this.direccionesDAO = new DireccionesDAOImpl();
    }

    /**
     * Calcula el costo de envío según método (Estandar/Express), ciudad destino
     * y subtotal (gratis estándar si subtotal >= 100000).
     */
    public double calcularCostoEnvio(String metodo, double subtotal, Integer ciudadId) {
        String m = (metodo == null || metodo.isBlank()) ? METODO_ESTANDAR : metodo.trim();
        if (!METODO_ESTANDAR.equalsIgnoreCase(m) && !METODO_EXPRESS.equalsIgnoreCase(m)) {
            m = METODO_ESTANDAR;
        }

        boolean tier1 = ciudadId != null && CIUDADES_TIER1.contains(ciudadId);
        double costo;

        if (METODO_ESTANDAR.equalsIgnoreCase(m)) {
            if (subtotal >= UMBRAL_ENVIO_GRATIS) {
                return 0.0;
            }
            costo = tier1 ? TIER1_ESTANDAR : TIER2_ESTANDAR;
        } else {
            costo = tier1 ? TIER1_EXPRESS : TIER2_EXPRESS;
        }
        return costo;
    }

    /**
     * Opciones de envío para el checkout (CU-CLI-03 paso 2):
     * Estándar 5-7 días y Express 2-3 días, con costo según destino.
     */
    public List<Map<String, Object>> listarOpcionesEnvio(double subtotal, Integer ciudadId) {
        List<Map<String, Object>> opciones = new ArrayList<>();

        double costoEstandar = calcularCostoEnvio(METODO_ESTANDAR, subtotal, ciudadId);
        double costoExpress = calcularCostoEnvio(METODO_EXPRESS, subtotal, ciudadId);

        Map<String, Object> estandar = new LinkedHashMap<>();
        estandar.put("metodo", METODO_ESTANDAR);
        estandar.put("nombre", "Envío Estándar");
        estandar.put("diasEstimados", "5-7 días");
        estandar.put("costo", costoEstandar);
        estandar.put("gratis", costoEstandar == 0.0);
        opciones.add(estandar);

        Map<String, Object> express = new LinkedHashMap<>();
        express.put("metodo", METODO_EXPRESS);
        express.put("nombre", "Envío Express");
        express.put("diasEstimados", "2-3 días");
        express.put("costo", costoExpress);
        express.put("gratis", false);
        opciones.add(express);

        return opciones;
    }

    /**
     * Resuelve la ciudad de una dirección (para calcular costo según destino).
     */
    public Integer ciudadDeDireccion(int direccionId) {
        if (direccionId <= 0) {
            return null;
        }
        Direcciones dir = direccionesDAO.buscarPorId(direccionId);
        if (dir == null || dir.getciudadId() <= 0) {
            return null;
        }
        return dir.getciudadId();
    }

    /**
     * Crea el registro de envío asociado al pedido al confirmar checkout (estado Pendiente).
     * Si ya existe envío para el pedido, lo deja intacto.
     */
    public boolean crearEnvioTrasCheckout(int pedidoId, int direccionId, String metodoEnvio) {
        if (pedidoId <= 0) {
            return false;
        }
        if (enviosDAO.buscarPorPedido(pedidoId) != null) {
            return true;
        }

        Envios envio = new Envios();
        envio.setpedidoId(pedidoId);
        envio.setdireccionId(direccionId);
        envio.setmetodoEnvio(normalizarMetodo(metodoEnvio));
        envio.setestado(EstadoEnvios.Pendiente);
        envio.setobservaciones("Método de envío: " + normalizarMetodo(metodoEnvio));
        return enviosDAO.crearEnvio(envio);
    }

    /**
     * Registra el despacho: transportadora, guía mock y fecha de entrega estimada
     * según método (Estandar +7 días, Express +3 días).
     */
    public boolean registrarDespacho(int pedidoId, String transportadora, String numeroGuia,
            Integer usuarioAdminId, String ip) {

        Envios envio = enviosDAO.buscarPorPedido(pedidoId);
        if (envio == null) {
            return false;
        }

        TransportadoraEnvios transp;
        try {
            transp = TransportadoraEnvios.valueOf(transportadora);
        } catch (Exception e) {
            transp = TransportadoraEnvios.Otro;
        }

        String metodo = normalizarMetodo(envio.getmetodoEnvio());
        int dias = METODO_EXPRESS.equalsIgnoreCase(metodo) ? 3 : 7;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, dias);
        Timestamp entregaEstimada = new Timestamp(cal.getTimeInMillis());

        envio.settransportadora(transp);
        envio.setnumeroGuia(numeroGuia);
        envio.setestado(EstadoEnvios.Despachado);
        envio.setfechaDespacho(new Timestamp(System.currentTimeMillis()));
        envio.setfechaEntregaEstimada(entregaEstimada);

        boolean ok = enviosDAO.actualizarEnvio(envio);
        if (ok) {
            AuditLog.registrar(usuarioAdminId, null, "ENVIO_DESPACHADO", "logistics", "envios",
                    envio.getidEnvio(), null, "pedido=" + pedidoId + ",guia=" + numeroGuia, ip, "EXITO");
        }
        return ok;
    }

    /**
     * Actualiza el estado del envío (pipeline logístico).
     */
    public boolean actualizarEstadoEnvio(int idEnvio, String estado, Integer usuarioId, String ip) {
        EstadoEnvios nuevo;
        try {
            nuevo = EstadoEnvios.valueOf(estado);
        } catch (Exception e) {
            return false;
        }
        boolean ok = enviosDAO.actualizarEstado(idEnvio, nuevo.name());
        if (ok) {
            AuditLog.registrar(usuarioId, null, "ENVIO_ESTADO", "logistics", "envios",
                    idEnvio, null, nuevo.name(), ip, "EXITO");
        }
        return ok;
    }

    /**
     * Rastreo: devuelve el envío del pedido si pertenece al usuario (o es admin).
     */
    public Envios obtenerEnvioParaRastreo(int pedidoId, int usuarioId, boolean esStaff) {
        Envios envio = enviosDAO.buscarPorPedido(pedidoId);
        if (envio == null) {
            return null;
        }
        if (esStaff) {
            return envio;
        }
        // Verificar pertenencia vía dirección o pedidos del usuario
        // (El pedido se valida fuera con PedidoController; aquí se retorna si existe)
        return envio;
    }

    public Envios buscarPorPedido(int pedidoId) {
        return enviosDAO.buscarPorPedido(pedidoId);
    }

    public Ciudades buscarCiudad(int ciudadId) {
        for (Ciudades c : ciudadesDAO.listar()) {
            if (c.getidCiudad() == ciudadId) {
                return c;
            }
        }
        return null;
    }

    private String normalizarMetodo(String metodo) {
        if (metodo == null || metodo.isBlank()) {
            return METODO_ESTANDAR;
        }
        String m = metodo.trim();
        if (METODO_EXPRESS.equalsIgnoreCase(m) || "express".equalsIgnoreCase(m)) {
            return METODO_EXPRESS;
        }
        return METODO_ESTANDAR;
    }
}
