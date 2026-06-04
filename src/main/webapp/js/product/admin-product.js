import { buildApiUrl } from '../core/config.js';
import { showToast } from '../core/ui.js';

const API_BASE = buildApiUrl('');

const PRODUCT_ENDPOINT = buildApiUrl('/admin/productos');
const CATEGORY_ENDPOINT = buildApiUrl('/catalog/categorias');
const PRODUCT_CATALOG_ENDPOINT = buildApiUrl('/catalog/productos');
const INVENTORY_ENDPOINT = buildApiUrl('/admin/inventarios');

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('admin-product-form');
    const table = document.querySelector('table');
    const formInventario = document.getElementById('form-inventario');

    const cerrarModal = () => document.getElementById('modal-inventario').style.display = 'none';
    document.getElementById('btn-cerrar-modal')?.addEventListener('click', cerrarModal);
    document.getElementById('btn-cancelar-modal')?.addEventListener('click', cerrarModal);

    if (form) {
        form.addEventListener('submit', handleSubmit);
        cargarCategorias();
        cargarProductoSiEsEdicion();
    }

    if (table) {
        table.addEventListener('click', (e) => {
            if (e.target.matches('[data-stock-id]')) {
                const productoId = e.target.getAttribute('data-stock-id');
                abrirModalInventario(productoId);
            }
        });
    }

    if (formInventario) {
        formInventario.addEventListener('submit', guardarCambiosInventario);
    }
});

async function handleSubmit(event) {
    event.preventDefault();
    const form = event.target;
    const producto = construirProductoDesdeFormulario();

    if (!producto) {
        mostrarMensaje('Datos del formulario inválidos o faltan campos obligatorios.', 'error');
        return;
    }

    const idProducto = obtenerIdProductoDesdeURL();
    const metodo = idProducto ? 'PUT' : 'POST';

    if (idProducto) {
        producto.idProducto = idProducto;
    }

    try {
        const response = await fetch(PRODUCT_ENDPOINT, {
            method: metodo,
            headers: {
                'Content-Type': 'application/json; charset=UTF-8'
            },
            body: JSON.stringify(producto)
        });

        const data = await leerRespuestaJSON(response);

        if (!response.ok) {
            throw new Error(data.message || 'No se pudo guardar el producto');
        }

        mostrarMensaje(
            data.message || (idProducto ? 'Producto actualizado correctamente' : 'Producto creado correctamente'),
            'success'
        );

        if (!idProducto) {
            form.reset();
        }

    } catch (error) {
        console.error('Error al guardar producto:', error);
        mostrarMensaje(error.message || 'Error al guardar producto', 'error');
    }
}

function construirProductoDesdeFormulario() {
    const nombreProducto = document.getElementById('product-name')?.value.trim() || '';
    const sku = document.getElementById('product-sku')?.value.trim() || '';
    const descripcion = document.getElementById('product-description')?.value.trim() || '';
    const precio = Number(document.getElementById('product-price')?.value || 0);
    const stock = Number(document.getElementById('product-stock')?.value || 0);
    const categoriaId = document.getElementById('product-category')?.value || '';
    const estadoFormulario = document.getElementById('product-status')?.value || 'active';
    const descuento = Number(document.getElementById('product-discount')?.value || 0);

    if (!nombreProducto || precio <= 0 || stock < 0 || !categoriaId) {
        return null;
    }

    return {
        nombreProducto,
        descripcionCorta: descripcion,
        descripcionLarga: descripcion,
        precio,
        sku,
        talla: null,
        color: '',
        categoriaId: Number(categoriaId),
        estado: mapearEstado(estadoFormulario),
        esDestacado: descuento > 0,
        stock
    };
}

function mapearEstado(estadoFormulario) {
    switch (estadoFormulario) {
        case 'active': return 'Activo';
        case 'inactive': return 'Inactivo';
        case 'draft': return 'Agotado';
        default: return 'Activo';
    }
}

async function cargarCategorias() {
    const selectCategoria = document.getElementById('product-category');
    if (!selectCategoria) return;

    try {
        const response = await fetch(CATEGORY_ENDPOINT);
        const categorias = await leerRespuestaJSON(response);

        if (!response.ok) throw new Error('No se pudieron cargar las categorías');
        if (!Array.isArray(categorias)) return;

        const opcionVacia = `<option value="">Selecciona una categoría</option>`;
        const opciones = categorias.map((categoria) => `
            <option value="${categoria.idCategoria}">
                ${categoria.nombreCategoria ?? 'Categoría'}
            </option>
        `).join('');

        selectCategoria.innerHTML = opcionVacia + opciones;

        const categoriaIdURL = new URLSearchParams(window.location.search).get('categoriaId');
        if (categoriaIdURL) {
            selectCategoria.value = categoriaIdURL;
        }

    } catch (error) {
        console.error('Error al cargar categorías:', error);
        mostrarMensaje('No se pudieron cargar las categorías.', 'error');
    }
}

async function cargarProductoSiEsEdicion() {
    const idProducto = obtenerIdProductoDesdeURL();
    if (!idProducto) return;

    try {
        const response = await fetch(`${PRODUCT_CATALOG_ENDPOINT}?idProducto=${encodeURIComponent(idProducto)}`);
        const producto = await leerRespuestaJSON(response);

        if (!response.ok) throw new Error(producto.message || 'No se pudo cargar el producto');

        llenarFormulario(producto);

    } catch (error) {
        console.error('Error al cargar producto:', error);
        mostrarMensaje(error.message || 'No se pudo cargar el producto para edición.', 'error');
    }
}

function llenarFormulario(producto) {
    setValue('product-name', producto.nombreProducto);
    setValue('product-description', producto.descripcionCorta || producto.descripcionLarga || '');
    setValue('product-category', producto.categoriaId);
    setValue('product-price', producto.precio);
    setValue('product-sku', producto.sku);

    const estadoSelect = document.getElementById('product-status');
    if (estadoSelect) {
        estadoSelect.value = mapearEstadoFormulario(producto.estado);
    }
}

function mapearEstadoFormulario(estadoBackend) {
    switch (estadoBackend) {
        case 'Activo': return 'active';
        case 'Inactivo': return 'inactive';
        case 'Agotado': return 'draft';
        default: return 'active';
    }
}

function obtenerIdProductoDesdeURL() {
    const id = new URLSearchParams(window.location.search).get('idProducto');
    return id ? Number(id) : null;
}

function setValue(id, value) {
    const element = document.getElementById(id);
    if (element) {
        element.value = value ?? '';
    }
}

function abrirModalInventario(productoId) {
    fetch(`${INVENTORY_ENDPOINT}?id=${productoId}`)
        .then(response => {
            if (!response.ok) throw new Error('Error al recuperar datos del servidor');
            return response.json();
        })
        .then(inventario => {
            document.getElementById('inv-id').value = inventario.idInventario;
            document.getElementById('inv-actual').value = inventario.stockActual;
            document.getElementById('inv-minimo').value = inventario.stockMinimo;
            document.getElementById('inv-reservado').value = inventario.stockReservado;

            const modal = document.getElementById('modal-inventario');
            if (modal) modal.style.display = 'flex';
        })
        .catch(error => {
            console.error(error);
            mostrarMensaje('No se pudo cargar el inventario del producto.', 'error');
        });
}

function guardarCambiosInventario(e) {
    e.preventDefault();

    const datos = {
        idInventario: parseInt(document.getElementById('inv-id').value),
        stockActual: parseInt(document.getElementById('inv-actual').value),
        stockMinimo: parseInt(document.getElementById('inv-minimo').value),
        stockReservado: parseInt(document.getElementById('inv-reservado').value)
    };

    fetch(INVENTORY_ENDPOINT, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json; charset=UTF-8'
        },
        body: JSON.stringify(datos)
    })
    .then(response => response.json())
    .then(res => {
        if (res.success) {
            mostrarMensaje('¡Inventario actualizado con éxito!', 'success');
            const modal = document.getElementById('modal-inventario');
            if (modal) modal.style.display = 'none';
        } else {
            mostrarMensaje(`Error: ${res.error}`, 'error');
        }
    })
    .catch(error => {
        console.error('Error al actualizar:', error);
        mostrarMensaje('Error de red al intentar actualizar el inventario.', 'error');
    });
}

async function leerRespuestaJSON(response) {
    const text = await response.text();
    try {
        return text ? JSON.parse(text) : {};
    } catch {
        return {};
    }
}

function mostrarMensaje(mensaje, tipo = 'info') {
    console.log(`[${tipo.toUpperCase()}] ${mensaje}`);
    if (typeof showToast === 'function') {
        showToast(mensaje, tipo);
    } else {
        alert(mensaje);
    }
}
