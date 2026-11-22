package com.didistore.controller.marketing;

import com.didistore.model.marketing.Cupones;
import com.didistore.model.marketing.enums.EstadoCupones;
import com.didistore.model.marketing.enums.TipoDescuentos;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * RF-09: cálculo de descuentos y validación de cupones (sin tocar la BD
 * cuando la validación falla antes de la consulta de duplicado).
 */
class CuponesControllerTest {

    private final CuponesController controller = new CuponesController();

    private Cupones cuponPorcentaje(double valor) {
        Cupones c = new Cupones();
        c.setcodigo("TEST10");
        c.settipoDescuento(TipoDescuentos.Porcentaje);
        c.setvalorDescuento(valor);
        c.setmontoMinimo(0);
        c.setcantidadMaximaUsos(100);
        c.setfechaInicio(LocalDateTime.now().minusDays(1));
        c.setfechaFin(LocalDateTime.now().plusDays(30));
        c.setestado(EstadoCupones.Activo);
        return c;
    }

    private Cupones cuponFijo(double valor) {
        Cupones c = cuponPorcentaje(0);
        c.setcodigo("FIJO5K");
        c.settipoDescuento(TipoDescuentos.MontoFijo);
        c.setvalorDescuento(valor);
        return c;
    }

    // ---------- calcularDescuento (puro) ----------
    @Test
    void descuentoPorcentaje() {
        Cupones c = cuponPorcentaje(10);
        assertEquals(50.0, controller.calcularDescuento(c, 500.0), 0.001);
        assertEquals(0.0, controller.calcularDescuento(c, 0), 0.001);
    }

    @Test
    void descuentoMontoFijo() {
        Cupones c = cuponFijo(5000);
        assertEquals(5000.0, controller.calcularDescuento(c, 20000.0), 0.001);
    }

    @Test
    void descuentoNoSuperaSubtotal() {
        Cupones c = cuponPorcentaje(100);
        assertEquals(300.0, controller.calcularDescuento(c, 300.0), 0.001);

        Cupones fijo = cuponFijo(99999);
        assertEquals(100.0, controller.calcularDescuento(fijo, 100.0), 0.001);
    }

    @Test
    void descuentoInvalidoEsCero() {
        assertEquals(0.0, controller.calcularDescuento(null, 500), 0.001);
        assertEquals(0.0, controller.calcularDescuento(cuponPorcentaje(10), -1), 0.001);
        assertEquals(0.0, controller.calcularDescuento(cuponPorcentaje(10), 0), 0.001);
    }

    @Test
    void descuentoRedondeaADosDecimales() {
        Cupones c = cuponPorcentaje(15);
        // 333.333... * 15% = 49.9999… → 50.00
        double d = controller.calcularDescuento(c, 333.333);
        assertEquals(Math.round(d * 100.0) / 100.0, d, 0.0001);
    }

    // ---------- validarCupon vía crearCupon (falla antes de la BD) ----------
    @Test
    void crearCuponNuloRechazado() {
        Map<String, Object> r = controller.crearCupon(null);
        assertEquals(Boolean.FALSE, r.get("success"));
        assertNotNull(r.get("message"));
    }

    @Test
    void crearCuponSinCodigoRechazado() {
        Cupones c = cuponPorcentaje(10);
        c.setcodigo("  ");
        Map<String, Object> r = controller.crearCupon(c);
        assertEquals(Boolean.FALSE, r.get("success"));
    }

    @Test
    void crearCuponValorNegativoRechazado() {
        Cupones c = cuponPorcentaje(-5);
        Map<String, Object> r = controller.crearCupon(c);
        assertEquals(Boolean.FALSE, r.get("success"));
    }

    @Test
    void crearCuponPorcentajeMayor100Rechazado() {
        Cupones c = cuponPorcentaje(150);
        Map<String, Object> r = controller.crearCupon(c);
        assertEquals(Boolean.FALSE, r.get("success"));
    }

    @Test
    void crearCuponMontoMinimoNegativoRechazado() {
        Cupones c = cuponPorcentaje(10);
        c.setmontoMinimo(-100);
        Map<String, Object> r = controller.crearCupon(c);
        assertEquals(Boolean.FALSE, r.get("success"));
    }

    @Test
    void crearCuponUsosMaximosCeroRechazado() {
        Cupones c = cuponPorcentaje(10);
        c.setcantidadMaximaUsos(0);
        Map<String, Object> r = controller.crearCupon(c);
        assertEquals(Boolean.FALSE, r.get("success"));
    }

    @Test
    void crearCuponSinFechasRechazado() {
        Cupones c = cuponPorcentaje(10);
        c.setfechaInicio(null);
        Map<String, Object> r = controller.crearCupon(c);
        assertEquals(Boolean.FALSE, r.get("success"));
    }

    @Test
    void crearCuponFinAntesInicioRechazado() {
        Cupones c = cuponPorcentaje(10);
        c.setfechaInicio(LocalDateTime.now().plusDays(10));
        c.setfechaFin(LocalDateTime.now().plusDays(1));
        Map<String, Object> r = controller.crearCupon(c);
        assertEquals(Boolean.FALSE, r.get("success"));
    }

    @Test
    void crearCuponTipoNuloRechazado() {
        Cupones c = cuponPorcentaje(10);
        c.settipoDescuento(null);
        Map<String, Object> r = controller.crearCupon(c);
        assertEquals(Boolean.FALSE, r.get("success"));
    }

    // ---------- validarYCalcular: código vacío (sin BD) ----------
    @Test
    void validarCodigoVacioRechazado() {
        Map<String, Object> r = controller.validarYCalcular("  ", 1000);
        assertEquals(Boolean.FALSE, r.get("success"));
        Map<String, Object> r2 = controller.validarYCalcular(null, 1000);
        assertEquals(Boolean.FALSE, r2.get("success"));
    }

    @Test
    void buscarPorCodigoInvalidoDevuelveNull() {
        assertNull(controller.buscarPorCodigo(null));
        assertNull(controller.buscarPorCodigo(""));
        assertNull(controller.buscarPorCodigo("   "));
    }

    @Test
    void buscarPorIdInvalidoDevuelveNull() {
        assertNull(controller.buscarPorId(0));
        assertNull(controller.buscarPorId(-5));
    }

    @Test
    void registrarUsoInvalidoFalso() {
        assertFalse(controller.registrarUso(0));
        assertFalse(controller.registrarUso(-1));
    }
}
