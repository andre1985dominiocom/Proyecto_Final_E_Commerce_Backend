package com.didistore.controller.marketing;

import com.didistore.dao.interfaces.marketing.ICuponesDAO;
import com.didistore.dao.impl.marketing.CuponesDAOImpl;
import com.didistore.model.marketing.Cupones;
import com.didistore.model.marketing.enums.EstadoCupones;
import com.didistore.model.marketing.enums.TipoDescuentos;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Controlador de cupones: validaciones de negocio RF-09 y cálculo de descuentos.
public class CuponesController {

    private final ICuponesDAO cuponesDAO;

    public CuponesController() {
        this.cuponesDAO = new CuponesDAOImpl();
    }

    public List<Cupones> listarCupones() {
        return cuponesDAO.listarCupones();
    }

    public Cupones buscarPorId(int idCupon) {
        if (idCupon <= 0) return null;
        return cuponesDAO.buscarPorId(idCupon);
    }

    public Cupones buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) return null;
        return cuponesDAO.buscarPorCodigo(codigo.trim().toUpperCase());
    }

    /**
     * Crea un cupón validando las restricciones de RF-09:
     * código único, fechas coherentes, valor no negativo, monto mínimo >= 0.
     * Devuelve {success, message}.
     */
    public Map<String, Object> crearCupon(Cupones cupon) {
        Map<String, Object> resultado = new HashMap<>();

        String errorValidacion = validarCupon(cupon, true);
        if (errorValidacion != null) {
            resultado.put("success", false);
            resultado.put("message", errorValidacion);
            return resultado;
        }

        cupon.setcodigo(cupon.getcodigo().trim().toUpperCase());
        if (cupon.getcantidadUsosActuales() < 0) {
            cupon.setcantidadUsosActuales(0);
        }
        if (cupon.getestado() == null) {
            cupon.setestado(EstadoCupones.Activo);
        }

        boolean ok = cuponesDAO.insertarCupon(cupon);
        resultado.put("success", ok);
        resultado.put("message", ok ? "Cupón creado correctamente" : "No se pudo crear el cupón");
        return resultado;
    }

    public Map<String, Object> actualizarCupon(Cupones cupon) {
        Map<String, Object> resultado = new HashMap<>();

        if (cupon == null || cupon.getidCupon() <= 0) {
            resultado.put("success", false);
            resultado.put("message", "idCupon inválido");
            return resultado;
        }

        Cupones existente = cuponesDAO.buscarPorId(cupon.getidCupon());
        if (existente == null) {
            resultado.put("success", false);
            resultado.put("message", "Cupón no encontrado");
            return resultado;
        }

        String errorValidacion = validarCupon(cupon, false);
        if (errorValidacion != null) {
            resultado.put("success", false);
            resultado.put("message", errorValidacion);
            return resultado;
        }

        cupon.setcodigo(cupon.getcodigo().trim().toUpperCase());
        if (cupon.getestado() == null) {
            cupon.setestado(existente.getestado());
        }

        boolean ok = cuponesDAO.actualizarCupon(cupon);
        resultado.put("success", ok);
        resultado.put("message", ok ? "Cupón actualizado correctamente" : "No se pudo actualizar el cupón");
        return resultado;
    }

    public Map<String, Object> eliminarCupon(int idCupon) {
        Map<String, Object> resultado = new HashMap<>();
        if (idCupon <= 0 || cuponesDAO.buscarPorId(idCupon) == null) {
            resultado.put("success", false);
            resultado.put("message", "Cupón no encontrado");
            return resultado;
        }
        boolean ok = cuponesDAO.eliminarCupon(idCupon);
        resultado.put("success", ok);
        resultado.put("message", ok ? "Cupón eliminado" : "No se pudo eliminar el cupón");
        return resultado;
    }

    /**
     * Valida un cupón para aplicarlo al carrito/checkout (CU-CLI-02 / CU-CLI-03).
     * Devuelve {success, message, cupon, descuento} — descuento calculado sobre el subtotal.
     */
    public Map<String, Object> validarYCalcular(String codigo, double subtotal) {
        Map<String, Object> resultado = new HashMap<>();

        if (codigo == null || codigo.isBlank()) {
            resultado.put("success", false);
            resultado.put("message", "Debes ingresar un código de cupón");
            return resultado;
        }

        Cupones cupon = cuponesDAO.buscarPorCodigo(codigo.trim().toUpperCase());
        if (cupon == null) {
            resultado.put("success", false);
            resultado.put("message", "El cupón no existe");
            return resultado;
        }

        String error = validarVigenciaYUsos(cupon, subtotal);
        if (error != null) {
            resultado.put("success", false);
            resultado.put("message", error);
            return resultado;
        }

        double descuento = calcularDescuento(cupon, subtotal);
        resultado.put("success", true);
        resultado.put("message", "Cupón aplicado correctamente");
        resultado.put("descuento", descuento);
        resultado.put("cupon", cupon);
        return resultado;
    }

    // Registra el uso del cupón tras un checkout exitoso.
    public boolean registrarUso(int idCupon) {
        if (idCupon <= 0) return false;
        return cuponesDAO.incrementarUsos(idCupon);
    }

    public double calcularDescuento(Cupones cupon, double subtotal) {
        if (cupon == null || subtotal <= 0) return 0.0;

        double descuento = 0.0;
        if (cupon.gettipoDescuento() == TipoDescuentos.Porcentaje) {
            descuento = subtotal * (cupon.getvalorDescuento() / 100.0);
        } else if (cupon.gettipoDescuento() == TipoDescuentos.MontoFijo) {
            descuento = cupon.getvalorDescuento();
        }

        if (descuento > subtotal) {
            descuento = subtotal;
        }
        if (descuento < 0) {
            descuento = 0.0;
        }
        return Math.round(descuento * 100.0) / 100.0;
    }

    // -------------------------------------------------------------------------
    // Validaciones de negocio (RF-09 / FA-1 / FA-2)
    // -------------------------------------------------------------------------

    private String validarCupon(Cupones cupon, boolean esCreacion) {
        if (cupon == null) {
            return "Datos del cupón inválidos";
        }
        if (cupon.getcodigo() == null || cupon.getcodigo().trim().isEmpty()) {
            return "El código de cupón es obligatorio";
        }
        if (cupon.gettipoDescuento() == null) {
            return "El tipo de descuento es obligatorio";
        }
        if (cupon.getvalorDescuento() < 0) {
            return "El valor del descuento no puede ser negativo";
        }
        if (cupon.gettipoDescuento() == TipoDescuentos.Porcentaje
                && cupon.getvalorDescuento() > 100) {
            return "El porcentaje de descuento no puede superar el 100%";
        }
        if (cupon.getmontoMinimo() < 0) {
            return "El monto mínimo no puede ser negativo";
        }
        if (cupon.getcantidadMaximaUsos() < 1) {
            return "La cantidad máxima de usos debe ser al menos 1";
        }
        if (cupon.getfechaInicio() == null || cupon.getfechaFin() == null) {
            return "Las fechas de inicio y fin son obligatorias";
        }
        // FA-1: fecha fin posterior a fecha inicio
        if (!cupon.getfechaFin().isAfter(cupon.getfechaInicio())) {
            return "La fecha de finalización debe ser posterior a la fecha de inicio";
        }

        String codigoLimpio = cupon.getcodigo().trim().toUpperCase();
        // FA-2: código único
        Cupones duplicado = cuponesDAO.buscarPorCodigo(codigoLimpio);
        if (duplicado != null && (!esCreacion || duplicado.getidCupon() != cupon.getidCupon())) {
            if (esCreacion || duplicado.getidCupon() != cupon.getidCupon()) {
                return "Este código de cupón ya está en uso. Elija un código diferente";
            }
        }

        return null;
    }

    private String validarVigenciaYUsos(Cupones cupon, double subtotal) {
        if (cupon.getestado() != EstadoCupones.Activo) {
            return "El cupón está inactivo";
        }

        LocalDateTime ahora = LocalDateTime.now();
        if (cupon.getfechaInicio() != null && ahora.isBefore(cupon.getfechaInicio())) {
            return "El cupón aún no está vigente";
        }
        if (cupon.getfechaFin() != null && ahora.isAfter(cupon.getfechaFin())) {
            return "El cupón ha expirado";
        }

        if (cupon.getcantidadUsosActuales() >= cupon.getcantidadMaximaUsos()) {
            // FA-4: desactivar automáticamente al agotar usos
            cupon.setestado(EstadoCupones.Inactivo);
            cuponesDAO.actualizarCupon(cupon);
            return "El cupón ha alcanzado su límite de usos";
        }

        if (subtotal < cupon.getmontoMinimo()) {
            double falta = cupon.getmontoMinimo() - subtotal;
            return "Este cupón requiere un monto mínimo de compra. Te faltan $" + String.format("%,.0f", falta);
        }

        return null;
    }
}
