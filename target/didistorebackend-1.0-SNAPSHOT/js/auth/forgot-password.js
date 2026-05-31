import { API_ENDPOINTS } from '../core/config.js';
import { request } from '../core/http.js';
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

  const usersResponse = await request(API_ENDPOINTS.users);

  if (!usersResponse.ok || !Array.isArray(usersResponse.data)) {
    updateStatus('No fue posible validar el correo en este momento.', 'error');
    showToast(usersResponse.error || 'No se pudo consultar usuarios.', 'error');
    setButtonLoading(submitButton, false);
    return;
  }

  const user = usersResponse.data.find((item) => String(item.email || '').trim().toLowerCase() === email);

  if (!user?.idUsuario) {
    updateStatus('No encontramos una cuenta asociada a ese correo.', 'error');
    showToast('No encontramos una cuenta asociada a ese correo.', 'error');
    setButtonLoading(submitButton, false);
    return;
  }

  const recoveryResponse = await request(API_ENDPOINTS.passwordRecovery, {
    method: 'POST',
    body: { usuarioId: Number(user.idUsuario) }
  });

  if (!recoveryResponse.ok) {
    updateStatus(recoveryResponse.error || 'No se pudo generar el token de recuperación.', 'error');
    showToast(recoveryResponse.error || 'No se pudo generar el token de recuperación.', 'error');
    setButtonLoading(submitButton, false);
    return;
  }

  const token = recoveryResponse.data?.token;
  updateStatus('Token generado correctamente. Serás redirigido para restablecer tu contraseña.', 'success');
  showToast('Token generado correctamente.', 'success');
  setButtonLoading(submitButton, false);

  if (token) {
    setTimeout(() => {
      window.location.href = `reset-password.html?token=${encodeURIComponent(token)}`;
    }, 900);
  }
}

function updateStatus(message, type) {
  if (!status) return;
  status.textContent = message;
  status.dataset.state = type;
}
