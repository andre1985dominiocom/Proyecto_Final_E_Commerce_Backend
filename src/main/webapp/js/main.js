import { buildAppUrl, buildApiUrl, API_ENDPOINTS } from './core/config.js';
import { getSession, clearSession } from './core/session.js';
import "./catalog/catalog-public.js";

const { token, user } = getSession();

if (token) {
  document.querySelectorAll('[data-auth-link]').forEach((link) => {
    link.textContent = user?.nombre ? `Hola, ${user.nombre}` : 'Mi cuenta';
    link.setAttribute('href', '#');
    link.dataset.logoutAction = 'true';
  });

  document.querySelectorAll('.admin-topbar__user').forEach((element) => {
    element.textContent = user?.nombre || user?.email || 'Admin';
  });
}

document.querySelectorAll('[data-logout-link], [data-logout-action="true"]').forEach((element) => {
  element.addEventListener('click', async (event) => {
    event.preventDefault();
    try {
      await fetch(buildApiUrl(API_ENDPOINTS.logout), { method: 'POST' });
    } catch (_) {}
    clearSession();
    window.location.href = buildAppUrl('/html/auth/login.html');
  });
});
