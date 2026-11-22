import { API_ENDPOINTS, STORAGE_KEYS, buildAppUrl } from '../core/config.js';
import { request } from '../core/http.js';
import { getJSON, setJSON } from '../core/storage.js';
import { canManageUsers, isAuthenticated } from '../core/guards.js';
import { renderStateRow, showToast } from '../core/ui.js';

const tbody = document.getElementById('users-table-body');
const searchInput = document.getElementById('users-search-input');
const roleFilter = document.getElementById('users-role-filter');
const pagination = document.getElementById('users-pagination');
const pageSize = 6;

const state = {
  all: [],
  filtered: [],
  currentPage: 1
};

const roleLabels = {
  1: 'Administrador',
  2: 'Empleado',
  3: 'Cliente'
};

if (tbody) {
  init();
}

async function init() {
  if (!isAuthenticated()) {
    window.location.href = buildAppUrl('/html/auth/login.html');
    return;
  }

  if (!canManageUsers()) {
    renderStateRow(tbody, 'No tienes permisos para administrar usuarios.', 'empty', 8);
    showToast('Acceso restringido solo para administradores.', 'error');
    return;
  }

  bindEvents();
  ensureModal();
  await loadUsers();
}

function bindEvents() {
  searchInput?.addEventListener('input', applyFilters);
  roleFilter?.addEventListener('change', applyFilters);
  pagination?.addEventListener('click', onPageClick);

  document.getElementById('btn-create-user')?.addEventListener('click', () => openModal());
  document.getElementById('user-modal-cancel')?.addEventListener('click', closeModal);
  document.getElementById('user-modal-form')?.addEventListener('submit', onSaveUser);
}

async function loadUsers() {
  renderStateRow(tbody, 'Cargando usuarios...', 'loading', 8);

  const result = await request(API_ENDPOINTS.adminUsuarios);

  if (result.ok && Array.isArray(result.data)) {
    state.all = result.data;
    setJSON(STORAGE_KEYS.users, result.data);
  } else {
    state.all = getJSON(STORAGE_KEYS.users, []);
    if (!state.all.length) {
      showToast(result.error || 'Backend no disponible. Mostrando lista local vacía.', 'warning');
    }
  }

  applyFilters();
}

function normalizeRole(user) {
  return String(user.perfilId || user.rol || '').toLowerCase();
}

function applyFilters() {
  const query = (searchInput?.value || '').trim().toLowerCase();
  const selectedRole = (roleFilter?.value || '').trim().toLowerCase();
  const roleAliases = { admin: 'administrador', empl: 'empleado', customer: 'cliente' };
  const expectedRole = roleAliases[selectedRole] || selectedRole;

  state.filtered = state.all.filter((user) => {
    const fullName = `${user.nombre || ''} ${user.apellido || ''}`.toLowerCase();
    const email = (user.email || '').toLowerCase();
    const role = normalizeRole(user);
    const roleText = roleLabels[user.perfilId] || user.rol || '';

    const matchesSearch =
      !query ||
      fullName.includes(query) ||
      email.includes(query) ||
      String(user.documento || '').includes(query);

    const matchesRole =
      !selectedRole ||
      role === selectedRole ||
      roleText.toLowerCase().includes(expectedRole);

    return matchesSearch && matchesRole;
  });

  state.currentPage = 1;
  renderTable();
  renderPagination();
}

function getCurrentPageRows() {
  const startIndex = (state.currentPage - 1) * pageSize;
  return state.filtered.slice(startIndex, startIndex + pageSize);
}

function renderTable() {
  if (!state.filtered.length) {
    renderStateRow(tbody, 'No se encontraron usuarios para los filtros actuales.', 'empty', 8);
    return;
  }

  const rows = getCurrentPageRows().map((user) => {
    const id = user.idUsuario || user.id || '-';
    const initials = getUserInitials(user);
    const fullName = `${user.nombre || ''} ${user.apellido || ''}`.trim() || 'Sin nombre';
    const role = roleLabels[user.perfilId] || user.rol || 'Cliente';
    const estado = String(user.estado || 'Activo');
    const stateClass = estado.toLowerCase() === 'activo'
      ? 'admin-badge--success'
      : estado.toLowerCase() === 'bloqueado'
        ? 'admin-badge--danger'
        : 'admin-badge--warning';

    return `
      <tr>
        <td>${id}</td>
        <td>${escapeHtml(user.email || '-')}</td>
        <td>
          <span class="admin-user-avatar">${escapeHtml(initials)}</span>
          <span>${escapeHtml(fullName)}</span>
        </td>
        <td>${escapeHtml(user.tipoDocumento || '-')}</td>
        <td>${escapeHtml(user.documento || '-')}</td>
        <td><span class="admin-badge admin-badge--info">${escapeHtml(role)}</span></td>
        <td><span class="admin-badge ${stateClass}">${escapeHtml(estado)}</span></td>
        <td>
          <div class="admin-table__actions">
            <button class="admin-btn admin-btn--secondary admin-btn--small" data-action="edit" data-id="${id}">Editar</button>
            <button class="admin-btn admin-btn--secondary admin-btn--small" data-action="toggle" data-id="${id}">Estado</button>
            <button class="admin-btn admin-btn--danger admin-btn--small" data-action="delete" data-id="${id}">Eliminar</button>
          </div>
        </td>
      </tr>
    `;
  });

  tbody.innerHTML = rows.join('');
  tbody.querySelectorAll('button[data-action]').forEach((button) => {
    button.addEventListener('click', handleAction);
  });
}

function getUserInitials(user) {
  const firstNameInitial = (user?.nombre || 'U').charAt(0);
  const lastNameInitial = (user?.apellido || 'N').charAt(0);
  return `${firstNameInitial}${lastNameInitial}`.toUpperCase();
}

function renderPagination() {
  if (!pagination) return;

  const totalPages = Math.max(1, Math.ceil(state.filtered.length / pageSize));

  pagination.innerHTML = `
    <button class="admin-pagination__btn" data-page="prev" ${state.currentPage === 1 ? 'disabled' : ''}>‹ Anterior</button>
    <span class="admin-pagination__status">Página ${state.currentPage} de ${totalPages}</span>
    <button class="admin-pagination__btn" data-page="next" ${state.currentPage === totalPages ? 'disabled' : ''}>Siguiente ›</button>
  `;
}

function onPageClick(event) {
  const button = event.target.closest('button[data-page]');
  if (!button) return;

  const totalPages = Math.max(1, Math.ceil(state.filtered.length / pageSize));

  if (button.dataset.page === 'prev' && state.currentPage > 1) {
    state.currentPage -= 1;
  }

  if (button.dataset.page === 'next' && state.currentPage < totalPages) {
    state.currentPage += 1;
  }

  renderTable();
  renderPagination();
}

function persistUsers() {
  setJSON(STORAGE_KEYS.users, state.all);
}

async function handleAction(event) {
  if (!canManageUsers()) {
    showToast('No tienes permisos para realizar esta acción.', 'error');
    return;
  }

  const button = event.currentTarget;
  const userId = button.dataset.id;
  const action = button.dataset.action;
  const user = state.all.find((u) => String(u.idUsuario || u.id) === String(userId));

  if (action === 'edit') {
    openModal(user);
    return;
  }

  if (action === 'delete') {
    // FA-2: motivo obligatorio para auditoría
    const motivo = window.prompt(
      'Motivo de eliminación (derecho al olvido — se anonimizará el usuario):\n' +
      'Se conservará el historial de pedidos sin datos personales.');
    if (motivo === null) return;
    if (motivo.trim().length < 5) {
      showToast('Debes indicar un motivo de al menos 5 caracteres.', 'error');
      return;
    }
    if (!window.confirm('¿Anonimizar este usuario? El historial transaccional se conservará.')) return;

    const result = await request(
      `${API_ENDPOINTS.adminUsuarios}?idUsuario=${encodeURIComponent(userId)}&motivo=${encodeURIComponent(motivo.trim())}`,
      { method: 'DELETE' }
    );

    if (!result.ok) {
      showToast(result.error || 'No se pudo anonimizar el usuario.', 'error');
      if (result.status === 409) return;
      return;
    }

    showToast(result.data?.message || 'Usuario anonimizado correctamente.', 'success');
    await loadUsers();
    return;
  }

  if (action === 'toggle') {
    if (!user) return;
    const actual = String(user.estado || 'Activo');
    const nuevo = actual.toLowerCase() === 'activo' ? 'Inactivo' : 'Activo';

    const result = await request(API_ENDPOINTS.adminUsuarios, {
      method: 'PUT',
      body: {
        idUsuario: Number(userId),
        nombre: user.nombre,
        apellido: user.apellido,
        email: user.email,
        documento: user.documento,
        perfilId: user.perfilId,
        estado: nuevo
      }
    });

    if (result.ok) {
      showToast(`Estado actualizado a ${nuevo}.`, 'success');
      await loadUsers();
    } else {
      showToast(result.error || 'No se pudo actualizar el estado.', 'error');
    }
  }
}

// ---------- Modal crear / editar ----------
function ensureModal() {
  if (document.getElementById('user-modal')) return;

  const modal = document.createElement('div');
  modal.id = 'user-modal';
  modal.style.cssText = 'display:none;position:fixed;inset:0;background:rgba(0,0,0,0.45);z-index:1000;align-items:center;justify-content:center;';
  modal.innerHTML = `
    <div style="background:#fff;border-radius:8px;padding:1.25rem;width:min(480px,92vw);max-height:90vh;overflow:auto;">
      <h2 id="user-modal-title" style="margin-top:0;">Crear usuario</h2>
      <form id="user-modal-form" style="display:flex;flex-direction:column;gap:0.6rem;">
        <input type="hidden" id="user-id">
        <label>Nombre
          <input required id="user-nombre" style="width:100%;padding:0.45rem;border:1px solid #ccc;border-radius:4px;">
        </label>
        <label>Apellido
          <input required id="user-apellido" style="width:100%;padding:0.45rem;border:1px solid #ccc;border-radius:4px;">
        </label>
        <label>Email
          <input required type="email" id="user-email" style="width:100%;padding:0.45rem;border:1px solid #ccc;border-radius:4px;">
        </label>
        <label>Documento (10-12 dígitos)
          <input required id="user-documento" pattern="\\d{10,12}" style="width:100%;padding:0.45rem;border:1px solid #ccc;border-radius:4px;">
        </label>
        <label>Rol
          <select id="user-perfil" style="width:100%;padding:0.45rem;border:1px solid #ccc;border-radius:4px;">
            <option value="3">Cliente</option>
            <option value="2">Empleado</option>
            <option value="1">Administrador</option>
          </select>
        </label>
        <label>Estado
          <select id="user-estado" style="width:100%;padding:0.45rem;border:1px solid #ccc;border-radius:4px;">
            <option value="Activo">Activo</option>
            <option value="Inactivo">Inactivo</option>
            <option value="Bloqueado">Bloqueado</option>
          </select>
        </label>
        <label id="user-password-label">Contraseña (mín 8, 1 mayúscula, 1 minúscula, 1 número)
          <input id="user-password" type="password" autocomplete="new-password"
                 style="width:100%;padding:0.45rem;border:1px solid #ccc;border-radius:4px;">
        </label>
        <div style="display:flex;gap:0.5rem;justify-content:flex-end;margin-top:0.5rem;">
          <button type="button" id="user-modal-cancel" class="admin-btn admin-btn--secondary">Cancelar</button>
          <button type="submit" class="admin-btn admin-btn--primary">Guardar</button>
        </div>
      </form>
    </div>
  `;
  document.body.appendChild(modal);

  document.getElementById('user-modal-cancel')?.addEventListener('click', closeModal);
  document.getElementById('user-modal-form')?.addEventListener('submit', onSaveUser);
}

function openModal(user = null) {
  ensureModal();
  const modal = document.getElementById('user-modal');
  const title = document.getElementById('user-modal-title');
  const passLabel = document.getElementById('user-password-label');

  document.getElementById('user-id').value = user ? (user.idUsuario || '') : '';
  document.getElementById('user-nombre').value = user?.nombre || '';
  document.getElementById('user-apellido').value = user?.apellido || '';
  document.getElementById('user-email').value = user?.email || '';
  document.getElementById('user-documento').value = user?.documento || '';
  document.getElementById('user-perfil').value = String(user?.perfilId || 3);
  document.getElementById('user-estado').value = user?.estado || 'Activo';
  document.getElementById('user-password').value = '';
  document.getElementById('user-password').required = !user;
  passLabel.style.display = user ? 'none' : '';

  title.textContent = user ? `Editar usuario #${user.idUsuario}` : 'Crear usuario';
  modal.style.display = 'flex';
}

function closeModal() {
  const modal = document.getElementById('user-modal');
  if (modal) modal.style.display = 'none';
}

async function onSaveUser(event) {
  event.preventDefault();

  const id = document.getElementById('user-id').value;
  const body = {
    nombre: document.getElementById('user-nombre').value.trim(),
    apellido: document.getElementById('user-apellido').value.trim(),
    email: document.getElementById('user-email').value.trim(),
    documento: document.getElementById('user-documento').value.trim(),
    perfilId: Number(document.getElementById('user-perfil').value),
    estado: document.getElementById('user-estado').value
  };

  const password = document.getElementById('user-password').value;
  if (password) body.contrasena = password;

  let result;
  if (id) {
    body.idUsuario = Number(id);
    result = await request(API_ENDPOINTS.adminUsuarios, { method: 'PUT', body });
  } else {
    if (!password) {
      showToast('La contraseña es obligatoria para crear un usuario.', 'error');
      return;
    }
    result = await request(API_ENDPOINTS.adminUsuarios, { method: 'POST', body });
  }

  if (result.ok) {
    showToast(result.data?.message || 'Guardado correctamente', 'success');
    closeModal();
    await loadUsers();
  } else {
    showToast(result.error || 'No se pudo guardar', 'error');
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
