import { API_ENDPOINTS } from '../core/config.js';
import { request } from '../core/http.js';
import { setButtonLoading, showToast } from '../core/ui.js';

const form = document.getElementById('reset-password-form');
const status = document.getElementById('reset-password-status');
const token = (new URLSearchParams(window.location.search).get('token') || '').trim();

if (form) {
  initializeResetForm();
  form.addEventListener('submit', handleResetPassword);
}

async function initializeResetForm() {
  if (!token) {
    disableForm('Falta el token de recuperación. Solicita uno nuevo para continuar.');
    return;
  }

  const validationResponse = await request(`${API_ENDPOINTS.validateToken}?token=${encodeURIComponent(token)}`);

  if (!validationResponse.ok) {
    disableForm(validationResponse.error || 'El token no es válido o ya expiró.');
    return;
  }

  updateStatus('Token validado. Ya puedes ingresar tu nueva contraseña.', 'success');
}

async function handleResetPassword(event) {
  event.preventDefault();

  if (!token) {
    disableForm('Falta el token de recuperación. Solicita uno nuevo para continuar.');
    return;
  }

  const submitButton = form.querySelector('button[type="submit"]');
  const nuevaContrasena = document.getElementById('new-password')?.value || '';
  const confirmacion = document.getElementById('confirm-password')?.value || '';

  if (!nuevaContrasena || !confirmacion) {
    showToast('Completa ambos campos para continuar.', 'error');
    return;
  }

  if (nuevaContrasena !== confirmacion) {
    showToast('Las contraseñas no coinciden.', 'error');
    return;
  }

  setButtonLoading(submitButton, true, 'Guardando...');

  const response = await request(API_ENDPOINTS.resetPassword, {
    method: 'POST',
    body: {
      token,
      nuevaContrasena
    }
  });

  if (!response.ok) {
    updateStatus(response.error || 'No fue posible actualizar la contraseña.', 'error');
    showToast(response.error || 'No fue posible actualizar la contraseña.', 'error');
    setButtonLoading(submitButton, false);
    return;
  }

  updateStatus(response.data?.message || 'Contraseña actualizada correctamente.', 'success');
  showToast(response.data?.message || 'Contraseña actualizada correctamente.', 'success');
  setButtonLoading(submitButton, false);

  setTimeout(() => {
    window.location.href = 'login.html';
  }, 1000);
}

function disableForm(message) {
  updateStatus(message, 'error');
  form.querySelectorAll('input, button').forEach((element) => {
    element.disabled = true;
  });
  showToast(message, 'error');
}

function updateStatus(message, type) {
  if (!status) return;
  status.textContent = message;
  status.dataset.state = type;
}
