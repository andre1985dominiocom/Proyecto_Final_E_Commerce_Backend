import { CATALOG_ENDPOINTS } from '../core/config.js';
import { request } from '../core/http.js';
import { formatCurrency, showToast } from '../core/ui.js';

const productDetailRoot = document.getElementById('product-detail-title');

if (productDetailRoot) {
  initProductDetail();
}

async function initProductDetail() {
  const idProducto = new URLSearchParams(window.location.search).get('idProducto');

  if (!idProducto) {
    renderErrorState('No se indicó qué producto se debe mostrar.');
    return;
  }

  const response = await request(`${CATALOG_ENDPOINTS.productos}?idProducto=${encodeURIComponent(idProducto)}`);

  if (!response.ok || !response.data || Array.isArray(response.data)) {
    renderErrorState(response.error || 'No se pudo cargar el detalle del producto.');
    return;
  }

  renderProduct(response.data);
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
