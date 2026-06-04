import { API_ENDPOINTS } from '../core/config.js';
import { request } from '../core/http.js';
import { setSession } from '../core/session.js';
import { setButtonLoading, showToast } from '../core/ui.js';

const form = document.getElementById('login-form');

if (form) {
  form.addEventListener('submit', handleLogin);
}

function normalizarRol(rol) {
  const valor = String(rol || '').trim().toUpperCase();

  if (valor === 'ADMIN' || valor === 'ADMINISTRADOR') {
    return 'ADMIN';
  }

  if (valor === 'EMPLEADO') {
    return 'EMPLEADO';
  }

  if (valor === 'CLIENTE' || valor === 'CLIENTES') {
    return 'CLIENTE';
  }

  return 'SIN_ROL';
}

function getRedirectByRol(rol, perfilId) {
  const rolNormalizado = normalizarRol(rol);
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

  const emailInput =
    document.getElementById('email') ||
    document.getElementById('username');

  const passwordInput =
    document.getElementById('contrasena') ||
    document.getElementById('password');

  const email = (emailInput?.value || '').trim().toLowerCase();
  const contrasena = (passwordInput?.value || '').trim();

  if (!email || !contrasena) {
    showToast('Debes completar correo y contraseña.', 'error');
    return;
  }

  setButtonLoading(submitButton, true, 'Ingresando...');
try {
    // 1. Usamos fetch nativo para evitar que http.js modifique la respuesta
    const response = await fetch(API_ENDPOINTS.login, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json' 
      },
      body: JSON.stringify({ email: email, contrasena: contrasena })
    });

    // 2. Extraemos el JSON crudo del servidor
    const data = await response.json();
    
    // 🔥 PUNTOS DE CONTROL OBLIGATORIOS 🔥
    console.log('--- DEBUG: RESPUESTA CRUDA DE JAVA ---', data);

    if (!data.success) {
      showToast(data.message || 'Credenciales inválidas.', 'error');
      return;
    }

    // 3. Extraemos los datos tal cual los manda Java
    const usuario = data.usuario || {};
    const token = data.token || '';
    
    // Fíjate que aquí usamos usuario.perfilId y usuario.rol
    const perfilId = usuario.perfilId; 
    const rol = usuario.rol;

    console.log('--- DEBUG: ROL ---', rol);
    console.log('--- DEBUG: PERFIL ID ---', perfilId);

    setSession({ token, user: usuario });

    // 4. Calculamos el destino
    const destino = getRedirectByRol(rol, perfilId);
    console.log('--- DEBUG: DESTINO ---', destino);

    showToast(data.message || 'Inicio de sesión exitoso.', 'success');

    setTimeout(() => {
      // window.location.href = destino;
      console.log('🛑 REDIRECCIÓN CONGELADA 🛑. Debería ir a:', destino);
    }, 800);

  } catch (error) {
    console.error('Error en login:', error);
    showToast('Ocurrió un error al iniciar sesión.', 'error');
  } finally {
    setButtonLoading(submitButton, false);
  }

//  try {
//    const result = await request(API_ENDPOINTS.login, {
//      method: 'POST',
//      body: { email, contrasena }
//    });
//
//    console.log('Respuesta login:', result);
//
//    if (!result.ok) {
//      showToast(result.error || result.data?.message || 'Credenciales inválidas.', 'error');
//      return;
//    }
//
//    const usuario = result.data?.usuario || {};
//    const token = result.data?.token || '';
//    const perfilId = usuario?.perfilId;
//    const rol = usuario?.rol;
//
//    console.log('Usuario autenticado:', usuario);
//    console.log('perfilId:', perfilId);
//    console.log('rol:', rol);
//
//    setSession({ token, user: usuario });
//
//    const destino = getRedirectByRol(rol, perfilId);
//
//    console.log('Destino calculado:', destino);
//
//    showToast(result.data?.message || 'Inicio de sesión exitoso.', 'success');
//
//    setTimeout(() => {
//      window.location.href = destino;
//    }, 800);
//
//  } catch (error) {
//    console.error('Error en login:', error);
//    showToast('Ocurrió un error al iniciar sesión.', 'error');
//  } finally {
//    setButtonLoading(submitButton, false);
//  }
}