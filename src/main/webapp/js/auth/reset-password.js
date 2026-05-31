import { API_ENDPOINTS, buildAppUrl } from '../core/config.js';
import { request } from '../core/http.js';
import { setButtonLoading, showToast } from '../core/ui.js';

const form = document.getElementById('reset-password-form');
const token = new URLSearchParams(window.location.search).get('token') || '';

if (form) {
  if (!token) {
    showToast('Token de recuperación no encontrado.', 'error');
  } else {
    validateToken(token);
  }
  form.addEventListener('submit', handleResetPassword);
}

async function validateToken(tokenValue) {
  const result = await request(`${API_ENDPOINTS.authValidateToken}?token=${encodeURIComponent(tokenValue)}`);
  if (!result.ok) {
    showToast(result.error || 'El token no es válido o expiró.', 'error');
  }
}

async function handleResetPassword(event) {
  event.preventDefault();

  const submitButton = form.querySelector('button[type="submit"]');
  const nuevaContrasena = (document.getElementById('new-password')?.value || '').trim();
  const confirmarContrasena = (document.getElementById('confirm-password')?.value || '').trim();

  if (!token) {
    showToast('No hay token de recuperación en la URL.', 'error');
    return;
  }

  if (!nuevaContrasena || !confirmarContrasena) {
    showToast('Debes completar ambos campos de contraseña.', 'error');
    return;
  }

  if (nuevaContrasena !== confirmarContrasena) {
    showToast('Las contraseñas no coinciden.', 'error');
    return;
  }

  setButtonLoading(submitButton, true, 'Guardando...');

  const result = await request(API_ENDPOINTS.authResetPassword, {
    method: 'POST',
    body: { token, nuevaContrasena }
  });

  if (!result.ok) {
    showToast(result.error || 'No fue posible restablecer la contraseña.', 'error');
    setButtonLoading(submitButton, false);
    return;
  }

  showToast(result.data?.message || 'Contraseña actualizada correctamente.', 'success');
  setButtonLoading(submitButton, false);

  setTimeout(() => {
    window.location.href = buildAppUrl('/html/auth/login.html');
  }, 900);
}
