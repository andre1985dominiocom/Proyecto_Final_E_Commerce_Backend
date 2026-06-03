import { API_ENDPOINTS, buildApiUrl } from '../core/config.js';
import { showToast } from '../core/ui.js';

document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementById('admin-category-form');
    if (form) {
        form.addEventListener('submit', handleCategorySubmit);
    }
});

async function handleCategorySubmit(event) {
    event.preventDefault();

    const nombreCategoria = document.getElementById('cat-name')?.value.trim() || '';
    const descripcion = document.getElementById('cat-description')?.value.trim() || '';

    if (!nombreCategoria) {
        showToast('El nombre de la categoría es obligatorio.', 'error');
        return;
    }

    const payload = { nombreCategoria, descripcion };

    try {
        const response = await fetch(buildApiUrl(API_ENDPOINTS.categories), {
            method: 'POST',
            headers: { 'Content-Type': 'application/json; charset=UTF-8' },
            body: JSON.stringify(payload)
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(data.message || 'No se pudo crear la categoría');
        }

        showToast(data.message || 'Categoría creada correctamente.', 'success');
        event.target.reset();
    } catch (error) {
        console.error('Error al crear categoría:', error);
        showToast(error.message || 'Error al crear la categoría.', 'error');
    }
}
