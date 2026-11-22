import { API_ENDPOINTS, buildApiUrl } from '../core/config.js';
import { formatCurrency, showToast } from '../core/ui.js';

const STATUS_CLASS = {
  Pendiente_Pago: 'orders__status--pendiente',
  Pagado: 'orders__status--en-camino',
  En_Preparacion: 'orders__status--en-camino',
  Despachado: 'orders__status--en-camino',
  En_Transito: 'orders__status--en-camino',
  Entregado: 'orders__status--entregado',
  Cancelado: 'orders__status--cancelado',
  Devuelto: 'orders__status--cancelado'
};

document.addEventListener('DOMContentLoaded', () => {
  bindFilters();
  cargarPedidos();
});

function bindFilters() {
  const form = document.getElementById('orders-filters');
  const clear = document.getElementById('filter-clear');

  form?.addEventListener('submit', (e) => {
    e.preventDefault();
    cargarPedidos();
  });

  clear?.addEventListener('click', () => {
    form?.reset();
    cargarPedidos();
  });
}

function currentFilters() {
  const desde = document.getElementById('filter-fecha-desde')?.value || '';
  const hasta = document.getElementById('filter-fecha-hasta')?.value || '';
  const estado = document.getElementById('filter-estado')?.value || '';
  const params = new URLSearchParams();
  params.append('accion', 'historial');
  if (desde) params.append('fechaDesde', desde);
  if (hasta) params.append('fechaHasta', hasta);
  if (estado) params.append('estado', estado);
  return params.toString();
}

async function cargarPedidos() {
  const container = document.getElementById('orders-container');
  const empty = document.getElementById('orders-empty');
  if (!container) return;

  container.innerHTML = '<p>Cargando pedidos...</p>';

  try {
    const response = await fetch(buildApiUrl(`${API_ENDPOINTS.orders}?${currentFilters()}`));
    const data = await response.json();

    if (!data.success || !Array.isArray(data.pedidos) || !data.pedidos.length) {
      container.innerHTML = '';
      if (empty) empty.style.display = '';
      return;
    }

    if (empty) empty.style.display = 'none';

    container.innerHTML = data.pedidos.map((p) => {
      const statusClass = STATUS_CLASS[p.estado] || '';
      return `
        <div class="order-card">
          <h3>Pedido: ${p.numeroPedido}</h3>
          <p>Estado: <span class="orders__status ${statusClass}">${p.estado}</span></p>
          <p>Fecha: ${p.fecha ? p.fecha.slice(0, 10) : '—'}</p>
          <strong>${formatCurrency(p.montoTotal)}</strong>

          <div class="order-card__actions" style="margin-top:0.75rem;display:flex;gap:0.5rem;flex-wrap:wrap;">
            <a class="btn btn--secondary" href="order-detail.html?id=${p.id}">Ver detalle</a>
            ${p.estado === 'Pendiente_Pago' ? `
              <button class="btn btn--primary" data-accion="pagar" data-id="${p.id}">Pagar</button>
              <button class="btn btn--secondary" data-accion="cancelar" data-id="${p.id}">Cancelar</button>
            ` : ''}
            ${p.estado === 'Pagado' || p.estado === 'En_Preparacion' ? `
              <button class="btn btn--secondary" data-accion="cancelar" data-id="${p.id}">Cancelar</button>
            ` : ''}
            <button class="btn btn--secondary" data-accion="historial" data-id="${p.id}">Ver historial</button>
            <button class="btn btn--secondary" data-accion="recompra" data-id="${p.id}">Recomprar</button>
            ${['Despachado', 'En_Transito', 'Entregado'].includes(p.estado) ? `
              <button class="btn btn--secondary" data-accion="rastreo" data-id="${p.id}">Rastreo</button>
            ` : ''}
          </div>
          <div class="order-history u-hidden" id="historial-${p.id}" style="margin-top:0.5rem;"></div>
          <div class="order-tracking u-hidden" id="rastreo-${p.id}" style="margin-top:0.5rem;"></div>
        </div>
      `;
    }).join('');

    container.querySelectorAll('[data-accion]').forEach((btn) => {
      btn.addEventListener('click', onAccion);
    });
  } catch (err) {
    console.error(err);
    container.innerHTML = '<p>No se pudieron cargar los pedidos.</p>';
    showToast('Error de conexión', 'error');
  }
}

async function onAccion(e) {
  const btn = e.currentTarget;
  const accion = btn.dataset.accion;
  const pedidoId = btn.dataset.id;

  try {
    if (accion === 'pagar') {
      const params = new URLSearchParams();
      params.append('accion', 'pagar');
      params.append('pedidoId', pedidoId);
      params.append('metodoPago', 'TarjetaCredito');
      const res = await fetch(buildApiUrl(`${API_ENDPOINTS.pagos}`), {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
      });
      const data = await res.json();
      showToast(data.message || (data.success ? 'Pago exitoso' : 'Error en el pago'),
        data.success ? 'success' : 'error');
      if (data.success) cargarPedidos();
    } else if (accion === 'cancelar') {
      if (!confirm('¿Cancelar este pedido?')) return;
      const params = new URLSearchParams();
      params.append('accion', 'cancelar');
      params.append('pedidoId', pedidoId);
      const res = await fetch(buildApiUrl(`${API_ENDPOINTS.estadosPedido}`), {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: params
      });
      const data = await res.json();
      showToast(data.message || (data.success ? 'Pedido cancelado' : 'No se pudo cancelar'),
        data.success ? 'success' : 'error');
      if (data.success) cargarPedidos();
    } else if (accion === 'historial') {
      const box = document.getElementById(`historial-${pedidoId}`);
      if (!box) return;
      if (!box.classList.contains('u-hidden')) {
        box.classList.add('u-hidden');
        return;
      }
      const res = await fetch(buildApiUrl(
        `${API_ENDPOINTS.historialPedido}?accion=listarPorPedido&pedidoId=${pedidoId}`));
      const data = await res.json();
      if (data.success && Array.isArray(data.historial)) {
        box.innerHTML = data.historial.map((h) => `
          <p style="font-size:0.85rem;margin:0.25rem 0;">
            <strong>${h.estadoAnterior || '—'} → ${h.estadoNuevo || '—'}</strong>
            <span style="opacity:0.7">(${h.fechaCambio || ''})</span>
            ${h.notas ? `<br><span style="opacity:0.8">${h.notas}</span>` : ''}
          </p>
        `).join('') || '<p>Sin historial</p>';
        box.classList.remove('u-hidden');
      }
    } else if (accion === 'recompra') {
      const res = await fetch(buildApiUrl(
        `${API_ENDPOINTS.recompra}?accion=recompra`), {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: new URLSearchParams({ pedidoId })
      });
      const data = await res.json();
      showToast(data.message || (data.success ? 'Agregado al carrito' : 'No se pudo recomprar'),
        data.success ? 'success' : 'error');
      if (data.success) {
        window.location.href = '../cart/cart.html';
      }
    } else if (accion === 'rastreo') {
      const box = document.getElementById(`rastreo-${pedidoId}`);
      if (!box) return;
      if (!box.classList.contains('u-hidden')) {
        box.classList.add('u-hidden');
        return;
      }
      const res = await fetch(buildApiUrl(
        `${API_ENDPOINTS.envios}?accion=rastreo&pedidoId=${pedidoId}`));
      const data = await res.json();
      if (data.success && data.envio) {
        const e2 = data.envio;
        box.innerHTML = `
          <div style="background:#f6f6f6;padding:0.75rem;border-radius:6px;font-size:0.9rem;">
            <p><strong>Estado envío:</strong> ${e2.estadoEnvio || e2.estado || '—'}</p>
            <p><strong>Transportadora:</strong> ${e2.transportadora || '—'}</p>
            <p><strong>Guía:</strong> ${e2.numeroGuia || e2.guia || 'Pendiente'}</p>
            <p><strong>Método:</strong> ${e2.metodoEnvio || '—'}</p>
          </div>`;
      } else {
        box.innerHTML = `<p style="font-size:0.9rem;">${data.message || 'Envío no disponible aún.'}</p>`;
      }
      box.classList.remove('u-hidden');
    }
  } catch (err) {
    console.error(err);
    showToast('Error de conexión', 'error');
  }
}
