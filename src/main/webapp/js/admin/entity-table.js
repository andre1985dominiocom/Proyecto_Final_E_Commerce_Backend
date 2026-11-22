import { API_ENDPOINTS, CATALOG_ENDPOINTS, STORAGE_KEYS, buildApiUrl } from '../core/config.js';
import { request } from '../core/http.js';
import { getJSON, setJSON } from '../core/storage.js';
import { formatCurrency, renderStateRow, showToast } from '../core/ui.js';

const root = document.querySelector('[data-admin-entity]');
const READ_ENDPOINTS = {
  products: CATALOG_ENDPOINTS.productos,
  categories: CATALOG_ENDPOINTS.categorias,
  orders: `${API_ENDPOINTS.orders}?accion=todos`,
  promotions: API_ENDPOINTS.adminPromociones
};
const DELETE_QUERY_PARAMS = {
  products: 'idProducto',
  categories: 'idCategoria',
  promotions: 'idPromocion'
};

if (root) {
  initEntityPage(root.dataset.adminEntity);
  if (root.dataset.adminEntity === 'promotions') {
    initPromotionsForm();
  }
}

async function initEntityPage(entity) {
  const tbody = document.getElementById('admin-entity-body');
  const searchInput = document.getElementById('admin-entity-search');
  const filterSelect = document.getElementById('admin-entity-filter');
  const pagination = document.getElementById('admin-entity-pagination');
  const columnsCount = document.querySelectorAll('.admin-table thead th').length || 1;

  if (!tbody) return;

  const storage = getJSON(STORAGE_KEYS.adminData, {});
  const apiPath = entity === 'promotions'
    ? API_ENDPOINTS.adminPromociones
    : (API_ENDPOINTS[entity] || '');
  const readPath = READ_ENDPOINTS[entity] || apiPath;
  const pageSize = 5;

  const state = {
    rows: [],
    filtered: [],
    page: 1
  };

  const renderers = {
    products: renderProductRow,
    categories: renderCategoryRow,
    orders: renderOrderRow,
    promotions: renderPromotionRow
  };

  const rowRenderer = renderers[entity];
  const getRowId = (row) => getEntityId(entity, row);

  renderStateRow(tbody, 'Cargando información...', 'loading', columnsCount);

  const response = readPath ? await request(readPath) : { ok: false };

  if (response.ok && Array.isArray(response.data)) {
    state.rows = ensureEntityIds(entity, response.data);
  } else if (response.ok && response.data && Array.isArray(response.data.pedidos)) {
    state.rows = ensureEntityIds(entity, response.data.pedidos);
  } else if (response.ok && response.data && Array.isArray(response.data.resenas)) {
    state.rows = ensureEntityIds(entity, response.data.resenas);
  } else if (response.ok && response.data && Array.isArray(response.data.promociones)) {
    state.rows = ensureEntityIds(entity, response.data.promociones);
  } else {
    // La API falló: NO ocultar el error detrás de datos mock.
    state.rows = [];
    const detail = response.error || response.message || `HTTP ${response.status ?? 'error'}`;
    renderStateRow(tbody, `No se pudo cargar la información del servidor (${detail}).`, 'error', columnsCount);
    showToast(`Error al cargar datos del servidor: ${detail}`, 'error');
    if (pagination) pagination.innerHTML = '';
    return;
  }

  storage[entity] = state.rows;
  setJSON(STORAGE_KEYS.adminData, storage);

  const applyFilters = () => {
    const query = (searchInput?.value || '').trim().toLowerCase();
    const filter = (filterSelect?.value || '').trim().toLowerCase();

    state.filtered = state.rows.filter((row) => {
      const plain = JSON.stringify(row).toLowerCase();
      const status = String(row.status || row.estado || '').toLowerCase();
      return (!query || plain.includes(query)) && (!filter || status.includes(filter));
    });

    state.page = 1;
    renderTable();
    renderPagination();
  };

  function getPageRows() {
    const start = (state.page - 1) * pageSize;
    return state.filtered.slice(start, start + pageSize);
  }

  function renderTable() {
    if (!state.filtered.length) {
      renderStateRow(tbody, 'No hay registros para mostrar.', 'empty', columnsCount);
      return;
    }

    tbody.innerHTML = getPageRows().map((row) => rowRenderer(row)).join('');

    tbody.querySelectorAll('button[data-delete-id]').forEach((button) => {
      button.addEventListener('click', async () => {
        const id = button.dataset.deleteId;
        const deleted = await deleteEntity(entity, apiPath || readPath, id);

        if (!deleted) {
          return;
        }

        state.rows = state.rows.filter((item) => String(getRowId(item)) !== id);
        storage[entity] = state.rows;
        setJSON(STORAGE_KEYS.adminData, storage);
        applyFilters();
      });
    });

    // RF-12/14: cambio de estado de pedido (staff) desde el panel admin
    tbody.querySelectorAll('button[data-cambiar-id]').forEach((button) => {
      button.addEventListener('click', async () => {
        const id = button.dataset.cambiarId;
        const select = tbody.querySelector(`[data-estado-select="${CSS.escape(id)}"]`);
        const estadoNuevo = select?.value;
        if (!estadoNuevo) return;

        const params = new URLSearchParams();
        params.append('accion', 'cambiar');
        params.append('pedidoId', id);
        params.append('estadoNuevo', estadoNuevo);
        params.append('notas', 'Cambio desde panel admin');

        button.disabled = true;
        try {
          const res = await fetch(buildApiUrl(`${API_ENDPOINTS.estadosPedido}`), {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: params
          });
          const data = await res.json().catch(() => ({}));
          if (res.ok && data.success) {
            showToast(data.message || 'Estado actualizado', 'success');
            const row = state.rows.find((r) => String(getRowId(r)) === id);
            if (row) {
              row.estado = estadoNuevo;
              row.status = estadoNuevo;
            }
            applyFilters();
          } else {
            showToast(data.message || 'No se pudo cambiar el estado', 'error');
            button.disabled = false;
          }
        } catch {
          showToast('Error de conexión', 'error');
          button.disabled = false;
        }
      });
    });
  }

  function renderPagination() {
    if (!pagination) return;

    const totalPages = Math.max(1, Math.ceil(state.filtered.length / pageSize));
    pagination.innerHTML = `
      <button class="admin-pagination__btn" data-page="prev" ${state.page === 1 ? 'disabled' : ''}>‹ Anterior</button>
      <span class="admin-pagination__status">Página ${state.page} de ${totalPages}</span>
      <button class="admin-pagination__btn" data-page="next" ${state.page === totalPages ? 'disabled' : ''}>Siguiente ›</button>
    `;

    pagination.querySelectorAll('button[data-page]').forEach((button) => {
      button.addEventListener('click', () => {
        if (button.dataset.page === 'prev' && state.page > 1) state.page -= 1;
        if (button.dataset.page === 'next' && state.page < totalPages) state.page += 1;
        renderTable();
        renderPagination();
      });
    });
  }

  searchInput?.addEventListener('input', applyFilters);
  filterSelect?.addEventListener('change', applyFilters);

  applyFilters();
}

async function deleteEntity(entity, apiPath, id) {
  const queryParam = DELETE_QUERY_PARAMS[entity];

  if (!apiPath || !queryParam) {
    return true;
  }

  const response = await request(`${apiPath}?${queryParam}=${encodeURIComponent(id)}`, {
    method: 'DELETE'
  });

  if (response.ok) {
    showToast(response.data?.message || 'Registro eliminado correctamente.', 'success');
    return true;
  }

  showToast(response.error || 'No se pudo eliminar el registro en el backend.', 'error');
  return false;
}

function getEntityId(entity, row) {
  if (entity === 'orders' && (row.id || row.idPedido || row.code)) return row.id || row.idPedido || row.code;
  if (entity === 'promotions' && (row.idPromocion || row.id || row.code || row.codigo)) {
    return row.idPromocion || row.id || row.code || row.codigo;
  }
  if (entity === 'categories' && (row.idCategoria || row.id)) return row.idCategoria || row.id;
  if (entity === 'products' && (row.idProducto || row.id || row.sku)) return row.idProducto || row.id || row.sku;
  return row.__localId;
}

function ensureEntityIds(entity, rows) {
  let localCounter = 0;

  return rows.map((row) => {
    const realId = getEntityId(entity, row);
    if (realId) return row;

    localCounter += 1;
    return {
      ...row,
      __localId: `local-${entity}-${Date.now()}-${localCounter}`
    };
  });
}

function statusBadge(status) {
  const value = String(status || '').toLowerCase();
  if (value.includes('activo') || value.includes('disponible') || value.includes('entregado') || value.includes('enviado')) return 'admin-badge--success';
  if (value.includes('pendiente') || value.includes('proceso') || value.includes('stock') || value.includes('expirada')) return 'admin-badge--warning';
  if (value.includes('cancelado') || value.includes('inactiva')) return 'admin-badge--danger';
  return 'admin-badge--info';
}

function renderProductRow(product) {
  const id = String(getEntityId('products', product));
  return `
    <tr>
      <td>${product.idProducto || '-'}</td>
      <td><div class="admin-product-image">Sin imagen</div></td>
      <td>${product.nombreProducto || '-'}</td>
      <td>${product.categoriaId || product.idCategoria || '-'}</td>
      <td>${formatCurrency(product.precio)}</td>
      <td>${product.sku || '-'}</td>
      <td>
        <span class="admin-badge ${statusBadge(product.estado)}">
          ${product.estado || '-'}
        </span>
      </td>
      <td>
        <div class="admin-table__actions">
          <button class="admin-btn admin-btn--primary admin-btn--small" data-stock-id="${id}">
            Stock
          </button>
          <button class="admin-btn admin-btn--danger admin-btn--small" data-delete-id="${id}">
            Eliminar
          </button>
        </div>
      </td>
    </tr>
  `;
}

function renderCategoryRow(category) {
  const id = String(getEntityId('categories', category));
  return `
    <tr>
      <td>${category.idCategoria || '-'}</td>
      <td>${category.nombreCategoria || category.nombre || '-'}</td>
      <td>${category.description || category.descripcion || '-'}</td>
      <td>${category.products || category.cantidadProductos || 0}</td>
      <td><span class="admin-badge ${statusBadge(category.date || category.fechaCreacion)}">${category.date || category.fechaCreacion || '-'}</span></td>
      <td><div class="admin-table__actions"><button class="admin-btn admin-btn--danger admin-btn--small" data-delete-id="${id}">Eliminar</button></div></td>
    </tr>
  `;
}

function renderOrderRow(order) {
  const id = String(getEntityId('orders', order));
  const numero = order.numeroPedido || order.code || '-';
  const fecha = (order.fecha || order.date || '-').toString().slice(0, 10);
  const estado = order.estado || order.status || '-';
  const total = order.montoTotal || order.total || 0;
  const cliente = order.customer || order.cliente
    || (order.usuarioId ? `Usuario #${order.usuarioId}` : '-');
  const items = order.items || order.productos || '-';

  const estados = [
    'Pendiente_Pago', 'Pagado', 'En_Preparacion', 'Despachado',
    'En_Transito', 'Entregado', 'Cancelado', 'Devuelto'
  ];

  return `
    <tr>
      <td>${order.id || order.idPedido || '-'}</td>
      <td>${escapeCell(cliente)}<br><small>${escapeCell(numero)}</small></td>
      <td>${escapeCell(fecha)}</td>
      <td>${escapeCell(String(items))}</td>
      <td>${formatCurrency(Number(total) || 0)}</td>
      <td><span class="admin-badge ${statusBadge(estado)}">${escapeCell(estado)}</span></td>
      <td>
        <div class="admin-table__actions">
          <select class="admin-search__input" data-estado-select="${id}" style="max-width:150px;padding:0.25rem;font-size:0.8rem;">
            ${estados.map((e) => `<option value="${e}" ${e === estado ? 'selected' : ''}>${e}</option>`).join('')}
          </select>
          <button class="admin-btn admin-btn--primary admin-btn--small" data-cambiar-id="${id}">Cambiar</button>
          <a class="admin-btn admin-btn--secondary admin-btn--small"
             href="../../html/orders/order-detail.html?id=${id}">Detalle</a>
        </div>
      </td>
    </tr>
  `;
}

function escapeCell(s) {
  if (s === null || s === undefined) return '-';
  return String(s)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function renderPromotionRow(p) {
  const id = String(getEntityId('promotions', p));
  const nombre = p.nombrePromocion || p.name || p.nombre || '-';
  const tipo = p.tipoDescuento || p.type || p.tipo || '-';
  const valor = p.valorDescuento ?? p.discount ?? p.descuento ?? '-';
  const inicio = (p.fechaInicio || p.start || p.inicio || '-').toString().slice(0, 10);
  const fin = (p.fechaFin || p.end || p.fin || '-').toString().slice(0, 10);
  const estado = p.estado || p.status || '-';

  return `
    <tr>
      <td>${p.idPromocion || p.id || '-'}</td>
      <td>${nombre}</td>
      <td>${tipo}</td>
      <td><span class="admin-promo-discount">${tipo === 'Porcentaje' ? valor + '%' : formatCurrency(Number(valor) || 0)}</span></td>
      <td>${tipo}</td>
      <td>${inicio}</td>
      <td>${fin}</td>
      <td><span class="admin-badge ${statusBadge(estado)}">${estado}</span></td>
      <td><div class="admin-table__actions"><button class="admin-btn admin-btn--danger admin-btn--small" data-delete-id="${id}">Eliminar</button></div></td>
    </tr>
  `;
}

// =========================
// Formulario Crear Promoción → POST /admin/promociones
// =========================
function initPromotionsForm() {
  const form = document.querySelector('.admin-widget--form-wide form');
  if (!form || form.dataset.bound === '1') return;
  form.dataset.bound = '1';

  form.addEventListener('submit', async (event) => {
    event.preventDefault();

    const nombre = document.getElementById('promo-name')?.value?.trim();
    const descuento = Number(document.getElementById('promo-discount')?.value || 0);
    const tipoRaw = document.getElementById('promo-type')?.value || 'percentage';
    const inicio = document.getElementById('promo-start')?.value;
    const fin = document.getElementById('promo-end')?.value;
    const codigo = document.getElementById('promo-code')?.value?.trim() || '';

    if (!nombre || !descuento || !inicio || !fin) {
      showToast('Completa los campos obligatorios', 'error');
      return;
    }

    // El backend Tipos: Porcentaje | MontoFijo
    const tipoDescuento = tipoRaw === 'fixed' ? 'MontoFijo' : 'Porcentaje';

    const body = {
      nombrePromocion: nombre,
      descripcion: codigo ? `Código: ${codigo}` : nombre,
      tipoDescuento,
      valorDescuento: descuento,
      productoId: 0,
      categoriaId: 0,
      fechaInicio: inicio + 'T00:00:00',
      fechaFin: fin + 'T23:59:59',
      estado: 'Activo',
      aplicaConOtros: 0
    };

    const result = await request(API_ENDPOINTS.adminPromociones, {
      method: 'POST',
      body
    });

    if (result.ok) {
      showToast(result.data?.message || 'Promoción creada correctamente', 'success');
      form.reset();
      setTimeout(() => window.location.reload(), 800);
    } else {
      showToast(result.error || 'No se pudo crear la promoción', 'error');
    }
  });
}
