let cart = {
    items: [],
    totalItems: 0,
    total: 0
};

const subscribers = [];

export function setCart(data){

    cart = {
        items: data.items || [],
        total: data.total || 0,
        totalItems: calculateItems(data.items)
    };

    notify();
}

export function getCart(){
    return cart;
}

function calculateItems(items = []){

    return items.reduce(
        (total, item) =>
        total + Number(item.cantidad),
        0
    );
}

export function subscribe(callback) {

    subscribers.push(callback);

}

function notify(){

    subscribers.forEach(callback => {
        callback(cart);
    });
}