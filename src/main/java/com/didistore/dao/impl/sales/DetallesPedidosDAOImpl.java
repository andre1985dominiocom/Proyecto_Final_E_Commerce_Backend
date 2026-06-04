package com.didistore.dao.impl.sales;

import com.didistore.dao.interfaces.sales.IDetallesPedidosDAO;
import com.didistore.model.sales.DetallesPedidos;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author DELL
 */
public class DetallesPedidosDAOImpl implements IDetallesPedidosDAO {

    private static final AtomicInteger SECUENCIA_DETALLES = new AtomicInteger(1);
    private static final Map<Integer, List<DetallesPedidos>> DETALLES_POR_PEDIDO = new ConcurrentHashMap<>();

    @Override
    public void guardarDetalles(int pedidoId, List<DetallesPedidos> detalles) {
        List<DetallesPedidos> copia = new ArrayList<>();

        for (DetallesPedidos detalle : detalles) {
            DetallesPedidos detalleCopia = copiarDetalle(detalle);
            if (detalleCopia.getidDetalle() <= 0) {
                detalleCopia.setidDetalle(SECUENCIA_DETALLES.getAndIncrement());
            }
            detalleCopia.setpedidoId(pedidoId);
            copia.add(detalleCopia);
        }

        DETALLES_POR_PEDIDO.put(pedidoId, copia);
    }

    @Override
    public List<DetallesPedidos> listarDetallesPorPedido(int pedidoId) {
        List<DetallesPedidos> lista = DETALLES_POR_PEDIDO.getOrDefault(pedidoId, List.of());
        List<DetallesPedidos> copia = new ArrayList<>();
        for (DetallesPedidos detalle : lista) {
            copia.add(copiarDetalle(detalle));
        }
        return copia;
    }

    private DetallesPedidos copiarDetalle(DetallesPedidos detalle) {
        DetallesPedidos copia = new DetallesPedidos();
        copia.setidDetalle(detalle.getidDetalle());
        copia.setpedidoId(detalle.getpedidoId());
        copia.setproductoId(detalle.getproductoId());
        copia.setcantidad(detalle.getcantidad());
        copia.setprecioUnitario(detalle.getprecioUnitario());
        copia.setsubtotal(detalle.getsubtotal());
        return copia;
    }
}
