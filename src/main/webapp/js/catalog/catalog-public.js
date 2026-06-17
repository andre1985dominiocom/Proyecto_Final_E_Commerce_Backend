import { request } from "../core/http.js";
import { buildAppUrl } from "../core/config.js";

document.addEventListener("DOMContentLoaded",
    cargarCatalogo
);

async function cargarCatalogo(){

    try {
        const respuesta = await request("/catalog/productos");
        
       if(!respuesta.ok){
            console.error("Error backend:",respuesta.error);
            return;
        }
        mostrarProductos(respuesta.data);
    }catch(error){
        console.error("Error cargando catálogo", error);
    }
}

function mostrarProductos(productos){
       
    const contenedor =
        document.getElementById("catalogo-productos");

        if(!contenedor){ 
            console.error("No existe catalogo-productos");
            return;
        }


        contenedor.innerHTML="";

        productos.forEach(producto=>{
                  
        contenedor.innerHTML += `

            <div class="product-card">
                <div class="product-card__image">
                    <img src="${buildAppUrl(producto.imagenUrl)}" alt="${producto.nombreProducto}">
                </div>

                <div class="product-card__content">
                    <h3 class="product-card__title">       
                        ${producto.nombreProducto}
                    </h3>

                    <div class="product-card__price">
                        <span class="product-card__price-current">
                            $${producto.precio}
                        </span>
                    </div>

                    <button 
                        class="btn btn--primary btn--block" data-product-id="${producto.idProducto}">
                            <i class="fa-solid fa-cart-plus"></i>
                                Agregar al Carrito
                    </button>
                </div>
            </div>
        `;
    });
}