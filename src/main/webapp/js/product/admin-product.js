import { showToast } from '../core/ui.js';

const API_BASE = 'http://localhost:8080/didistorebackend';

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('admin-product-form');
    const table = document.querySelector('table');
    const formInventario = document.getElementById('form-inventario');
    
    const cerrarModal = () => document.getElementById('modal-inventario').style.display = 'none';
        document.getElementById('btn-cerrar-modal')?.addEventListener('click', cerrarModal);
        document.getElementById('btn-cancelar-modal')?.addEventListener('click', cerrarModal);

    // --- Inicialización sección: Formulario de Crear / Editar Producto ---
    if (form) {
        form.addEventListener('submit', handleSubmit);
        cargarCategorias();
        cargarProductoSiEsEdicion();
    }

    // --- Inicialización sección: Delegación de Inventarios en la Tabla ---
    if (table) {
        table.addEventListener('click', (e) => {
            if (e.target.matches('[data-stock-id]')) {
                const productoId = e.target.getAttribute('data-stock-id');
                abrirModalInventario(productoId);
            }
        });
    }

    // --- Inicialización sección: Guardar datos de la Modal ---
    if (formInventario) {
        formInventario.addEventListener('submit', guardarCambiosInventario);
    }
});

/* ==========================================================================
   LÓGICA DEL FORMULARIO DE PRODUCTOS (POST / PUT)
   ========================================================================== */

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
        const response = await fetch(`${API_BASE}/admin/productos`, {
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
        talla: '',
        color: '',
        categoriaId: Number(categoriaId),
        estado: mapearEstado(estadoFormulario),
        esDestacado: descuento > 0,
        stock
    };
}

function mapearEstado(estadoFormulario) {
    switch (estadoFormulario) {
        case 'active': return 'ACTIVO';
        case 'inactive': return 'INACTIVO';
        case 'draft': return 'AGOTADO';
        default: return 'ACTIVO';
    }
}

async function cargarCategorias() {
    const selectCategoria = document.getElementById('product-category');
    if (!selectCategoria) return;

    try {
        const response = await fetch(`${API_BASE}/catalog/categorias`);
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
        const response = await fetch(`${API_BASE}/catalog/productos?idProducto=${encodeURIComponent(idProducto)}`);
        const producto = await leerRespuestaJSON(response);

        if (!response.ok) throw new Error(producto.message || 'No se pudo cargar el producto');

        llenarFormulario(producto);

    } catch (error) {
        console.error('Error al cargar producto:', error);
        mostrarMensaje(error.message || 'No se pudo cargar el producto para edición.', 'error');
    }
}

function llenarFormulario(producto) {
    setValue('idProducto', producto.idProducto);
    setValue('url', producto.url || '');
    setValue('nombreProducto', producto.nombreProducto);
    setValue('categoriaId', producto.categoriaId);
    setValue('precio', producto.precio);
    setValue('sku', producto.sku);
    
    const estadoSelect = document.getElementById('estado');
    if (estadoSelect) {
        estadoSelect.value = mapearEstadoFormulario(producto.estado);
    }
}

function mapearEstadoFormulario(estadoBackend) {
    switch (estadoBackend) {
        case 'ACTIVO': return 'active';
        case 'INACTIVO': return 'inactive';
        case 'AGOTADO': return 'draft';
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

/* ==========================================================================
   MÓDULO DE GESTIÓN DE INVENTARIOS (MODAL & FETCH REST)
   ========================================================================== */

function abrirModalInventario(productoId) {
    // Corregido: Se añade API_BASE para evitar desvíos de URL de subcarpetas
    fetch(`${API_BASE}/admin/inventarios?id=${productoId}`)
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

    fetch(`${API_BASE}/admin/inventarios`, {
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
            
            // Opcional: Aquí puedes meter la función que recargue tu tabla de productos de forma asíncrona
        } else {
            mostrarMensaje(`Error: ${res.error}`, 'error');
        }
    })
    .catch(error => {
        console.error('Error al actualizar:', error);
        mostrarMensaje('Error de red al intentar actualizar el inventario.', 'error');
    });
}

/* ==========================================================================
   UTILIDADES COMPARTIDAS
   ========================================================================== */

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
    // Integración de tus Toasts del core UI
    if (typeof showToast === 'function') {
        showToast(mensaje, tipo);
    } else {
        alert(mensaje);
    }
}