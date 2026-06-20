import { subscribe } from "./cart-state.js";

export function initCartBadge(){

    const badge =
        document.querySelector(".badge--cart");

    if(!badge){
        return;
    }

    subscribe(cart => {
        badge.textContent = cart.totalItems;
    });
}