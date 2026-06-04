package com.didistore.dao.interfaces.sales;

import com.didistore.model.sales.DetallesPedidos;
import java.util.List;

/**
 *
 * @author DELL
 */
public interface IDetallesPedidosDAO {

    void guardarDetalles(int pedidoId, List<DetallesPedidos> detalles);

    List<DetallesPedidos> listarDetallesPorPedido(int pedidoId);
}
