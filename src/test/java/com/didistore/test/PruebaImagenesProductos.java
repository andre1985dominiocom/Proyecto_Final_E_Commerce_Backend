
package com.didistore.test;

import com.didistore.dao.impl.catalog.ImagenesProductosDAOImpl;
import com.didistore.dao.interfaces.catalog.IImagenesProductosDAO;
import com.didistore.model.catalog.ImagenesProductos;


/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PruebaImagenesProductos {

    public static void main(String[] args) {
        
        IImagenesProductosDAO imagenDAO = new ImagenesProductosDAOImpl();
        
        ImagenesProductos imagen1 = new ImagenesProductos();
        
        imagen1.setProductoId(1);
        imagen1.setUrl("assets/images/products/WhatsApp-Image-2025-08-29-at4.48.24-PM.jpg");
        imagen1.setFormato("jpg");
   
        ImagenesProductos imagen2 = new ImagenesProductos();
        
        imagen2.setProductoId(2);
        imagen2.setUrl("assets/images/products/WhatsApp-Image-2025-08-29-at4.48.26-PM-4.jpg");
        imagen2.setFormato("jpg");

        boolean url1 = imagenDAO.insertarImagen(imagen1);

        boolean url2 = imagenDAO.insertarImagen(imagen2);

        System.out.println("Imagen 1: " + url1);

        System.out.println("Imagen 2: " + url2);
    }
}