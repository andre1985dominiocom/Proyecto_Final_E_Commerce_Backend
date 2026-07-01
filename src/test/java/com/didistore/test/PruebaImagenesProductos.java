
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
        
//        ImagenesProductos imagen1 = new ImagenesProductos();
//        
//        imagen1.setProductoId(1);
//        imagen1.setUrl("assets/images/products/WhatsApp-Image-2025-08-29-at-4.48.24-PM.jpg");
//        imagen1.setFormato("jpg");
//   
//        ImagenesProductos imagen2 = new ImagenesProductos();
//        
//        imagen2.setProductoId(2);
//        imagen2.setUrl("assets/images/products/WhatsApp-Image-2025-08-29-at-4.48.26-PM-4.jpg");
//        imagen2.setFormato("jpg");
        
//        ImagenesProductos imagen3 = new ImagenesProductos();
//        
//        imagen3.setProductoId(3);
//        imagen3.setUrl("assets/images/products/Foto-22-08-24-4-02-52-p.-m.jpg");
//        imagen3.setFormato("jpg");

//        ImagenesProductos imagen4 = new ImagenesProductos();
//          
//        imagen4.setProductoId(4);
//        imagen4.setUrl("assets/images/products/cosmetiqueras/box-organizador.jpg");
//        imagen4.setFormato("jpg");

//        ImagenesProductos imagen5 = new ImagenesProductos();
//        imagen5.setProductoId(5);
//        imagen5.setUrl("assets/images/products/cosmetiqueras/organizador transparente grande.png");
//        imagen5.setFormato("png");

//        ImagenesProductos imagen6 = new ImagenesProductos();
//        imagen6.setProductoId(6);
//        imagen6.setUrl("assets/images/products/cosmetiqueras/cosmetiquera aly cebra.png");
//        imagen6.setFormato("png");

//        ImagenesProductos imagen7 = new ImagenesProductos();
//        imagen7.setProductoId(7);
//        imagen7.setUrl("assets/images/products/cosmetiqueras/cosmetiquera-grande-travel.jpg");
//        imagen7.setFormato("jpg");

//        ImagenesProductos imagen8 = new ImagenesProductos();
//        imagen8.setProductoId(8);
//        imagen8.setUrl("assets/images/products/bolso-rumania-negro-2.jpg");
//        imagen8.setFormato("jpg");

          ImagenesProductos imagen9 = new ImagenesProductos();
          imagen9.setProductoId(9);
          imagen9.setUrl("assets/images/products/MORRAL VICTORIA.jpg");
          imagen9.setFormato("jpg");

//        boolean url1 = imagenDAO.insertarImagen(imagen1);
//
//        boolean url2 = imagenDAO.insertarImagen(imagen2);
        
//        boolean url3 = imagenDAO.insertarImagen(imagen3);

//        boolean url4 = imagenDAO.insertarImagen(imagen4);

//        boolean url5 = imagenDAO.insertarImagen(imagen5);

//        boolean url6 = imagenDAO.insertarImagen(imagen6);

//        boolean url7 = imagenDAO.insertarImagen(imagen7);

//        boolean url8 = imagenDAO.insertarImagen(imagen8);

          boolean url9 = imagenDAO.insertarImagen(imagen9);

//        System.out.println("Imagen 1: " + url1);
//
//        System.out.println("Imagen 2: " + url2);
        
//        System.out.println("Imagen 3: " + url3);

//        System.out.println("Imagen 4: " + url4);

//        System.out.println("Imagen 5: " + url5);

//        System.out.println("Imagen 6: " + url6);

//        System.out.println("Imagen 7: " + url7);

//        System.out.println("Imagen 8: " + url8);

          System.out.println("Imagen 9: " + url9);
    }
}