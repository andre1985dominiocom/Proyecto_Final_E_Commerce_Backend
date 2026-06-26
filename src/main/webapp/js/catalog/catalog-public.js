import { request } from "../core/http.js";
import { buildAppUrl } from "../core/config.js";

let catalogoCargado = false;

document.addEventListener("DOMContentLoaded", async () => {

    if (catalogoCargado) {
        return;
    }
    
    await cargarCatalogo();
});

async function cargarCatalogo(){

    if (catalogoCargado) {
        console.log("Catálogo ya cargado");
        return;
    }

    catalogoCargado = true;

    try {
        const respuesta = await request("/catalog/productos?vista=publica");
        console.log("RESPUESTA COMPLETA:", respuesta.data);

        if(!respuesta.ok){
            console.error("Error backend:", respuesta.error);

            catalogoCargado = false;
            return;
        }

        mostrarProductos(respuesta.data);
    } catch(error){

        catalogoCargado = false;
        console.error("Error cargando catálogo", error);
    }
}

function mostrarProductos(productos){

    const contenedor = document.getElementById("catalogo-productos");

    if(!contenedor){
        console.error("No existe catalogo-productos");
        return;
    }

    contenedor.innerHTML = "";

    productos.forEach(producto => {
        
        const imagen = producto.imagenUrl
            ? buildAppUrl(producto.imagenUrl)
            : "";

        console.log("PRODUCTO:", producto.nombreProducto);

        console.log("IMAGEN FINAL:", imagen);

        contenedor.insertAdjacentHTML(
            "beforeend",
            `
            <div class="product-card">
                <div class="product-card__image">
                    <img 
                        src="${imagen}"
                        alt="${producto.nombreProducto}"
                    >
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
                        class="btn btn--primary btn--block btn-agregar-carrito"
                        data-product-id="${producto.idProducto}"
                    >
                        <i class="fa-solid fa-cart-plus"></i>
                        Agregar al Carrito
                    </button>
                </div>
            </div>
            `
        );
    });
    
    activarBotonesCarrito();
    
    function activarBotonesCarrito(){

        document.querySelectorAll(".btn-agregar-carrito").forEach(btn => {

            btn.addEventListener("click", async() => {

                const productoId = btn.dataset.productId;

                const card = btn.closest(".product-card");

                const precio = card.querySelector(".product-card__price-current")
                                    .textContent
                                    .replace("$","")
                                    .replace(".","")
                                    .trim();

                const params = new URLSearchParams();

                params.append("accion","agregar");
                params.append("productoId",productoId);
                params.append("cantidad",1);
                params.append("precioUnitario",precio);

                const url = buildAppUrl("/sales/carrito");
                    console.log("URL CARRITO:", url);

                const response = await fetch(url,{
                    method:"POST",
                    headers:{
                        "Content-Type":
                        "application/x-www-form-urlencoded"
                    },
                    body:params
                });

                const texto = await response.text();
                    console.log("RESPUESTA SERVIDOR CARRITO:", texto);

                try {

                    const data = JSON.parse(texto);

                    if(data.success) {
                        await actualizarContadorCarrito();
                        
                        alert("Producto agregado al carrito");
                    }
                } catch(error) {
                    console.error("Backend no devolvió JSON", texto);
                }
            });
        });
    }
    
    async function actualizarContadorCarrito(){

        try {
            
            const url = buildAppUrl("/sales/carrito?accion=ver");

            const response = await fetch(url);

            const data = await response.json();

            let cantidadTotal = 0;

            if(data.items) {

                data.items.forEach(item => {
                    cantidadTotal += Number(item.cantidad);
                });
            }

            document.querySelectorAll(".badge--cart").forEach((contador) => {
                contador.textContent = cantidadTotal;
            });
        } catch(error) {
        console.error("Error contador carrito", error);
        }
    }
}