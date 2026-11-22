import { API_ENDPOINTS, buildApiUrl } from '../core/config.js';
import { formatCurrency, showToast } from '../core/ui.js';

const params = new URLSearchParams(window.location.search);
const pedidoId = params.get('id');

document.addEventListener('DOMContentLoaded', init);

async function init() {
  const root = document.getElementById('order-detail-content');
  if (!root) return;

  if (!pedidoId) {
    root.innerHTML = '<p>No se indicó qué pedido mostrar.</p>';
    return;
  }

  try {
    const res = await fetch(buildApiUrl(
      `${API_ENDPOINTS.pedido}?accion=detalle&pedidoId=${encodeURIComponent(pedidoId)}`));
    const data = await res.json();

    if (!res.ok || !data.success) {
      root.innerHTML = `<p>${data.message || 'No se pudo cargar el pedido.'}</p>`;
      return;
    }

    render(data);
  } catch (err) {
    console.error(err);
    root.innerHTML = '<p>Error de conexión.</p>';
    showToast('Error de conexión', 'error');
  }
}

function render(data) {
  const p = data.pedido;
  const items = Array.isArray(data.items) ? data.items : [];
  const root = document.getElementById('order-detail-content');
  const actions = document.getElementById('order-detail-actions');

  document.title = `Pedido ${p.numeroPedido} - DidiStore`;

  const fecha = p.fecha ? p.fecha.slice(0, 10) : '—';
  const metodoPago = p.metodoPago || '—';

  root.innerHTML = `
    <h1 class="order-detail__title">Detalle del Pedido #${escapeHtml(p.numeroPedido)}</h1>

    <div class="order-detail__info">
      <div class="order-detail__info-group">
        <span class="order-detail__info-label">Fecha del pedido:</span>
        <span class="order-detail__info-value">${escapeHtml(fecha)}</span>
      </div>
      <div class="order-detail__info-group">
        <span class="order-detail__info-label">Estado:</span>
        <span class="order-detail__status">${escapeHtml(p.estado)}</span>
      </div>
      <div class="order-detail__info-group">
        <span class="order-detail__info-label">Método de pago:</span>
        <span class="order-detail__info-value">${escapeHtml(metodoPago)}</span>
      </div>
    </div>

    <section class="order-detail__section">
      <h2 class="order-detail__section-title">Productos</h2>
      <div class="order-detail__products">
        ${items.map((item) => `
          <div class="order-detail__product">
            <div class="order-detail__product-info">
              <p class="order-detail__product-name">${escapeHtml(item.nombre || ('Producto #' + item.productoId))}</p>
              <p class="order-detail__product-sku">REF: ${item.productoId}</p>
            </div>
            <div class="order-detail__product-quantity">
              <span>Cantidad: ${item.cantidad}</span>
            </div>
            <div class="order-detail__product-price">
              <span>${formatCurrency(item.precioUnitario)} c/u</span>
              <strong>${formatCurrency(item.subtotal)}</strong>
            </div>
          </div>
        `).join('')}
      </div>
    </section>

    <section class="order-detail__section order-detail__section--totals">
      <h2 class="order-detail__section-title">Resumen del Pedido</h2>
      <div class="order-detail__totals">
        <div class="order-detail__totals-row">
          <span>Subtotal</span><span>${formatCurrency(p.subtotal)}</span>
        </div>
        <div class="order-detail__totals-row">
          <span>Descuento</span><span>-${formatCurrency(p.descuento)}</span>
        </div>
        <div class="order-detail__totals-row">
          <span>IVA</span><span>${formatCurrency(p.iva)}</span>
        </div>
        <div class="order-detail__totals-row">
          <span>Envío</span><span>${formatCurrency(p.costoEnvio)}</span>
        </div>
        <div class="order-detail__totals-row order-detail__totals-row--total">
          <strong>Total</strong><strong>${formatCurrency(p.montoTotal)}</strong>
        </div>
      </div>
    </section>
  `;

  // Acciones RF-17: factura (print), recompra, rastreo
  actions.style.display = 'flex';
  actions.innerHTML = `
    <button class="btn btn--primary" id="btn-factura">Descargar factura (imprimir / PDF)</button>
    <button class="btn btn--secondary" id="btn-recompra">Recomprar</button>
    <button class="btn btn--secondary" id="btn-rastreo">Ver rastreo</button>
    <div id="rastreo-box" style="width:100%;"></div>
  `;

  document.getElementById('btn-factura')?.addEventListener('click', () => window.print());

  document.getElementById('btn-recompra')?.addEventListener('click', async () => {
    try {
      const res = await fetch(buildApiUrl(`${API_ENDPOINTS.pedido}?accion=recompra`), {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ pedidoId: String(p.id) })
      });
      const data = await res.json();
      showToast(data.message || (data.success ? 'Agregado al carrito' : 'No se pudo recomprar'),
        data.success ? 'success' : 'error');
      if (data.success) {
        setTimeout(() => { window.location.href = '../cart/cart.html'; }, 600);
      }
    } catch {
      showToast('Error de conexión', 'error');
    }
  });

  document.getElementById('btn-rastreo')?.addEventListener('click', async () => {
    const box = document.getElementById('rastreo-box');
    try {
      const res = await fetch(buildApiUrl(
        `${API_ENDPOINTS.envios}?accion=rastreo&pedidoId=${encodeURIComponent(p.id)}`));
      const data = await res.json();
      if (data.success && data.envio) {
        const e = data.envio;
        box.innerHTML = `
          <div style="background:#f6f6f6;padding:0.75rem;border-radius:6px;margin-top:0.75rem;font-size:0.9rem;">
            <p><strong>Estado:</strong> ${escapeHtml(e.estadoEnvio || e.estado || '—')}</p>
            <p><strong>Transportadora:</strong> ${escapeHtml(e.transportadora || '—')}</p>
            <p><strong>Guía:</strong> ${escapeHtml(e.numeroGuia || e.guia || 'Pendiente')}</p>
          </div>`;
      } else {
        box.innerHTML = `<p style="margin-top:0.75rem;font-size:0.9rem;">${escapeHtml(data.message || 'Envío no disponible aún.')}</p>`;
      }
    } catch {
      box.innerHTML = '<p style="margin-top:0.75rem;">Error consultando el rastreo.</p>';
    }
  });
}

function escapeHtml(s) {
  if (s === null || s === undefined) return '';
  return String(s)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}
