import { API_ENDPOINTS, buildAppUrl } from '../core/config.js';
import { request } from '../core/http.js';
import { canAccessAdminView, isAdmin, isAuthenticated } from '../core/guards.js';
import { renderStateRow, showToast } from '../core/ui.js';

const tbody = document.getElementById('products-table-body');
const searchInput = document.getElementById('products-search-input');

const state = {
  all: [],
  filtered: []
};

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

  bindEvents();
  await loadProducts();
}

function bindEvents() {
  searchInput?.addEventListener('input', applyFilters);
}

async function loadProducts() {
  renderStateRow(tbody, 'Cargando productos...', 'loading', 8);

  const result = await request(API_ENDPOINTS.products);

  if (!result.ok || !Array.isArray(result.data)) {
    renderStateRow(tbody, 'No se pudieron cargar los productos.', 'empty', 8);
    showToast(result.error || 'Error al cargar productos.', 'error');
    return;
  }

  state.all = result.data;
  applyFilters();
}

function applyFilters() {
  const query = (searchInput?.value || '').trim().toLowerCase();

  state.filtered = state.all.filter((product) => {
    const nombre = String(product.nombre || product.nombreProducto || '').toLowerCase();
    const categoria = String(product.categoriaNombre || product.categoria || '').toLowerCase();
    const sku = String(product.sku || '').toLowerCase();

    return !query
      || nombre.includes(query)
      || categoria.includes(query)
      || sku.includes(query);
  });

  renderProducts();
}

function renderProducts() {
  if (!state.filtered.length) {
    renderStateRow(tbody, 'No se encontraron productos.', 'empty', 8);
    return;
  }

  tbody.innerHTML = state.filtered.map((product) => {
    const id = product.idProducto ?? product.id ?? '-';
    const nombre = product.nombre ?? product.nombreProducto ?? '-';
    const categoria = product.nombreCategoria ?? product.categoria ?? '-';
    const precio = formatCurrency(product.precio);
    const sku = product.sku ?? '-';
    const stock = product.stock ?? product.stockActual ?? '-';
    const estado = product.estado ?? 'Activo';

    return `
      <tr>
        <td>${id}</td>
        <td>${nombre}</td>
        <td>${categoria}</td>
        <td>${precio}</td>
        <td>${sku}</td>
        <td>${stock}</td>
        <td>
          <span class="admin-badge ${getStatusClass(estado)}">${estado}</span>
        </td>
        <td>
          <div class="admin-table__actions">
            <button
              class="admin-btn admin-btn--secondary admin-btn--small"
              data-action="edit"
              data-id="${id}">
              Editar
            </button>

            ${isAdmin() ? `
              <button
                class="admin-btn admin-btn--danger admin-btn--small"
                data-action="delete"
                data-id="${id}">
                Eliminar
              </button>
            ` : ''}
          </div>
        </td>
      </tr>
    `;
  }).join('');

  tbody.querySelectorAll('button[data-action]').forEach((button) => {
    button.addEventListener('click', handleAction);
  });
}

function getStatusClass(estado) {
  const value = String(estado || '').toLowerCase();

  if (value === 'activo' || value === 'disponible') {
    return 'admin-badge--success';
  }

  if (value === 'inactivo') {
    return 'admin-badge--warning';
  }

  return 'admin-badge--info';
}

function formatCurrency(value) {
  const number = Number(value);

  if (Number.isNaN(number)) {
    return value ?? '-';
  }

  return new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0
  }).format(number);
}

async function handleAction(event) {
  const button = event.currentTarget;
  const action = button.dataset.action;
  const id = button.dataset.id;

  if (action === 'edit') {
    window.location.href = `product-form.html?idProducto=${encodeURIComponent(id)}`;
    return;
  }

  if (action === 'delete') {
    if (!isAdmin()) {
      showToast('No tienes permisos para eliminar productos.', 'error');
      return;
    }

    const confirmed = window.confirm('¿Deseas eliminar este producto?');
    if (!confirmed) return;

    const result = await request(`${API_ENDPOINTS.products}?idProducto=${encodeURIComponent(id)}`, {
      method: 'DELETE'
    });

    if (!result.ok) {
      showToast(result.error || 'No se pudo eliminar el producto.', 'error');
      return;
    }

    showToast(result.data?.message || 'Producto eliminado correctamente.', 'success');
    await loadProducts();
  }
}