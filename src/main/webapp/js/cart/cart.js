import { API_ENDPOINTS, COUPON_DISCOUNTS, STORAGE_KEYS } from '../core/config.js';
import { formatCurrency, showToast } from '../core/ui.js';

const itemsContainer = document.getElementById('cart-items');

// Variable global para mantener el estado actual de la vista
let currentCartData = { items: [], total: 0 };

if (itemsContainer) {
  initCart();
}

function initCart() {
  bindActions();
  // En lugar de leer el HTML o LocalStorage, pedimos el carrito al servidor
  fetchCart(); 
}

function bindActions() {
  document.getElementById('cart-clear-btn')?.addEventListener('click', async () => {
    // Llamada a la API para vaciar el carrito en la BD
    await updateServerCart('vaciar');
    showToast('Carrito vaciado correctamente.', 'info');
  });

  document.getElementById('cart-coupon-btn')?.addEventListener('click', applyCoupon);
}

// 1. OBTENER DATOS DEL SERVIDOR (GET)
async function fetchCart() {
  try {
    // Asegúrate de que API_ENDPOINTS.cart apunte a '/api/carrito'
    const response = await fetch(`${API_ENDPOINTS.cart}?accion=ver`);
    if (!response.ok) throw new Error('Error al cargar el carrito');
    
    currentCartData = await response.json();
    renderCart();
  } catch (error) {
    console.error(error);
    showToast('Error al conectar con el servidor', 'error');
  }
}

// 2. ENVIAR CAMBIOS AL SERVIDOR (POST)
async function updateServerCart(accion, itemId = null, nuevaCantidad = null) {
  const params = new URLSearchParams();
  params.append('accion', accion);
  
  if (itemId) params.append('itemId', itemId);
  if (nuevaCantidad !== null) params.append('cantidad', nuevaCantidad);

  try {
    const response = await fetch(API_ENDPOINTS.cart, {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params
    });
    
    const result = await response.json();
    if (result.success) {
      // Si el servidor guardó el cambio con éxito, volvemos a descargar el carrito actualizado
      await fetchCart();
    } else {
      showToast(result.message || 'Error al actualizar', 'error');
    }
  } catch (error) {
    console.error(error);
    showToast('No se pudo sincronizar con el servidor', 'warning');
  }
}

function applyCoupon() {
  const couponInput = document.getElementById('coupon');
  const couponCode = (couponInput?.value || '').trim().toUpperCase();

  if (!couponCode) {
    showToast('Ingresa un cupón para aplicar.', 'error');
    return;
  }

  if (!COUPON_DISCOUNTS[couponCode]) {
    showToast('Cupón no válido.', 'error');
    return;
  }

  localStorage.setItem(STORAGE_KEYS.coupon, couponCode);
  renderCart(); // Repintamos para reflejar el descuento visual
  showToast(`Cupón ${couponCode} aplicado correctamente.`, 'success');
}

function updateSummary() {
  // Usamos el subtotal que viene directamente de la base de datos
  const subtotal = currentCartData.total; 
  const shipping = subtotal > 0 ? 10000 : 0;
  
  const couponCode = localStorage.getItem(STORAGE_KEYS.coupon) || '';
  const couponDiscount = COUPON_DISCOUNTS[couponCode] || 0;
  const discount = subtotal * couponDiscount;
  
  const total = Math.max(subtotal + shipping - discount, 0);

  document.getElementById('cart-subtotal')?.replaceChildren(document.createTextNode(formatCurrency(subtotal)));
  document.getElementById('cart-shipping')?.replaceChildren(document.createTextNode(formatCurrency(shipping)));
  
  const discountEl = document.getElementById('cart-discount');
  if (discountEl) {
      discountEl.replaceChildren(document.createTextNode(`-${formatCurrency(discount)}`));
  }
  
  document.getElementById('cart-total')?.replaceChildren(document.createTextNode(formatCurrency(total)));
}

function renderCart() {
  const items = currentCartData.items || [];
  const emptyState = document.getElementById('cart-empty');
  const layout = document.getElementById('cart-layout');

  if (!items.length) {
    itemsContainer.innerHTML = '';
    layout?.classList.add('u-hidden');
    emptyState?.classList.remove('u-hidden');
    updateSummary();
    return;
  }

  layout?.classList.remove('u-hidden');
  emptyState?.classList.add('u-hidden');

  itemsContainer.innerHTML = items.map((item) => `
    <div class="cart__item" data-item-id="${item.itemId}">
      <div class="cart__item-product">
        <div>
          <p class="cart__item-name">${item.nombreProducto || `Producto #${item.productoId}`}</p>
        </div>
      </div>
      <span class="cart__item-price">${formatCurrency(item.precioUnitario)}</span>
      <div class="cart__item-quantity">
        <button class="cart__quantity-btn" type="button" data-action="decrease">-</button>
        <input type="number" class="cart__quantity-input" value="${item.cantidad}" min="1" max="99">
        <button class="cart__quantity-btn" type="button" data-action="increase">+</button>
      </div>
      <span class="cart__item-subtotal">${formatCurrency(item.precioUnitario * item.cantidad)}</span>
      <button class="cart__item-remove" type="button" data-action="remove" aria-label="Eliminar producto">&#10005;</button>
    </div>
  `).join('');

  // Re-vincular eventos a los nuevos botones
  itemsContainer.querySelectorAll('.cart__item').forEach((row) => {
    row.querySelectorAll('button[data-action]').forEach((button) => {
      button.addEventListener('click', () => handleItemAction(row, button.dataset.action));
    });
    
    row.querySelector('.cart__quantity-input')?.addEventListener('change', (e) => {
        handleItemAction(row, 'input', Number(e.target.value));
    });
  });

  updateSummary();
}

function handleItemAction(row, action, inputValue = null) {
  const itemId = Number(row.dataset.itemId);
  // Buscamos el item actual en nuestra variable de estado
  const item = currentCartData.items.find(i => i.itemId === itemId);
  if (!item) return;

  let nuevaCantidad = item.cantidad;

  if (action === 'increase') nuevaCantidad += 1;
  if (action === 'decrease') nuevaCantidad -= 1;
  if (action === 'input') nuevaCantidad = Math.min(99, Math.max(1, inputValue));

  // Si la cantidad llega a 0 o la acción es eliminar, llamamos a la API con 'eliminar'
  if (action === 'remove' || nuevaCantidad <= 0) {
    updateServerCart('eliminar', itemId);
  } else if (nuevaCantidad !== item.cantidad) {
    // Si la cantidad cambió, llamamos a la API con 'actualizar'
    updateServerCart('actualizar', itemId, nuevaCantidad);
  }
}