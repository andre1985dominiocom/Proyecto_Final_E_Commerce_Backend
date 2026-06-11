import { STORAGE_KEYS } from './config.js';

export function setSession({ token, user }) {
  if (token) {
    localStorage.setItem(STORAGE_KEYS.token, token);
  }

  if (user) {
    localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
  }
}

export function getSession() {
  const token = localStorage.getItem(STORAGE_KEYS.token);
  const rawUser = localStorage.getItem(STORAGE_KEYS.user);

  let user = null;

  try {
    user = rawUser ? JSON.parse(rawUser) : null;
  } catch {
    user = null;
  }

  return { token, user };
}

export function clearSession() {
  localStorage.removeItem(STORAGE_KEYS.token);
  localStorage.removeItem(STORAGE_KEYS.user);
}