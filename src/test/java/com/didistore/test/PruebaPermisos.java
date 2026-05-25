
package com.didistore.test;

import com.didistore.dao.impl.auth.PermisosDAOImpl;
import com.didistore.dao.interfaces.auth.IPermisosDAO;
import com.didistore.model.auth.Permisos;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class PruebaPermisos {

   
    public static void main(String[] args) {
        
        IPermisosDAO permisoDAO = new PermisosDAOImpl();
                
        String[][] permisos = {
            {"GESTIONAR_USUARIO", "Permite administrar usuarios"},
            {"GESTIONAR_PERFILES", "Permite administrar perfiles"},
            {"GESTIONAR_PERMISOS", "Permite administrar permisos"},
            {"GESTIONAR_INVENTARIO", "Permite administrar inventario"},
            {"CREAR_PRODUCTO", "Permite crear producto"},
            {"EDITAR_PRODUCTO", "Permite editar producto"},
            {"VER_PRODUCTOS", "Permite ver productos"},
            {"ELIMINAR_PRODUCTO", "Permite elimnar producto"},
            {"EDITAR_USUARIO", "Permite editar usuariso"},
            {"VER_USUARIOS", "Permite ver usuarios"},
            {"ELIMNAR_USUARIO", "Permite eliminar usuarios"},
            {"VER_PEDIDOS", "Permite ver pedidos"},
            {"ACTUALIZAR_ESTADO_PEDIDO", "Permite actualizar estado pedido"},
            {"CANCELAR_PEDIDO", "Permite cancelar pedido"},
            {"VER_REPORTES", "Permiso para ver reportes"},
            {"EXPORTAR_REPORTES", "Permite exportar reportes"},
            {"CONFIGURAR_SISTEMA", "Permite configurar sistema"},
            {"AGREGAR_CARRITO", "Permite agregar productos al carrito"},
            {"REALIZAR_COMPRA", "Permite realizar compras"},
            {"VER_MIS_PEDIDOS", "Permite ver pedidos a los clientes"},
            {"EDITAR_MI_PERFIL", "Permite editar el perfil al cliente"}
        };
        
        for (String[] p: permisos) {
            
            Permisos nuevoPermiso = new Permisos();
        
            nuevoPermiso.setnombrePermiso(p[0]);
            nuevoPermiso.setdescripcionPermiso(p[1]);

            permisoDAO.insertarPermisos(nuevoPermiso);
            
            System.out.println("Permiso registrado: " + p[0]);
        }      
        System.out.println("Todos los permisos fueron registrados correctamente.");
    }
}