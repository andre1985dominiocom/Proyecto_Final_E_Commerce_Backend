package com.didistore.dao.interfaces.sales;

import com.didistore.model.sales.Pedidos;
import java.util.List;

/**
 *
 * @author DELL
 */
public interface IPedidosDAO {

    Pedidos guardarPedido(Pedidos pedido);

    List<Pedidos> listarPedidos();

    Pedidos consultarPedidoPorId(int idPedido);
}
