import { CATALOG_ENDPOINTS, STORAGE_KEYS } from '../core/config.js';
import { request } from '../core/http.js';
import { getJSON } from '../core/storage.js';
import { formatCurrency, showToast } from '../core/ui.js';

initProductDetail();

function renderProduct(product) {
  const name = product.nombreProducto || 'Producto';
  const price = Number(product.precio || 0);
  const description = product.descripcionCorta || '';
  const sku = product.sku || product.idProducto || '';
  const categoryName = product.nombreCategoria || '';
  const imageUrl = product.imagenUrl || '';
  const categoryId = product.categoriaId || '';
  const stock = Number(product.stock ?? 0);

  const titleEl = document.getElementById('product-detail-title');
  if (titleEl) titleEl.textContent = name;
  document.title = `${name} - DidiStore`;

  const skuEl = document.getElementById('product-detail-sku');
  if (skuEl) skuEl.textContent = sku ? `Ref: ${sku}` : '';

  const priceEl = document.getElementById('product-detail-price');
  if (priceEl) priceEl.textContent = formatCurrency(price);

  const descriptionEl = document.getElementById('product-detail-description');
  if (descriptionEl) descriptionEl.textContent = description;

  const tabDescriptionEl = document.getElementById('product-detail-tab-description');
  if (tabDescriptionEl) tabDescriptionEl.textContent = description;

  const stockEl = document.getElementById('product-detail-stock');
  if (stockEl) {
    if (stock > 0) {
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
  if (breadcrumbName) breadcrumbName.textContent = name;

  if (imageUrl) {
    const mainImage = document.getElementById('mainImage');
    if (mainImage) {
      mainImage.src = imageUrl;
      mainImage.alt = name;
    }

    document.querySelectorAll('.product-detail__thumbnail').forEach((thumb) => {
      thumb.src = imageUrl;
      thumb.alt = name;
    });
  }
}

async function initProductDetail() {
  const params = new URLSearchParams(window.location.search);
  const idProducto = params.get('idProducto') || params.get('id');

  if (!idProducto) {
    showToast('No se indicó el producto a consultar.', 'warning');
    return;
  }

  const response = await request(`${CATALOG_ENDPOINTS.productos}?idProducto=${encodeURIComponent(idProducto)}`);
  if (response.ok && response.data) {
    const product = Array.isArray(response.data) ? response.data[0] : response.data;
    if (product) {
      renderProduct(product);
      return;
    }
  }

  const fallbackProduct = getProductFromLocalCache(idProducto);
  if (fallbackProduct) {
    renderProduct(fallbackProduct);
    showToast('Producto cargado desde datos locales.', 'warning');
    return;
  }

  showToast(response.error || 'No se pudo cargar el producto.', 'error');
}

function getProductFromLocalCache(idProducto) {
  const productId = String(idProducto);
  const adminData = getJSON(STORAGE_KEYS.adminData, {});
  const products = Array.isArray(adminData.products) ? adminData.products : [];
  return products.find((product) => String(product.idProducto || product.id || product.codigo || '') === productId) || null;
}
