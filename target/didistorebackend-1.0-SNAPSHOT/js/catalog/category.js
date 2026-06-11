import { API_ENDPOINTS, buildAppUrl } from '../core/config.js';
import { request } from '../core/http.js';
import { canAccessAdminView, isAuthenticated } from '../core/guards.js';
import { renderStateRow, showToast } from '../core/ui.js';

const tbody = document.getElementById('categories-table-body');

if (tbody) {
  init();
}

async function init() {
  if (!isAuthenticated()) {
    window.location.href = buildAppUrl('/html/auth/login.html');
    return;
  }

  if (!canAccessAdminView()) {
    window.location.href = buildAppUrl('/index.html');
    return;
  }

  await loadCategories();
}

async function loadCategories() {
  renderStateRow(tbody, 'Cargando categorías...', 'loading', 5);

  const result = await request(API_ENDPOINTS.categories);

  if (!result.ok || !Array.isArray(result.data)) {
    renderStateRow(tbody, 'No se pudieron cargar las categorías.', 'empty', 5);
    showToast(result.error || 'Error al cargar categorías.', 'error');
    return;
  }

  renderCategories(result.data);
}

function renderCategories(categories) {
  if (!categories.length) {
    renderStateRow(tbody, 'No hay categorías registradas.', 'empty', 5);
    return;
  }

  tbody.innerHTML = categories.map((category) => `
    <tr>
      <td>${category.idCategoria ?? '-'}</td>
      <td>${category.nombre ?? '-'}</td>
      <td>${category.descripcion ?? '-'}</td>
      <td>${category.estado ?? 'Activa'}</td>
      <td>
        <button class="admin-btn admin-btn--secondary admin-btn--small" data-id="${category.idCategoria}">Editar</button>
      </td>
    </tr>
  `).join('');
}