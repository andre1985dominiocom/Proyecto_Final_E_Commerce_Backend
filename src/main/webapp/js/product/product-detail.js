import { API_ENDPOINTS, CATALOG_ENDPOINTS } from '../core/config.js';
import { request } from '../core/http.js';
import { formatCurrency, showToast } from '../core/ui.js';

const productDetailRoot = document.getElementById('product-detail-title');
let currentProductId = null;

if (productDetailRoot) {
  initProductDetail();
}

async function initProductDetail() {
  const idProducto = new URLSearchParams(window.location.search).get('idProducto');

  if (!idProducto) {
    renderErrorState('No se indicó qué producto se debe mostrar.');
    return;
  }

  currentProductId = Number(idProducto);

  const response = await request(`${CATALOG_ENDPOINTS.productos}?idProducto=${encodeURIComponent(idProducto)}`);

  if (!response.ok || !response.data || Array.isArray(response.data)) {
    renderErrorState(response.error || 'No se pudo cargar el detalle del producto.');
    return;
  }

  renderProduct(response.data);
  await cargarResenas();
}

function renderProduct(product) {
  const name = product.nombreProducto || 'Producto';
  const price = Number(product.precio || 0);
  const description = product.descripcionLarga || product.descripcionCorta || 'Sin descripción disponible.';
  const sku = product.sku || product.idProducto || '';
  const categoryName = product.nombreCategoria || '';
  const imageUrl = product.imagenUrl || product.imagenPrincipal || product.imagen || '';
  const categoryId = product.categoriaId || product.idCategoria || '';
  const stockValue = product.stock ?? product.stockActual;
  const hasStockInfo = stockValue !== undefined && stockValue !== null && stockValue !== '';
  const stock = Number(stockValue || 0);

  const titleEl = document.getElementById('product-detail-title');
  if (titleEl) {
    titleEl.textContent = name;
  }

  document.title = `${name} - DidiStore`;

  const skuEl = document.getElementById('product-detail-sku');
  if (skuEl) {
    skuEl.textContent = sku ? `Ref: ${sku}` : '';
  }

  const priceEl = document.getElementById('product-detail-price');
  if (priceEl) {
    priceEl.textContent = formatCurrency(price);
  }

  const descriptionEl = document.getElementById('product-detail-description');
  if (descriptionEl) {
    descriptionEl.textContent = description;
  }

  const tabDescriptionEl = document.getElementById('product-detail-tab-description');
  if (tabDescriptionEl) {
    tabDescriptionEl.textContent = description;
  }

  const stockEl = document.getElementById('product-detail-stock');
  if (stockEl) {
    if (!hasStockInfo) {
      stockEl.textContent = 'Disponibilidad sujeta a inventario.';
      stockEl.className = 'product-detail__stock';
    } else if (stock > 0) {
      stockEl.textContent = `✓ En stock (${stock} unidades disponibles)`;
      stockEl.className = 'product-detail__stock product-detail__stock--available';
    } else {
      stockEl.textContent = '✗ Sin stock';
      stockEl.className = 'product-detail__stock product-detail__stock--unavailable';
    }
  }

  const categoryLink = document.getElementById('product-detail-category-link');
  if (categoryLink) {
    categoryLink.textContent = categoryName || 'Ver categoría';
    if (categoryId) {
      categoryLink.href = `../catalog/catalog.html?idCategoria=${encodeURIComponent(categoryId)}`;
    }
  }

  const breadcrumbName = document.getElementById('product-detail-breadcrumb-name');
  if (breadcrumbName) {
    breadcrumbName.textContent = name;
  }

  if (imageUrl) {
    const mainImage = document.getElementById('mainImage');
    if (mainImage) {
      mainImage.src = imageUrl;
      mainImage.alt = name;
    }

    const thumbnails = document.querySelectorAll('.product-detail__thumbnail');
    thumbnails.forEach((thumb) => {
      thumb.src = imageUrl;
      thumb.alt = name;
    });
  }
}

// ========== RF-21: reseñas ==========
function stars(n) {
  const v = Math.round(Number(n) || 0);
  return '★'.repeat(Math.max(0, Math.min(5, v))) + '☆'.repeat(Math.max(0, 5 - v));
}

async function cargarResenas() {
  const listaEl = document.getElementById('resena-lista');
  if (!listaEl || !currentProductId) return;

  try {
    const res = await request(`${API_ENDPOINTS.resenas}?productoId=${currentProductId}`);
    const data = res.data || {};

    const promedio = Number(data.promedio || 0);
    const total = Number(data.total || 0);

    setText('resena-promedio', total ? promedio.toFixed(1) : '—');
    setText('resena-estrellas-promedio', total ? stars(Math.round(promedio)) : '☆☆☆☆☆');
    setText('resena-total', `(${total} reseña${total === 1 ? '' : 's'})`);

    // Formulario
    const formWrap = document.getElementById('resena-form-wrap');
    const hint = document.getElementById('resena-hint');
    if (formWrap) {
      if (data.puedeResenar === true) {
        formWrap.style.display = '';
        const mi = data.miResena;
        const idField = document.getElementById('resena-id');
        const title = document.getElementById('resena-form-title');
        if (mi && mi.idResena) {
          idField.value = mi.idResena;
          document.getElementById('resena-calificacion').value = String(mi.calificacion || 5);
          document.getElementById('resena-comentario').value = mi.comentario || '';
          title.textContent = 'Edita tu reseña (volverá a moderación)';
          setText('resena-hint', 'Estado actual: ' + (mi.estado || ''));
        } else {
          idField.value = '';
          title.textContent = 'Deja tu reseña';
          setText('resena-hint', '');
        }
      } else if (data.miResena) {
        formWrap.style.display = '';
        const mi = data.miResena;
        document.getElementById('resena-id').value = mi.idResena || '';
        document.getElementById('resena-form-title').textContent = 'Edita tu reseña';
        setText('resena-hint', 'Estado: ' + (mi.estado || '') + (mi.estado === 'Pendiente' ? ' (esperando moderación)' : ''));
        if (mi.comentario) document.getElementById('resena-comentario').value = mi.comentario;
        if (mi.calificacion) document.getElementById('resena-calificacion').value = String(mi.calificacion);
      } else {
        formWrap.style.display = 'none';
      }
    }

    const resenas = Array.isArray(data.resenas) ? data.resenas : [];
    if (!resenas.length) {
      listaEl.innerHTML = '<p style="opacity:0.7;">Aún no hay reseñas aprobadas para este producto.</p>';
      return;
    }

    listaEl.innerHTML = resenas.map((r) => `
      <div class="product-detail__review">
        <p class="product-detail__review-author">
          Usuario #${r.usuarioId ?? '?'} —
          <span class="product-detail__review-stars">${stars(r.calificacion)}</span>
          ${r.fechaCreacion ? `<span style="opacity:0.6;font-size:0.8rem;">(${String(r.fechaCreacion).slice(0, 10)})</span>` : ''}
        </p>
        <p class="product-detail__review-text">${escapeHtml(r.comentario || '')}</p>
      </div>
    `).join('');
  } catch (err) {
    console.error(err);
    listaEl.innerHTML = '<p>No se pudieron cargar las reseñas.</p>';
  }
}

function bindResenaForm() {
  const form = document.getElementById('resena-form');
  if (!form || form.dataset.bound === '1') return;
  form.dataset.bound = '1';

  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!currentProductId) return;

    const id = Number(document.getElementById('resena-id').value || 0);
    const calificacion = Number(document.getElementById('resena-calificacion').value);
    const comentario = document.getElementById('resena-comentario').value.trim();

    if (comentario.length < 10) {
      showToast('El comentario debe tener al menos 10 caracteres.', 'error');
      return;
    }

    const body = {
      productoId: currentProductId,
      calificacion,
      comentario
    };

    let result;
    if (id > 0) {
      body.idResena = id;
      result = await request(API_ENDPOINTS.resenas, { method: 'PUT', body });
    } else {
      result = await request(API_ENDPOINTS.resenas, { method: 'POST', body });
    }

    if (result.ok) {
      showToast(result.data?.message || 'Reseña enviada. Será publicada tras moderación.', 'success');
      form.reset();
      await cargarResenas();
    } else {
      showToast(result.error || 'No se pudo enviar la reseña.', 'error');
    }
  });
}

function setText(id, text) {
  const el = document.getElementById(id);
  if (el) el.textContent = text;
}

function escapeHtml(s) {
  if (s === null || s === undefined) return '';
  return String(s)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function renderErrorState(message) {
  const titleEl = document.getElementById('product-detail-title');
  const descriptionEl = document.getElementById('product-detail-description');

  if (titleEl) {
    titleEl.textContent = 'Producto no disponible';
  }

  if (descriptionEl) {
    descriptionEl.textContent = message;
  }

  showToast(message, 'error');
}

document.addEventListener('DOMContentLoaded', bindResenaForm);
