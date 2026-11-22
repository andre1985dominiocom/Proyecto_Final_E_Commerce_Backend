import { API_ENDPOINTS, buildApiUrl, STORAGE_KEYS } from '../core/config.js';
import { formatCurrency, showToast } from '../core/ui.js';
import { setCart } from '../core/cart-state.js';

const itemsContainer = document.getElementById('cart-items');

let currentCartData = {
    items: [],
    total: 0,
    cuponCodigo: null,
    descuento: 0,
    totalConDescuento: 0
};
let shippingEstimate = 0;

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
        localStorage.removeItem(STORAGE_KEYS.coupon);
        showToast('Carrito vaciado correctamente', 'info');
    });

    document.getElementById('cart-coupon-btn')?.addEventListener(
        'click',
        applyCoupon
    );

    document.getElementById('cart-coupon-remove')?.addEventListener(
        'click',
        removeCoupon
    );
}

// ================================
// OBTENER CARRITO
// ================================

async function fetchCart() {

    try {

        const response = await fetch(buildApiUrl(`${API_ENDPOINTS.cart}?accion=ver`));
        const text = await response.text();

        const data = JSON.parse(text);

        currentCartData = data;

        // Sincroniza estado global
        setCart(data);

        if (data.cuponCodigo) {
            localStorage.setItem(STORAGE_KEYS.coupon, data.cuponCodigo);
        }

        renderCart();
        await loadShippingEstimate();

    } catch (error) {

        console.error("Error carrito:", error);

        showToast('Error cargando carrito', 'error');
    }
}

async function loadShippingEstimate() {
    const subtotal = currentCartData.total || 0;
    shippingEstimate = 0;

    if (subtotal <= 0) {
        updateSummary();
        return;
    }

    try {
        const params = new URLSearchParams({
            accion: 'opciones',
            subtotal: String(subtotal)
        });
        const res = await fetch(buildApiUrl(`${API_ENDPOINTS.envios}?${params}`));
        const data = await res.json();
        if (data.success && Array.isArray(data.opciones)) {
            const estandar = data.opciones.find(o => o.metodo === 'Estandar');
            shippingEstimate = estandar ? estandar.costo : 12000;
        } else {
            shippingEstimate = subtotal >= 100000 ? 0 : 12000;
        }
    } catch {
        shippingEstimate = subtotal >= 100000 ? 0 : 12000;
    }

    updateSummary();
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

        const response = await fetch(buildApiUrl(API_ENDPOINTS.cart), {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: params
        });

        const text = await response.text();
        const result = JSON.parse(text);

        if (result.success) {
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
// CUPONES (RF-09 - API real)
// ================================

async function applyCoupon() {

    const input = document.getElementById('coupon');
    const codigo = (input?.value || '').trim().toUpperCase();

    if (!codigo) {
        showToast("Ingrese cupón", "error");
        return;
    }

    const subtotal = currentCartData.total || 0;

    try {
        const params = new URLSearchParams();
        params.append('accion', 'aplicar-cupon');
        params.append('codigo', codigo);
        params.append('subtotal', String(subtotal));

        const res = await fetch(buildApiUrl(API_ENDPOINTS.cart), {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: params
        });
        const data = await res.json();

        if (data.success) {
            localStorage.setItem(STORAGE_KEYS.coupon, codigo);
            showToast(data.message || 'Cupón aplicado', 'success');
            await fetchCart();
        } else {
            showToast(data.message || 'Cupón inválido', 'error');
            localStorage.removeItem(STORAGE_KEYS.coupon);
        }
    } catch (err) {
        console.error(err);
        showToast('Error validando cupón', 'error');
    }
}

async function removeCoupon() {
    try {
        const params = new URLSearchParams();
        params.append('accion', 'quitar-cupon');
        const res = await fetch(buildApiUrl(API_ENDPOINTS.cart), {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: params
        });
        const data = await res.json();
        if (data.success) {
            localStorage.removeItem(STORAGE_KEYS.coupon);
            showToast('Cupón retirado', 'info');
            await fetchCart();
        }
    } catch (err) {
        console.error(err);
    }
}

// ================================
// RESUMEN
// ================================

function updateSummary() {

    const subtotal = currentCartData.total || 0;
    const descuento = currentCartData.descuento || 0;
    const envio = subtotal > 0 ? shippingEstimate : 0;
    const total = Math.max(0, subtotal - descuento) + envio;

    document.getElementById('cart-subtotal')
        ?.replaceChildren(document.createTextNode(formatCurrency(subtotal)));

    document.getElementById('cart-shipping')
        ?.replaceChildren(document.createTextNode(envio === 0 && subtotal > 0 ? 'GRATIS' : formatCurrency(envio)));

    document.getElementById('cart-discount')
        ?.replaceChildren(document.createTextNode('-' + formatCurrency(descuento)));

    document.getElementById('cart-total')
        ?.replaceChildren(document.createTextNode(formatCurrency(total)));

    const applied = document.getElementById('cart-coupon-applied');
    const removeBtn = document.getElementById('cart-coupon-remove');
    const codigo = currentCartData.cuponCodigo;

    if (applied) {
        if (codigo) {
            applied.textContent = `Cupón ${codigo} aplicado (-${formatCurrency(descuento)})`;
            applied.classList.remove('u-hidden');
        } else {
            applied.classList.add('u-hidden');
        }
    }
    if (removeBtn) {
        if (codigo) {
            removeBtn.classList.remove('u-hidden');
        } else {
            removeBtn.classList.add('u-hidden');
        }
    }
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
