const RESERVED_ROOT_SEGMENTS = new Set(['html', 'css', 'js', 'assets', 'META-INF', 'WEB-INF']);

function detectAppContextPath() {
  if (typeof window === 'undefined') {
    return '/didistorebackend';
  }

  const segments = window.location.pathname.split('/').filter(Boolean);
  const firstSegment = segments[0] || '';

  if (!firstSegment || firstSegment.includes('.') || RESERVED_ROOT_SEGMENTS.has(firstSegment)) {
    return '';
  }

  return `/${firstSegment}`;
}

export const APP_CONTEXT_PATH = detectAppContextPath();
export const API_BASE_URL = `${window.location.origin}${APP_CONTEXT_PATH}`;

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
  logout: '/logout',
  users: '/auth/registro',
  products: '/admin/productos',
  categories: '/admin/categorias',
  inventory: '/admin/inventarios',
  cart: '/sales/carrito',
  pedido: '/sales/pedido',
  orders: '/sales/pedido',
  promotions: '/catalog/productos',
  checkout: '/sales/pedido',
  passwordRecovery: '/auth/recuperacion',
  validateToken: '/auth/validar-token',
  resetPassword: '/auth/restablecer-password',
  dashboard: '/admin/dashboard-data',
  direcciones: '/logistics/direccion'
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
  const normalizedBase = API_BASE_URL.replace(/\/$/, '');

  if (!path) return normalizedBase;

  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  return `${normalizedBase}${normalizedPath}`;
}

export function buildAppUrl(path = '') {
  const normalizedBase = `${window.location.origin}${APP_CONTEXT_PATH}`.replace(/\/$/, '');

  if (!path) {
    return normalizedBase;
  }

  const normalizedPath = path.startsWith('/') ? path : `/${path}`;
  return `${normalizedBase}${normalizedPath}`;
}