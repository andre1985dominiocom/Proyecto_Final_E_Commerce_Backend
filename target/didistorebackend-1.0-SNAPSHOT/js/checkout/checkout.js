import { API_ENDPOINTS, COUPON_DISCOUNTS, STORAGE_KEYS, buildApiUrl } from '../core/config.js';
import { request } from '../core/http.js';
import { getJSON, setJSON } from '../core/storage.js';
import { formatCurrency, setButtonLoading, showToast } from '../core/ui.js';

const form = document.getElementById('checkout-form');

if (form) {
  initCheckout();
}

function initCheckout() {
  hydrateSummary();
  hydrateFormDraft();
  bindPaymentToggles();
  bindCardFormatters();
  form.addEventListener('input', persistDraft);
  form.addEventListener('submit', submitCheckout);
}

function hydrateSummary() {
  const cart = getJSON(STORAGE_KEYS.cart, []);
  const list = document.getElementById('checkout-summary-items');

  if (!Array.isArray(cart) || !cart.length) return;

  const subtotal = cart.reduce((acc, item) => acc + (item.price || 0) * (item.quantity || 1), 0);
  const shipping = subtotal > 0 ? 10000 : 0;
  const couponCode = localStorage.getItem(STORAGE_KEYS.coupon) || '';
  const couponDiscount = COUPON_DISCOUNTS[couponCode] || 0;
  const discount = subtotal * couponDiscount;
  const total = subtotal + shipping - discount;

  if (list) {
    list.innerHTML = cart.map((item) => `
      <li class="checkout__summary-item">
        <span class="checkout__summary-item-name">${item.name}</span>
        <span class="checkout__summary-item-qty">x${item.quantity}</span>
        <span class="checkout__summary-item-price">${formatCurrency((item.price || 0) * (item.quantity || 1))}</span>
      </li>
    `).join('');
  }

  document.getElementById('checkout-summary-subtotal')?.replaceChildren(document.createTextNode(formatCurrency(subtotal)));
  document.getElementById('checkout-summary-shipping')?.replaceChildren(document.createTextNode(formatCurrency(shipping)));
  document.getElementById('checkout-summary-discount')?.replaceChildren(document.createTextNode(`-${formatCurrency(discount)}`));
  document.getElementById('checkout-summary-total')?.replaceChildren(document.createTextNode(formatCurrency(total)));
}

function bindPaymentToggles() {
  const radios = document.querySelectorAll('input[name="payment-method"]');
  const cardFields = document.getElementById('card-fields');

  const update = () => {
    const selected = document.querySelector('input[name="payment-method"]:checked')?.value;
    cardFields?.classList.toggle('u-hidden', selected !== 'credit-card');
  };

  radios.forEach((radio) => radio.addEventListener('change', update));
  update();
}

function bindCardFormatters() {
  const cardNumber = document.getElementById('card-number');
  const cardExpiry = document.getElementById('card-expiry');

  cardNumber?.addEventListener('input', () => {
    cardNumber.value = cardNumber.value.replace(/\D/g, '').slice(0, 16);
  });

  cardNumber?.addEventListener('blur', () => {
    cardNumber.value = formatCardNumber(cardNumber.value);
  });

  cardExpiry?.addEventListener('input', () => {
    const cleaned = cardExpiry.value.replace(/\D/g, '').slice(0, 4);
    cardExpiry.value = cleaned.length > 2 ? `${cleaned.slice(0, 2)}/${cleaned.slice(2)}` : cleaned;
  });
}

function formatCardNumber(rawValue = '') {
  return rawValue.replace(/\D/g, '').slice(0, 16).replace(/(.{4})/g, '$1 ').trim();
}

function persistDraft() {
  const payload = Object.fromEntries(new FormData(form).entries());
  setJSON(STORAGE_KEYS.checkoutDraft, payload);
}

function hydrateFormDraft() {
  const draft = getJSON(STORAGE_KEYS.checkoutDraft, null);
  if (!draft) return;

  Object.entries(draft).forEach(([name, value]) => {
    const input = form.elements.namedItem(name);
    if (!input) return;

    if (input instanceof RadioNodeList) {
      Array.from(input).forEach((radio) => {
        radio.checked = radio.value === value;
      });
      return;
    }

    if (input.type === 'checkbox') {
      input.checked = value === 'on' || value === true || value === 'true';
      return;
    }

    input.value = value;
  });
}

async function submitCheckout(event) {
  event.preventDefault();

  const submitButton = form.querySelector('button[type="submit"]');
  // Extraemos los datos del formulario (debe contener un input name="direccionEnvioId")
  const formData = new FormData(form);
  
  setButtonLoading(submitButton, true, 'Confirmando...');

  // Adaptamos el envío al formato que espera PedidoServlet.java
  const params = new URLSearchParams();
  params.append('accion', 'checkout');
  
  // Asegúrate de que en tu HTML tengas un campo con name="direccionEnvioId"
  params.append('direccionEnvioId', formData.get('direccionEnvioId') || 1); 
  
  const couponCode = localStorage.getItem(STORAGE_KEYS.coupon);
  // Si tienes la lógica de IDs de cupones, la envías aquí:
  params.append('cuponId', couponCode ? 1 : 0); 

  try {
    const response = await fetch(buildApiUrl(API_ENDPOINTS.checkout), {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: params
    });

    const result = await response.json();

    if (!result.success) {
      // Manejo de error si el Servlet rechaza la transacción
      showToast(result.message || 'Error al procesar el pedido.', 'error');
    } else {
      showToast('Pedido confirmado correctamente.', 'success');
      
      // Limpiamos los borradores visuales
      localStorage.removeItem(STORAGE_KEYS.checkoutDraft);
      
      setTimeout(() => {
        window.location.href = './confirmation.html';
      }, 1200);
    }
  } catch (error) {
    console.error("Error en el checkout:", error);
    // Fallback offline (Excelente práctica que ya tenías)
    const localOrders = getJSON(STORAGE_KEYS.orders, []);
    localOrders.push({ id: `LOCAL-${Date.now()}`, status: 'Pendiente_Pago' });
    setJSON(STORAGE_KEYS.orders, localOrders);
    showToast('Backend no disponible: pedido guardado localmente.', 'warning');
  } finally {
    setButtonLoading(submitButton, false);
  }
}

function normalizeDocumentType(value) {
  const map = { cc: 'CC', foreign_id: 'CE', passport: 'PP', card_id: 'TI' };
  return map[value] || value;
}
