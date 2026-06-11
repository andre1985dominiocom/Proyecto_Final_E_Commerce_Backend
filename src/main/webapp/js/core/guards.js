import { getSession } from './session.js';

export function getCurrentSession() {
  return getSession() || null;
}

export function getCurrentUser() {
  return getCurrentSession()?.user || null;
}

export function getCurrentRole() {
  const user = getCurrentUser();
  return String(user?.rol || '').trim().toUpperCase();
}

export function getCurrentPerfilId() {
  const user = getCurrentUser();
  return Number(user?.perfilId || 0);
}

export function isAuthenticated() {
  const session = getCurrentSession();
  return Boolean(session?.token && session?.user);
}

export function isAdmin() {
  return getCurrentRole() === 'ADMIN' || getCurrentPerfilId() === 1;
}

export function isEmpleado() {
  return getCurrentRole() === 'EMPLEADO' || getCurrentPerfilId() === 2;
}

export function isCliente() {
  return getCurrentRole() === 'CLIENTE' || getCurrentPerfilId() === 3;
}

export function canAccessAdminView() {
  return isAdmin() || isEmpleado();
}

export function canManageUsers() {
  return isAdmin();
}