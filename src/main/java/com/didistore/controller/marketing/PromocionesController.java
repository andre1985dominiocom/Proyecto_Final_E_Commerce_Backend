package com.didistore.controller.marketing;

import com.didistore.dao.impl.marketing.PromocionesDAOImpl;
import com.didistore.dao.interfaces.marketing.IPromocionesDAO;
import com.didistore.model.marketing.Promociones;
import com.didistore.model.marketing.enums.EstadoPromociones;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Controlador de promociones: validaciones RF-09 y consulta del banner activo.
public class PromocionesController {

    private final IPromocionesDAO promocionesDAO;

    public PromocionesController() {
        this.promocionesDAO = new PromocionesDAOImpl();
    }

    public List<Promociones> listarPromociones() {
        return promocionesDAO.listarPromociones();
    }

    public Promociones buscarPorId(int idPromocion) {
        if (idPromocion <= 0) return null;
        return promocionesDAO.buscarPorId(idPromocion);
    }

    // Promociones vigentes para el banner de la página principal (CU-CLI-01).
    public List<Promociones> listarActivasParaBanner() {
        return promocionesDAO.listarActivas();
    }

    public Map<String, Object> crearPromocion(Promociones promocion) {
        Map<String, Object> resultado = new HashMap<>();

        String error = validar(promocion);
        if (error != null) {
            resultado.put("success", false);
            resultado.put("message", error);
            return resultado;
        }

        if (promocion.getestado() == null) {
            promocion.setestado(EstadoPromociones.Activo);
        }

        boolean ok = promocionesDAO.insertarPromocion(promocion);
        resultado.put("success", ok);
        resultado.put("message", ok ? "Promoción creada correctamente" : "No se pudo crear la promoción");
        return resultado;
    }

    public Map<String, Object> actualizarPromocion(Promociones promocion) {
        Map<String, Object> resultado = new HashMap<>();

        if (promocion == null || promocion.getidPromocion() <= 0) {
            resultado.put("success", false);
            resultado.put("message", "idPromocion inválido");
            return resultado;
        }

        Promociones existente = promocionesDAO.buscarPorId(promocion.getidPromocion());
        if (existente == null) {
            resultado.put("success", false);
            resultado.put("message", "Promoción no encontrada");
            return resultado;
        }

        String error = validar(promocion);
        if (error != null) {
            resultado.put("success", false);
            resultado.put("message", error);
            return resultado;
        }

        if (promocion.getestado() == null) {
            promocion.setestado(existente.getestado());
        }

        boolean ok = promocionesDAO.actualizarPromocion(promocion);
        resultado.put("success", ok);
        resultado.put("message", ok ? "Promoción actualizada" : "No se pudo actualizar la promoción");
        return resultado;
    }

    public Map<String, Object> eliminarPromocion(int idPromocion) {
        Map<String, Object> resultado = new HashMap<>();
        if (idPromocion <= 0 || promocionesDAO.buscarPorId(idPromocion) == null) {
            resultado.put("success", false);
            resultado.put("message", "Promoción no encontrada");
            return resultado;
        }
        boolean ok = promocionesDAO.eliminarPromocion(idPromocion);
        resultado.put("success", ok);
        resultado.put("message", ok ? "Promoción eliminada" : "No se pudo eliminar la promoción");
        return resultado;
    }

    private String validar(Promociones promocion) {
        if (promocion == null) {
            return "Datos de la promoción inválidos";
        }
        if (promocion.getnombrePromocion() == null || promocion.getnombrePromocion().trim().isEmpty()) {
            return "El nombre de la promoción es obligatorio";
        }
        if (promocion.gettipoDescuento() == null) {
            return "El tipo de descuento es obligatorio";
        }
        if (promocion.getvalorDescuento() < 0) {
            return "El valor del descuento no puede ser negativo";
        }
        if (promocion.getfechaInicio() == null || promocion.getfechaFin() == null) {
            return "Las fechas de inicio y fin son obligatorias";
        }
        // FA-1
        if (!promocion.getfechaFin().isAfter(promocion.getfechaInicio())) {
            return "La fecha de finalización debe ser posterior a la fecha de inicio";
        }
        return null;
    }
}
