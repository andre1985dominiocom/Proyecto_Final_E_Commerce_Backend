import { API_ENDPOINTS } from '../core/config.js';
import { request } from '../core/http.js';
import { setSession } from '../core/session.js';
import { setButtonLoading, showToast } from '../core/ui.js';

const form = document.getElementById('login-form');

if (form) {
  form.addEventListener('submit', handleLogin);
}

function getRedirectByRol(rol, perfilId) {
  const rolNormalizado = String(rol || '').toUpperCase().trim();
  const perfil = Number(perfilId);

  if (rolNormalizado === 'ADMIN' || perfil === 1) {
    return '../admin/dashboard.html';
  }

  if (rolNormalizado === 'EMPLEADO' || perfil === 2) {
    return '../admin/dashboard.html';
  }

  if (rolNormalizado === 'CLIENTE' || perfil === 3) {
    return '../account/account.html';
  }

  return '../../index.html';
}

async function handleLogin(event) {
  event.preventDefault();

  const submitButton = form.querySelector('button[type="submit"]');

  const emailInput = document.getElementById('email') || document.getElementById('username');
  const passwordInput = document.getElementById('contrasena') || document.getElementById('password');

  const email = (emailInput?.value || '').trim().toLowerCase();
  const contrasena = (passwordInput?.value || '').trim();

  if (!email || !contrasena) {
    showToast('Debes completar correo y contraseña.', 'error');
    return;
  }

  setButtonLoading(submitButton, true, 'Ingresando...');

  try {
    const result = await request(API_ENDPOINTS.login, {
      method: 'POST',
      body: { email, contrasena }
    });

    console.log('Respuesta completa login:', result);

    if (!result.ok) {
      console.error('Login fallido:', result);
      showToast(result.error || result.data?.message || 'Credenciales inválidas.', 'error');
      return;
    }

    const usuario = result.data?.usuario || {};
    const token = result.data?.token || '';
    const perfilId = usuario?.perfilId;
    const rol = usuario?.rol;

    console.log('Usuario autenticado:', usuario);
    console.log('perfilId:', perfilId);
    console.log('rol:', rol);

    setSession({ token, user: usuario });

    const destino = getRedirectByRol(rol, perfilId);
    console.log('Destino calculado:', destino);

    showToast(result.data?.message || 'Inicio de sesión exitoso.', 'success');

    setTimeout(() => {
      window.location.href = destino;
    }, 800);

  } catch (error) {
    console.error('Error real de login:', error);
    showToast('Ocurrió un error al iniciar sesión.', 'error');
  } finally {
    setButtonLoading(submitButton, false);
  }
}