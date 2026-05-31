const STATIC_ROOT_SEGMENTS = new Set(['html', 'css', 'js', 'assets', 'meta-inf', 'web-inf', 'index.html']);

function detectContextPath() {
  if (typeof window === 'undefined') return '';
  const [firstSegment] = window.location.pathname.split('/').filter(Boolean);
  if (!firstSegment || STATIC_ROOT_SEGMENTS.has(firstSegment.toLowerCase())) {
    return '';
  }
  return `/${firstSegment}`;
}

export const APP_CONTEXT_PATH = detectContextPath();

const DEFAULT_API_BASE_URL = `${window.location.origin}${APP_CONTEXT_PATH}`;
export const API_BASE_URL = localStorage.getItem('didistore:apiBaseUrl') || DEFAULT_API_BASE_URL;

export const STORAGE_KEYS = {
  token: 'didistore:token',
  user: 'didistore:user',
  cart: 'didistore:cart',
  coupon: 'didistore:coupon',
  orders: 'didistore:orders',
  users: 'didistore:users',
  adminData: 'didistore:admin-data',
  checkoutDraft: 'didistore:checkout-draft'
};

export const API_ENDPOINTS = {
  login: '/login',
  users: '/admin/usuarios',
  products: '/admin/productos',
  categories: '/admin/categorias',
  orders: null,
  promotions: null,
  checkout: null,
  authRecovery: '/auth/recuperacion',
  authValidateToken: '/auth/validar-token',
  authResetPassword: '/auth/restablecer-password'
};

export const CATALOG_ENDPOINTS = {
  productos: '/catalog/productos',
  categorias: '/catalog/categorias'
};

export const COUPON_DISCOUNTS = {
  DIDI10: 0.1,
  DIDI15: 0.15,
  DIDI20: 0.2,
  LIQUID30: 0.3
};

export function buildApiUrl(path = '') {
  if (!path) return API_BASE_URL;
  const normalizedBase = API_BASE_URL.replace(/\/$/, '');
  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  return `${normalizedBase}${normalizedPath}`;
}

export function buildAppUrl(path = '') {
  const normalizedPath = path
    ? (path.startsWith('/') ? path : `/${path}`)
    : '';
  return `${APP_CONTEXT_PATH}${normalizedPath}` || '/';
}
