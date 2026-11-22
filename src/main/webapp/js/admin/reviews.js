import { API_ENDPOINTS, buildApiUrl } from '../core/config.js';
import { request } from '../core/http.js';
import { showToast } from '../core/ui.js';

const tbody = document.getElementById('resenas-body');
const filter = document.getElementById('resenas-estado-filter');
const refreshBtn = document.getElementById('resenas-refresh');

if (tbody) {
  init();
}

function init() {
  filter?.addEventListener('change', load);
  refreshBtn?.addEventListener('click', load);
  load();
}

async function load() {
  const estado = filter?.value || 'Pendiente';
  tbody.innerHTML = '<tr><td colspan="7">Cargando reseñas...</td></tr>';

  const qs = estado ? `?estado=${encodeURIComponent(estado)}` : '';
  const res = await request(`${API_ENDPOINTS.adminResenas}${qs}`);
  const data = res.data || {};
  const lista = Array.isArray(data.resenas) ? data.resenas : [];

  if (!lista.length) {
    tbody.innerHTML = '<tr><td colspan="7">No hay reseñas para este filtro.</td></tr>';
    return;
  }

  tbody.innerHTML = lista.map((r) => {
    const id = r.idResena ?? r.id ?? '';
    const estadoActual = r.estado || 'Pendiente';
    const badge = estadoActual === 'Aprobada' ? 'admin-badge--success'
      : estadoActual === 'Rechazada' ? 'admin-badge--danger'
        : 'admin-badge--warning';

    return `
      <tr data-id="${id}">
        <td>${id}</td>
        <td>Usuario #${r.usuarioId ?? '?'}</td>
        <td>Producto #${r.productoId ?? '?'}</td>
        <td>${'★'.repeat(r.calificacion || 0)}${'☆'.repeat(5 - (r.calificacion || 0))}</td>
        <td style="max-width:280px;">${escapeHtml(r.comentario || '')}</td>
        <td><span class="admin-badge ${badge}">${estadoActual}</span></td>
        <td>
          <div class="admin-table__actions">
            ${estadoActual === 'Pendiente' ? `
              <button class="admin-btn admin-btn--primary admin-btn--small" data-mod="aprobar">Aprobar</button>
              <button class="admin-btn admin-btn--danger admin-btn--small" data-mod="rechazar">Rechazar</button>
            ` : '<span style="opacity:0.6;font-size:0.85rem;">Sin acciones</span>'}
          </div>
        </td>
      </tr>
    `;
  }).join('');

  tbody.querySelectorAll('button[data-mod]').forEach((btn) => {
    btn.addEventListener('click', onModerar);
  });
}

async function onModerar(e) {
  const btn = e.currentTarget;
  const accion = btn.dataset.mod;
  const row = btn.closest('tr');
  const id = row?.dataset.id;
  if (!id) return;

  if (accion === 'rechazar' && !confirm(`¿Rechazar reseña #${id}?`)) return;

  btn.disabled = true;
  try {
    const params = new URLSearchParams();
    params.append('accion', accion);
    params.append('idResena', id);

    const res = await fetch(buildApiUrl(`${API_ENDPOINTS.adminResenas}?${params}`), {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' }
    });
    const data = await res.json().catch(() => ({}));

    if (res.ok && data.success) {
      showToast(data.message || (accion === 'aprobar' ? 'Aprobada' : 'Rechazada'), 'success');
      await load();
    } else {
      showToast(data.message || 'No se pudo moderar', 'error');
      btn.disabled = false;
    }
  } catch {
    showToast('Error de conexión', 'error');
    btn.disabled = false;
  }
}

function escapeHtml(s) {
  if (s === null || s === undefined) return '';
  return String(s)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}
