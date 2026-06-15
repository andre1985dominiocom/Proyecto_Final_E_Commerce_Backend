import { buildAppUrl } from '../core/config.js';
import { canAccessAdminView, isAdmin, isAuthenticated } from '../core/guards.js';
import { getSession, clearSession } from '../core/session.js';
import { showToast } from '../core/ui.js';

document.addEventListener('DOMContentLoaded', initDashboardAccess);

function initDashboardAccess() {
  if (!isAuthenticated()) {
    window.location.href = buildAppUrl('/html/admin/dashboard.html');
    return;
  }

  if (!canAccessAdminView()) {
    showToast('No tienes permisos para acceder al panel administrativo.', 'error');
    window.location.href = buildAppUrl('/index.html');
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
  const logoutButtons = document.querySelectorAll('[data-action="logout"]');

  logoutButtons.forEach((button) => {
    button.addEventListener('click', () => {
      clearSession();
      window.location.href = buildAppUrl('/html/admin/dashboard.html');
    });
  });
}