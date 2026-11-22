import { API_ENDPOINTS, buildApiUrl, STORAGE_KEYS } from '../core/config.js';
import { formatCurrency, setButtonLoading, showToast } from '../core/ui.js';

let shippingOptions = [
    { metodo: 'Estandar', costo: 8000, diasEstimados: '5-7 días' },
    { metodo: 'Express', costo: 18000, diasEstimados: '2-3 días' }
];
let currentSubtotal = 0;
let currentDescuento = 0;
let direcciones = [];

// Ciudad seed → select values del checkout
const CIUDAD_SELECT_TO_ID = {
    bogota: 1, medellin: 2, cali: 3, barranquilla: 4, cartagena: 5,
    bucaramanga: 7, pereira: 8, manizales: 9, cucuta: 6, armenia: 8,
    neiva: 1, ibague: 1, villavicencio: 1, popayan: 3, santamarta: 5,
    monteria: 4, valledupar: 4, pasto: 1
};

const PAYMENT_TO_METODO = {
    'credit-card': 'TarjetaCredito',
    'pse': 'PSE',
    'cash': 'Efectivo',
    'nequi': 'Transferencia'
};

document.addEventListener("DOMContentLoaded", () => {
    cargarResumen();
    cargarDirecciones();

    document.getElementById("checkout-form")?.addEventListener("submit", procesarPedido);

    document.querySelectorAll('input[name="metodoEnvio"]').forEach(radio => {
        radio.addEventListener("change", () => {
            recargarOpcionesEnvio();
            actualizarResumen();
        });
    });

    document.querySelectorAll('input[name="payment-method"]').forEach(radio => {
        radio.addEventListener("change", toggleCardFields);
    });
    toggleCardFields();
});

async function cargarResumen() {
    try {
        const response = await fetch(buildApiUrl(`${API_ENDPOINTS.cart}?accion=ver`));
        const data = await response.json();

        currentSubtotal = data.total || 0;
        currentDescuento = data.descuento || 0;

        // Cupón guardado en sesión/carrito
        if (data.cuponCodigo) {
            localStorage.setItem(STORAGE_KEYS.coupon, data.cuponCodigo);
            const cuponInput = document.getElementById("coupon-code");
            if (cuponInput && !cuponInput.value) {
                cuponInput.value = data.cuponCodigo;
            }
        }

        await cargarOpcionesEnvio(currentSubtotal);
        actualizarResumen();

        const lista = document.getElementById("checkout-summary-items");
        if (lista && data.items) {
            lista.innerHTML = data.items.map(item => `
                <li class="checkout__summary-item">
                    <span>Producto ${item.productoId}</span>
                    <span>x${item.cantidad}</span>
                    <span>${formatCurrency(item.precioUnitario * item.cantidad)}</span>
                </li>
            `).join("");
        }
    } catch (error) {
        console.error("Error cargando resumen del carrito:", error);
    }
}

async function cargarDirecciones() {
    const select = document.getElementById("direccion-envio-select");
    if (!select) return;

    try {
        const res = await fetch(buildApiUrl(API_ENDPOINTS.direcciones));
        if (!res.ok) {
            // sin sesión o sin direcciones → formulario manual
            return;
        }
        direcciones = await res.json();
        if (!Array.isArray(direcciones) || direcciones.length === 0) {
            return;
        }

        select.innerHTML = '<option value="">Usar dirección del formulario</option>' +
            direcciones.map(d => `
                <option value="${d.idDireccion}"
                    data-ciudad="${d.ciudadId || ''}">
                    ${d.direccion || ''} — ${d.barrio || ''} (dir. #${d.idDireccion})
                </option>
            `).join("");

        select.addEventListener("change", onDireccionChange);
    } catch (e) {
        console.error("Error cargando direcciones:", e);
    }
}

function onDireccionChange() {
    const select = document.getElementById("direccion-envio-select");
    const hidden = document.getElementById("direccionEnvioId");
    if (!select || !hidden) return;

    const opt = select.options[select.selectedIndex];
    const id = select.value || '0';
    hidden.value = id;

    // Recotizar envío con ciudad de la dirección elegida
    if (id && id !== '0' && opt) {
        const ciudadId = opt.dataset.ciudad;
        if (ciudadId) {
            recargarOpcionesEnvio(ciudadId);
        }
    } else {
        recargarOpcionesEnvio();
    }
    actualizarResumen();
}

async function recargarOpcionesEnvio(ciudadIdForzada) {
    let ciudadId = ciudadIdForzada;
    if (!ciudadId) {
        const citySel = document.getElementById("city");
        if (citySel && citySel.value) {
            ciudadId = CIUDAD_SELECT_TO_ID[citySel.value] || null;
        }
        const hidden = document.getElementById("direccionEnvioId");
        if (!ciudadId && hidden && hidden.value && hidden.value !== '0') {
            const dir = direcciones.find(d => String(d.idDireccion) === hidden.value);
            if (dir) ciudadId = dir.ciudadId;
        }
    }

    try {
        const params = new URLSearchParams({
            accion: 'opciones',
            subtotal: String(currentSubtotal)
        });
        if (ciudadId) params.set('ciudadId', String(ciudadId));
        const response = await fetch(buildApiUrl(`${API_ENDPOINTS.envios}?${params}`));
        const data = await response.json();
        if (data.success && Array.isArray(data.opciones) && data.opciones.length) {
            shippingOptions = data.opciones;
        }
    } catch (error) {
        console.error("Error cargando opciones de envío:", error);
    }
    renderCostosEnvio();
}

function renderCostosEnvio() {
    const estandar = shippingOptions.find(o => o.metodo === 'Estandar');
    const express = shippingOptions.find(o => o.metodo === 'Express');

    const elE = document.getElementById('shipping-cost-estandar');
    const elX = document.getElementById('shipping-cost-express');
    const note = document.getElementById('shipping-free-note');

    if (elE && estandar) {
        elE.textContent = estandar.costo === 0 ? 'GRATIS' : formatCurrency(estandar.costo);
    }
    if (elX && express) {
        elX.textContent = formatCurrency(express.costo);
    }
    if (note) {
        if (estandar && estandar.costo === 0) {
            note.classList.remove('u-hidden');
        } else {
            note.classList.add('u-hidden');
        }
    }
}

function metodoSeleccionado() {
    const checked = document.querySelector('input[name="metodoEnvio"]:checked');
    return checked ? checked.value : 'Estandar';
}

function costoEnvioSeleccionado() {
    const op = shippingOptions.find(o => o.metodo === metodoSeleccionado());
    return op ? op.costo : 0;
}

function actualizarResumen() {
    const envio = costoEnvioSeleccionado();
    const subtotal = currentSubtotal;
    const descuento = currentDescuento;
    const total = Math.max(0, subtotal - descuento) + envio;

    const shipEl = document.getElementById("checkout-summary-shipping");
    const totalEl = document.getElementById("checkout-summary-total");
    const subtotalEl = document.getElementById("checkout-summary-subtotal");
    const discEl = document.getElementById("checkout-summary-discount");

    if (subtotalEl) subtotalEl.textContent = formatCurrency(subtotal);
    if (shipEl) shipEl.textContent = envio === 0 && subtotal > 0 ? 'GRATIS' : formatCurrency(envio);
    if (discEl) discEl.textContent = '-' + formatCurrency(descuento);
    if (totalEl) totalEl.textContent = formatCurrency(total);
}

function toggleCardFields() {
    const checked = document.querySelector('input[name="payment-method"]:checked');
    const cardFields = document.getElementById('card-fields');
    if (!cardFields || !checked) return;
    if (checked.value === 'credit-card') {
        cardFields.classList.remove('u-hidden');
    } else {
        cardFields.classList.add('u-hidden');
    }
}

async function crearDireccionDesdeFormulario() {
    const direccion = document.getElementById('address')?.value?.trim();
    const barrio = document.getElementById('neighbor')?.value?.trim();
    const referencia = document.getElementById('reference')?.value?.trim() || '';
    const citySel = document.getElementById('city');
    const ciudadId = citySel ? (CIUDAD_SELECT_TO_ID[citySel.value] || null) : null;

    if (!direccion || !barrio || !ciudadId) {
        return null;
    }

    const params = new URLSearchParams();
    params.append('action', 'agregar');
    params.append('direccion', direccion);
    params.append('barrio', barrio);
    params.append('referencia', referencia);
    params.append('ciudadId', String(ciudadId));

    try {
        const res = await fetch(buildApiUrl(API_ENDPOINTS.direcciones), {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: params
        });
        const data = await res.json();
        if (data.success && data.idDireccion) {
            return data.idDireccion;
        }
        if (data.success) {
            // refrescar lista y tomar la principal/nueva
            const listRes = await fetch(buildApiUrl(API_ENDPOINTS.direcciones));
            const list = await listRes.json();
            if (Array.isArray(list) && list.length) {
                return list[list.length - 1].idDireccion;
            }
        }
        if (data.message) {
            showToast(data.message, 'error');
        }
    } catch (e) {
        console.error(e);
    }
    return null;
}

async function procesarPedido(e) {
    e.preventDefault();

    const submitButton = document.querySelector('#checkout-form button[type="submit"]');
    if (submitButton) setButtonLoading(submitButton, true, 'Procesando...');

    try {
        // 1. Resolver dirección de envío
        let direccionId = 0;
        const hidden = document.getElementById("direccionEnvioId");
        const select = document.getElementById("direccion-envio-select");

        if (select && select.value) {
            direccionId = Number(select.value);
        } else if (hidden && hidden.value && hidden.value !== '0') {
            direccionId = Number(hidden.value);
        } else {
            direccionId = await crearDireccionDesdeFormulario();
            if (!direccionId) {
                showToast('Selecciona o completa una dirección de envío válida', 'error');
                if (submitButton) setButtonLoading(submitButton, false);
                return;
            }
        }

        // 2. Crear pedido
        const params = new URLSearchParams();
        params.append("accion", "checkout");
        params.append("direccionEnvioId", String(direccionId));
        params.append("metodoEnvio", metodoSeleccionado());

        const cuponInput = document.getElementById("coupon-code");
        const cuponValor = cuponInput?.value?.trim()
            || localStorage.getItem(STORAGE_KEYS.coupon) || '';
        if (cuponValor) {
            params.append("cuponCodigo", cuponValor);
        }

        const response = await fetch(buildApiUrl(API_ENDPOINTS.pedido), {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        });
        const result = await response.json();

        if (!result.success) {
            showToast(result.message || "Error al crear el pedido", "error");
            return;
        }

        showToast("Pedido creado correctamente", "success");

        // 3. Pago mock automático (excepto contra entrega)
        const metodoPagoRadio = document.querySelector('input[name="payment-method"]:checked');
        const metodoKey = metodoPagoRadio ? metodoPagoRadio.value : 'credit-card';
        const pedidoId = result.pedidoId;

        if (metodoKey !== 'cash' && pedidoId) {
            try {
                const payParams = new URLSearchParams();
                payParams.append('accion', 'pagar');
                payParams.append('pedidoId', String(pedidoId));
                payParams.append('metodoPago', PAYMENT_TO_METODO[metodoKey] || 'TarjetaCredito');
                await fetch(buildApiUrl(API_ENDPOINTS.pagos), {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: payParams
                });
            } catch (payErr) {
                console.error('Pago automático:', payErr);
            }
        }

        localStorage.removeItem(STORAGE_KEYS.coupon);

        setTimeout(() => {
            window.location.href = "../orders/orders.html";
        }, 1200);

    } catch (error) {
        console.error("Error procesando pedido:", error);
        showToast("Error procesando pedido", "error");
    } finally {
        if (submitButton) setButtonLoading(submitButton, false);
    }
}
