import { API_ENDPOINTS, buildApiUrl } from '../core/config.js';
import { setButtonLoading, showToast } from '../core/ui.js';

const form = document.getElementById('forgot-password-form');
const status = document.getElementById('forgot-password-status');

if (form) {
  form.addEventListener('submit', handleRecoveryRequest);
}

async function handleRecoveryRequest(event) {
  event.preventDefault();

  const submitButton = form.querySelector('button[type="submit"]');
  const email = (document.getElementById('recovery-email')?.value || '').trim().toLowerCase();

  if (!email) {
    showToast('Debes ingresar un correo electrónico.', 'error');
    return;
  }

  setButtonLoading(submitButton, true, 'Enviando...');

  try {
    const response = await fetch(buildApiUrl(API_ENDPOINTS.passwordRecovery), {
      method: 'POST',
      headers: { 'Content-Type': 'application/json; charset=UTF-8' },
      body: JSON.stringify({ email })
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      updateStatus(data.message || 'No se pudo generar el token de recuperación.', 'error');
      showToast(data.message || 'No se pudo generar el token de recuperación.', 'error');
      setButtonLoading(submitButton, false);
      return;
    }

    const token = data.token;
    updateStatus('Token generado correctamente. Serás redirigido para restablecer tu contraseña.', 'success');
    showToast('Token generado correctamente.', 'success');
    setButtonLoading(submitButton, false);

    if (token) {
      setTimeout(() => {
        window.location.href = `reset-password.html?token=${encodeURIComponent(token)}`;
      }, 900);
    }
  } catch (error) {
    console.error('Error en recuperación de contraseña:', error);
    updateStatus('No fue posible procesar la solicitud en este momento.', 'error');
    showToast('Error al procesar la solicitud.', 'error');
    setButtonLoading(submitButton, false);
  }
}

function updateStatus(message, type) {
  if (!status) return;
  status.textContent = message;
  status.dataset.state = type;
}
