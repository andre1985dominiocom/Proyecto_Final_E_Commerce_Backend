import { buildAppUrl, buildApiUrl, API_ENDPOINTS } from './core/config.js';
import { getSession, clearSession } from './core/session.js';
import { initCartBadge } from './core/cart-badge.js';
import "./catalog/catalog-public.js";

const { token, user } = getSession();
initCartBadge();
loadHomeBanner();

async function loadHomeBanner() {
  const title = document.querySelector('.promo-banner__title');
  if (!title) return;
  try {
    const res = await fetch(buildApiUrl(API_ENDPOINTS.promocionesBanner));
    if (!res.ok) return;
    const promos = await res.json();
    if (!Array.isArray(promos) || !promos.length) return;
    const p = promos[0];
    if (p.nombrePromocion) title.textContent = `¡${p.nombrePromocion}!`;
    const sub = document.querySelector('.promo-banner__subtitle');
    if (sub && p.descripcion) sub.textContent = p.descripcion;
    const desc = document.querySelector('.promo-banner__description');
    if (desc && p.tipoDescuento) {
      desc.textContent = p.tipoDescuento === 'Porcentaje'
        ? `Hasta ${p.valorDescuento}% de descuento. Solo por tiempo limitado.`
        : `${Number(p.valorDescuento).toLocaleString('es-CO')} de descuento. Solo por tiempo limitado.`;
    }
  } catch (_) {}
}

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
  if (element.dataset.logoutBound === 'true') return;
  element.dataset.logoutBound = 'true';

  element.addEventListener('click', (event) => {
    event.preventDefault();
    // Limpiar ANTES de la petición: evita bloquear la página si fetch tarda.
    clearSession();
    try {
      fetch(buildApiUrl(API_ENDPOINTS.logout), {
        method: 'POST',
        keepalive: true
      }).catch(() => {});
    } catch (_) {}
    window.location.replace(buildAppUrl('/html/auth/login.html'));
  });
});
