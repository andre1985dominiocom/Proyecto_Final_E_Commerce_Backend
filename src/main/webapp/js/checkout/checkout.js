import { API_ENDPOINTS, COUPON_DISCOUNTS, STORAGE_KEYS, buildApiUrl } from '../core/config.js';
import { request } from '../core/http.js';
//import { getJSON, setJSON } from '../core/storage.js';
import { formatCurrency, setButtonLoading, showToast } from '../core/ui.js';

document.addEventListener("DOMContentLoaded", () => {

    cargarResumen();

    document.getElementById("checkout-form")?.addEventListener("submit", procesarPedido);
});

async function cargarResumen() {

    try {

        const response = await fetch(`${API_ENDPOINTS.cart}?accion=ver`);

        const data = await response.json();

        document.getElementById("checkout-summary-subtotal").textContent = formatCurrency(data.total);

        const envio = 15000;

        document.getElementById("checkout-summary-shipping").textContent = formatCurrency(envio);

        document.getElementById("checkout-summary-total").textContent = formatCurrency(data.total + envio);

        const lista = document.getElementById("checkout-summary-items");

        lista.innerHTML = data.items.map(item=>`

            <li class="checkout__summary-item">

                <span>
                    Producto ${item.productoId}
                </span>

                <span>
                    x${item.cantidad}
                </span>

                <span>
                    ${formatCurrency(
                    item.precioUnitario *
                    item.cantidad
                )}
                </span>
            </li>
        `).join("");

    } catch(error) {
        console.error(error);
    }

}

async function procesarPedido(e) {

    e.preventDefault();

    const form = document.getElementById("checkout-form");

    const datos = new FormData(form);

    datos.append(
        "accion",
        "checkout"
    );

    try {

        const response = await fetch(API_ENDPOINTS.pedido, {
                method:"POST",
                body:datos
            }
        );

        const result = await response.json();

        if(result.success){

            showToast(
                "Pedido creado correctamente",
                "success"
            );

            setTimeout(() => {
                window.location.href = "../orders/orders.html";
            },1500);
        } else {

            showToast(
                result.message,
                "error"
            );
        }

    } catch(error) {
        console.error(error);
        
        showToast(
            "Error procesando pedido",
            "error"
        );
    }
}