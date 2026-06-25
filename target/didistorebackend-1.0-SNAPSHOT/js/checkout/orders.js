import { API_ENDPOINTS, COUPON_DISCOUNTS, STORAGE_KEYS, buildApiUrl } from '../core/config.js';
import { request } from '../core/http.js';
import {formatCurrency} from "../core/ui.js";

document.addEventListener("DOMContentLoaded", cargarPedidos);

async function cargarPedidos() {

    const response = await fetch(`${API_ENDPOINTS.pedido}?accion=historial`);

    const data = await response.json();

    const container = document.getElementById("orders-container");

    if(!data.success) {

    container.innerHTML = "No hay pedidos";
    return;
    }   

    container.innerHTML = data.pedidos.map(p =>`

        <div class="order-card">
            <h3>
                Pedido:
                    ${p.numeroPedido}
            </h3>

            <p>
            Estado:
                ${p.estado}
            </p>

            <p>
                Fecha:
                    ${p.fecha}
            </p>

            <strong>
                ${formatCurrency(
                    p.montoTotal
                )}
            </strong>
        </div>
    `).join("");
}