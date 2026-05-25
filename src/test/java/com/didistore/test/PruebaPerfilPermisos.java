
package com.didistore.test;

import com.didistore.dao.impl.auth.PerfilPermisosDAOImpl;
import com.didistore.model.auth.PerfilPermisos;
import com.didistore.dao.interfaces.auth.IPerfilPermisosDAO;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PruebaPerfilPermisos {

    public static void main(String[] args) {
        
        IPerfilPermisosDAO perfilPermisoDAO = new PerfilPermisosDAOImpl();
        
        /*
        Formato:
        { idPerdil, idPermiso }
        */
        
        int[][] relaciones = {
            
            //==========================
            // Administrador (Perfil 1)
            //==========================
            
            {1, 1}, // ESYIONAR_USUARIOS
            {1, 2}, // GESTIONAR_PERFILES
            {1, 3}, // GESTIONAR_PERMISOS
            {1, 4}, // GESTIONAR_INVENTARIO
            {1, 5}, // CREAR_PRODUCTO
            {1, 6}, // EDITAR_PRODUCTO
            {1, 7}, // VER_PRODUCTOS
            {1, 8}, // ELIMINAR_PRODUCTO
            {1, 9}, // EDITAR_USUARIO
            {1, 10}, // VER_USUARIOS
            {1, 11}, // ELIMINAR_USUARIO
            {1, 12}, // VER_PEDIDOS
            {1, 13}, // ACTUALIZAR_ESTADO_PEDIDO
            {1, 14}, // CANCELAR_PEDIDO
            {1, 15}, // VER_REPORTES
            {1, 16}, // EXPORTAR_REPORTES
            {1, 17}, // CONFIGURAR_SISTEMA
            {1, 18}, // AGREGAR_CARRITO
            {1, 19}, // REALIZAR_COMPRA
            
            //====================
            // Empleado (Pefil 2)
            //====================
            {2, 4}, // GESTIONAR_INVENTARIO
            {2, 6}, // EDITAR_PRODUCTO
            {2, 7}, // VER_PRODUCTOS
            {2, 10}, //VER_USUARIOS
            {2, 12}, // VER_PEDIDOS
            {2, 13}, // ACTUALIZAR_ESTADO_PEDIDO
            
            //====================
            // Cliente (Perfil 3)
            //====================
            {3, 7}, // VER_PRODUCTOS
            {3, 18}, // AGREGAR_CARRITO
            {3, 19}, // REALIZAR_COMPRA
            {3, 20}, // VER_MIS_PEDIDOS
            {3, 21} // EDITAR_MI_PERFIL              
        };
        
        for (int[] r : relaciones) {
            
            PerfilPermisos perfilPermiso = new PerfilPermisos();
            
            perfilPermiso.setidPerfil(r[0]);
            perfilPermiso.setidPermiso(r[1]);
            
            perfilPermisoDAO.insertarPerfilPermisos(perfilPermiso);
            
            System.out.println("Relación registrada -> Perfil: " + r[0] + "Permiso" + r[1]);
        }
        
        System.out.println("Todas las relaciones fueron registrados correctamente.");
    }
}