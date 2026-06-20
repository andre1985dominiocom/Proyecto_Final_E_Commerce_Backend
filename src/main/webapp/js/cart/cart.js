import { API_ENDPOINTS } from '../core/config.js';
import { formatCurrency, showToast } from '../core/ui.js';
import { setCart } from '../core/cart-state.js';

const itemsContainer = document.getElementById('cart-items');

let currentCartData = {
    items: [],
    total: 0
};

// ================================
// INIT
// ================================

if (itemsContainer) {
    initCart();
}

function initCart() {
    bindActions();
    fetchCart();
}

// ================================
// EVENTOS GLOBALES
// ================================

function bindActions() {

    document.getElementById('cart-clear-btn')?.addEventListener('click', async () => {
        await updateServerCart('vaciar');

        showToast('Carrito vaciado correctamente', 'info');
    });

    document.getElementById('cart-coupon-btn')?.addEventListener(
        'click',
        applyCoupon
    );
}

// ================================
// OBTENER CARRITO
// ================================

async function fetchCart() {

    try {

        const response = await fetch(`${API_ENDPOINTS.cart}?accion=ver`);
        const text = await response.text();

        console.log("RESPUESTA CARRITO:", text);

        const data = JSON.parse(text);

        currentCartData = data;

        // 🔥 SINCRONIZA ESTADO GLOBAL (IMPORTANTE)
        setCart(data);

        renderCart();

    } catch (error) {

        console.error("Error carrito:", error);

        showToast('Error cargando carrito', 'error');
    }
}

// ================================
// ACTUALIZAR CARRITO BACKEND
// ================================

async function updateServerCart(accion, idItem = null, cantidad = null) {

    const params = new URLSearchParams();

    params.append("accion", accion);

    if (idItem !== null) {
        params.append("idItem", idItem);
    }

    if (cantidad !== null) {
        params.append("cantidad", cantidad);
    }

    try {

        const response = await fetch(API_ENDPOINTS.cart, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: params
        });

        const text = await response.text();

        console.log("POST CARRITO:", text);

        const result = JSON.parse(text);

        if (result.success) {

            // 🔥 REFRESCA TODO EL CARRITO DESDE BACKEND
            await fetchCart();

        } else {

            showToast(result.message, "error");
        }

    } catch (error) {

        console.error(error);

        showToast("No se pudo actualizar carrito", "warning");
    }
}

// ================================
// CUPONES
// ================================

function applyCoupon() {

    const input = document.getElementById('coupon');

    const codigo = (input?.value || '').trim().toUpperCase();

    if (!codigo) {
        showToast("Ingrese cupón", "error");
        return;
    }

    showToast("Cupón aplicado", "success");

    localStorage.setItem("coupon", codigo);

    renderCart();
}

// ================================
// RESUMEN
// ================================

function updateSummary() {

    const subtotal = currentCartData.total || 0;

    const envio = subtotal > 0 ? 10000 : 0;

    const codigo = localStorage.getItem("coupon") || '';

    const descuento = subtotal * (0); // (puedes conectar COUPON_DISCOUNTS aquí)

    const total = subtotal + envio - descuento;

    document.getElementById('cart-subtotal')
        ?.replaceChildren(document.createTextNode(formatCurrency(subtotal)));

    document.getElementById('cart-shipping')
        ?.replaceChildren(document.createTextNode(formatCurrency(envio)));

    document.getElementById('cart-total')
        ?.replaceChildren(document.createTextNode(formatCurrency(total)));
}

// ================================
// RENDER CARRITO
// ================================

function renderCart() {

    const items = currentCartData.items || [];

    const empty = document.getElementById('cart-empty');
    const layout = document.getElementById('cart-layout');

    if (items.length === 0) {

        itemsContainer.innerHTML = "";
        layout?.classList.add("u-hidden");
        empty?.classList.remove("u-hidden");

        updateSummary();
        return;
    }

    layout?.classList.remove("u-hidden");
    empty?.classList.add("u-hidden");

    itemsContainer.innerHTML = items.map(item => `
        <div class="cart__item" data-item-id="${item.idItem}">

            <p>Producto #${item.productoId}</p>

            <span>${formatCurrency(item.precioUnitario)}</span>

            <button data-action="decrease">-</button>

            <input class="cart__quantity-input" value="${item.cantidad}">

            <button data-action="increase">+</button>

            <span>${formatCurrency(item.precioUnitario * item.cantidad)}</span>

            <button data-action="remove">X</button>

        </div>
    `).join("");

    bindItemActions();

    updateSummary();
}

// ================================
// EVENTOS ITEMS
// ================================

function bindItemActions() {

    itemsContainer.querySelectorAll(".cart__item").forEach(row => {

        row.querySelectorAll("[data-action]").forEach(btn => {

            btn.addEventListener("click", () => {

                handleItemAction(row, btn.dataset.action);
            });
        });
    });
}

// ================================
// ACCIONES POR ITEM
// ================================

function handleItemAction(row, action) {

    const itemId = Number(row.dataset.itemId);

    const item = currentCartData.items.find(i => i.idItem === itemId);

    if (!item) return;

    let cantidad = Number(item.cantidad);

    if (action === "increase") cantidad++;

    if (action === "decrease") {

        if (cantidad > 1) cantidad--;
    }

    if (action === "remove") {

        updateServerCart("eliminar", itemId);
        return;
    }

    updateServerCart("actualizar", itemId, cantidad);
}