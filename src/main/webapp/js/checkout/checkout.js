import { API_ENDPOINTS, buildApiUrl } from '../core/config.js';
import { formatCurrency, setButtonLoading, showToast } from '../core/ui.js';

document.addEventListener("DOMContentLoaded", () => {

    cargarResumen();

    document.getElementById("checkout-form")?.addEventListener("submit", procesarPedido);
});

async function cargarResumen() {

    try {

        const response = await fetch(buildApiUrl(`${API_ENDPOINTS.cart}?accion=ver`));

        const data = await response.json();

        const subtotal = data.total || 0;
        const envio = 15000;

        document.getElementById("checkout-summary-subtotal").textContent = formatCurrency(subtotal);
        document.getElementById("checkout-summary-shipping").textContent = formatCurrency(envio);
        document.getElementById("checkout-summary-total").textContent = formatCurrency(subtotal + envio);

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

    } catch(error) {
        console.error("Error cargando resumen del carrito:", error);
    }

}

async function procesarPedido(e) {

    e.preventDefault();

    const submitButton = document.querySelector('#checkout-form button[type="submit"]');
    if (submitButton) setButtonLoading(submitButton, true, 'Procesando...');

    const form = document.getElementById("checkout-form");

    const params = new URLSearchParams();
    params.append("accion", "checkout");

    // direccionEnvioId: intentar obtener del campo si existe, si no usar 0
    const direccionSelect = document.getElementById("direccionEnvioId");
    const direccionId = direccionSelect ? (direccionSelect.value || 0) : 0;
    params.append("direccionEnvioId", direccionId);

    // Cupon si existe
    const cuponInput = document.getElementById("coupon-code") || document.getElementById("cupon");
    if (cuponInput && cuponInput.value) {
        params.append("cuponId", cuponInput.value.trim());
    }

    try {

        const response = await fetch(buildApiUrl(API_ENDPOINTS.pedido), {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: params
        });

        const result = await response.json();

        if (result.success) {

            showToast("Pedido creado correctamente", "success");

            setTimeout(() => {
                window.location.href = "../orders/orders.html";
            }, 1500);
        } else {

            showToast(result.message || "Error al crear el pedido", "error");
        }

    } catch(error) {
        console.error("Error procesando pedido:", error);
        showToast("Error procesando pedido", "error");
    } finally {
        if (submitButton) setButtonLoading(submitButton, false);
    }
}