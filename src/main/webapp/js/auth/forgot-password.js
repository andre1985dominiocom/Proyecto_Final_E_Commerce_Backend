import { API_ENDPOINTS, buildAppUrl } from '../core/config.js';
import { request } from '../core/http.js';
import { setButtonLoading, showToast } from '../core/ui.js';

const form = document.getElementById('forgot-password-form');

if (form) {
  form.addEventListener('submit', handleForgotPassword);
}

async function handleForgotPassword(event) {
  event.preventDefault();

  const submitButton = form.querySelector('button[type="submit"]');
  const email = (document.getElementById('recovery')?.value || '').trim().toLowerCase();

  if (!email) {
    showToast('Debes ingresar un correo electrónico.', 'error');
    return;
  }

  setButtonLoading(submitButton, true, 'Enviando...');

  const result = await request(API_ENDPOINTS.authRecovery, {
    method: 'POST',
    body: { email }
  });

  if (!result.ok) {
    showToast(result.error || 'No fue posible iniciar la recuperación.', 'error');
    setButtonLoading(submitButton, false);
    return;
  }

  const token = result.data?.token;
  showToast(result.data?.message || 'Solicitud procesada correctamente.', 'success');

  setButtonLoading(submitButton, false);

  if (token) {
    setTimeout(() => {
      window.location.href = `${buildAppUrl('/html/auth/reset-password.html')}?token=${encodeURIComponent(token)}`;
    }, 900);
  }
}
