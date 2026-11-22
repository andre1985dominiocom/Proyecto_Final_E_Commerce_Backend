import { API_ENDPOINTS, buildApiUrl, STORAGE_KEYS, CATALOG_ENDPOINTS } from '../core/config.js';
import { getJSON, setJSON } from '../core/storage.js';
import { showToast, toNumberFromCurrency, formatCurrency } from '../core/ui.js';

initPromotions();

function initPromotions() {
  loadCoupons();
  loadBannerPromociones();
  bindPromoProducts();
  bindCouponCards(); // fallback para cards estáticas
}

// =========================
// Cupones activos (API Fase 2)
// =========================
async function loadCoupons() {
  const grid = document.querySelector('.promotions-coupons__grid');
  if (!grid) return;

  try {
    const res = await fetch(buildApiUrl(`${API_ENDPOINTS.cupones}?accion=listar`));
    if (!res.ok) return;
    const cupones = await res.json();
    if (!Array.isArray(cupones) || cupones.length === 0) return;

    grid.innerHTML = cupones.map(c => {
      const esPorcentaje = String(c.tipoDescuento).toLowerCase().includes('porcentaje');
      const descLabel = esPorcentaje
        ? `${Number(c.valorDescuento)}%`
        : formatCurrency(Number(c.valorDescuento));
      const minimo = Number(c.montoMinimo) > 0
        ? `Compra mínima: ${formatCurrency(Number(c.montoMinimo))}`
        : 'Sin compra mínima';
      const fin = c.fechaFin
        ? `Válido hasta: ${String(c.fechaFin).slice(0, 10)}`
        : 'Vigente';
      const desc = esPorcentaje
        ? 'Descuento porcentaje en tu compra'
        : 'Descuento fijo en tu compra';

      return `
        <div class="coupon-card coupon-card--interactive" role="button" tabindex="0">
          <div class="coupon-card__discount">${descLabel}</div>
          <div class="coupon-card__body">
            <p class="coupon-card__description">${desc}<br>${minimo}</p>
            <span class="coupon-card__code">${c.codigo}</span>
          </div>
          <p class="coupon-card__expiry">${fin}</p>
        </div>
      `;
    }).join('');

    bindCouponCards();
  } catch (err) {
    console.error('Error cargando cupones:', err);
  }
}

function bindCouponCards() {
  document.querySelectorAll('.coupon-card').forEach((card) => {
    if (card.dataset.bound === '1') return;
    const codeElement = card.querySelector('.coupon-card__code');
    if (!codeElement) return;

    card.dataset.bound = '1';
    card.classList.add('coupon-card--interactive');
    card.setAttribute('role', 'button');
    card.setAttribute('tabindex', '0');

    const applyCoupon = async () => {
      const code = codeElement.textContent.trim().toUpperCase();
      localStorage.setItem(STORAGE_KEYS.coupon, code);

      // Aplicar en sesión del carrito si hay sesión
      try {
        const params = new URLSearchParams();
        params.append('accion', 'aplicar-cupon');
        params.append('codigo', code);
        const res = await fetch(buildApiUrl(API_ENDPOINTS.cart), {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: params
        });
        const data = await res.json();
        if (data.success) {
          showToast(`Cupón ${code} aplicado al carrito.`, 'success');
        } else {
          showToast(data.message || `Cupón ${code} listo para usar en el carrito.`, 'info');
        }
      } catch {
        showToast(`Cupón ${code} listo para usar en el carrito.`, 'info');
      }
    };

    card.addEventListener('click', applyCoupon);
    card.addEventListener('keydown', (event) => {
      if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault();
        applyCoupon();
      }
    });
  });
}

// =========================
// Banner de promociones activas (CU-CLI-01)
// =========================
async function loadBannerPromociones() {
  try {
    const res = await fetch(buildApiUrl(API_ENDPOINTS.promocionesBanner));
    if (!res.ok) return;
    const promos = await res.json();
    if (!Array.isArray(promos) || promos.length === 0) return;

    const primera = promos[0];
    const title = document.querySelector('.promotions-hero__title');
    const subtitle = document.querySelector('.promotions-hero__subtitle');

    if (title && primera.nombrePromocion) {
      title.textContent = primera.nombrePromocion;
    }
    if (subtitle && primera.descripcion) {
      subtitle.textContent = primera.descripcion;
    }

    // Home: promo-banner dinámico
    const homeTitle = document.querySelector('.promo-banner__title');
    const homeSubtitle = document.querySelector('.promo-banner__subtitle');
    const homeDesc = document.querySelector('.promo-banner__description');
    if (homeTitle && primera.nombrePromocion) {
      homeTitle.textContent = `¡${primera.nombrePromocion}!`;
    }
    if (homeSubtitle && primera.descripcion) {
      homeSubtitle.textContent = primera.descripcion;
    }
    if (homeDesc && primera.tipoDescuento) {
      const valor = primera.valorDescuento;
      homeDesc.textContent = primera.tipoDescuento === 'Porcentaje'
        ? `Hasta ${valor}% de descuento. Solo por tiempo limitado.`
        : `${formatCurrency(valor)} de descuento. Solo por tiempo limitado.`;
    }
  } catch (err) {
    console.error('Error cargando banner promociones:', err);
  }
}

// =========================
// Productos en oferta → carrito real
// =========================
function bindPromoProducts() {
  document.querySelectorAll('.promo-card').forEach((card) => {
    const button = card.querySelector('.promo-card__button');
    if (!button || button.dataset.bound === '1') return;
    button.dataset.bound = '1';

    button.addEventListener('click', async (event) => {
      event.preventDefault();

      const name = card.querySelector('.promo-card__name')?.textContent?.trim() || 'Producto en oferta';
      const priceText = card.querySelector('.promo-card__price--discounted')?.textContent
        || card.querySelector('.promo-card__price')?.textContent
        || '0';
      const price = toNumberFromCurrency(priceText);

      // Buscar producto real por nombre en catálogo
      let productoId = card.dataset.productoId || null;
      let precio = price;

      if (!productoId) {
        try {
          const res = await fetch(buildApiUrl(`${CATALOG_ENDPOINTS.productos}?vista=publica`));
          const productos = await res.json();
          if (Array.isArray(productos)) {
            const match = productos.find(p =>
              String(p.nombreProducto || '').toLowerCase() === name.toLowerCase()
              || String(p.nombreProducto || '').toLowerCase().includes(name.toLowerCase().slice(0, 12))
            );
            if (match) {
              productoId = match.idProducto;
              precio = Number(match.precio) || price;
            }
          }
        } catch (e) {
          console.error(e);
        }
      }

      if (!productoId) {
        showToast('Producto no encontrado en el catálogo', 'error');
        return;
      }

      try {
        const params = new URLSearchParams();
        params.append('accion', 'agregar');
        params.append('productoId', String(productoId));
        params.append('cantidad', '1');
        params.append('precioUnitario', String(precio));
        const res = await fetch(buildApiUrl(API_ENDPOINTS.cart), {
          method: 'POST',
          headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
          body: params
        });
        const data = await res.json();
        if (data.success) {
          showToast(`${name} agregado al carrito.`, 'success');
          window.location.href = '../cart/cart.html';
        } else {
          showToast(data.message || 'No se pudo agregar', 'error');
        }
      } catch (err) {
        console.error(err);
        showToast('Error agregando al carrito', 'error');
      }
    });
  });
}
