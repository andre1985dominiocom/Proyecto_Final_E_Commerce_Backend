import { API_ENDPOINTS, buildAppUrl, buildApiUrl } from '../core/config.js';
import { canAccessAdminView, isAdmin, isAuthenticated } from '../core/guards.js';
import { getSession, clearSession } from '../core/session.js';
import { showToast } from '../core/ui.js';

document.addEventListener('DOMContentLoaded', initDashboardAccess);

function initDashboardAccess() {
  if (!isAuthenticated()) {
    // Sin sesión → login (NO recargar el dashboard: eso provocaba bucle)
    window.location.replace(buildAppUrl('/html/auth/login.html'));
    return;
  }

  if (!canAccessAdminView()) {
    showToast('No tienes permisos para acceder al panel administrativo.', 'error');
    window.location.replace(buildAppUrl('/index.html'));
    return;
  }

  setupRoleBasedUI();
  setupUserInfo();
  setupLogout();
}

function setupRoleBasedUI() {
  const adminOnlyElements = document.querySelectorAll('[data-role="admin-only"]');

  if (!isAdmin()) {
    adminOnlyElements.forEach((element) => {
      element.style.display = 'none';
    });
  }
}

function setupUserInfo() {
  const session = getSession();
  const user = session?.user || {};
  const role = String(user?.rol || '').toUpperCase();

  const userNameElements = document.querySelectorAll('[data-session="user-name"]');
  const userRoleElements = document.querySelectorAll('[data-session="user-role"]');

  const displayName = [user.nombre, user.apellido].filter(Boolean).join(' ').trim() || user.email || 'Usuario';

  userNameElements.forEach((element) => {
    element.textContent = displayName;
  });

  userRoleElements.forEach((element) => {
    element.textContent = role || 'SIN ROL';
  });
}

function setupLogout() {
  // Soporta data-action="logout" y data-logout-link (usados en distintas vistas admin)
  const logoutButtons = document.querySelectorAll(
    '[data-action="logout"], [data-logout-link]');

  logoutButtons.forEach((button) => {
    if (button.dataset.logoutBound === 'true') return;
    button.dataset.logoutBound = 'true';

    button.addEventListener('click', (event) => {
      event.preventDefault();
      // Limpiar sesión ANTES de cualquier red: si fetch cuelga, la página no se bloquea.
      clearSession();
      try {
        fetch(buildApiUrl(API_ENDPOINTS.logout), {
          method: 'POST',
          keepalive: true
        }).catch(() => {});
      } catch (_) { /* la sesión local ya se limpió */ }
      window.location.replace(buildAppUrl('/html/auth/login.html'));
    });
  });
}