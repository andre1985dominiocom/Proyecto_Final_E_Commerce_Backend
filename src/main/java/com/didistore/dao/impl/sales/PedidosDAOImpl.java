package com.didistore.dao.impl.sales;

import com.didistore.dao.interfaces.sales.IPedidosDAO;
import com.didistore.model.sales.Pedidos;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author DELL
 */
public class PedidosDAOImpl implements IPedidosDAO {

    private static final AtomicInteger SECUENCIA_PEDIDOS = new AtomicInteger(1);
    private static final Map<Integer, Pedidos> PEDIDOS = new ConcurrentHashMap<>();

    @Override
    public Pedidos guardarPedido(Pedidos pedido) {
        Pedidos copia = copiarPedido(pedido);

        if (copia.getidPedido() <= 0) {
            copia.setidPedido(SECUENCIA_PEDIDOS.getAndIncrement());
        }

        if (copia.getfechaPedido() == null) {
            copia.setfechaPedido(new Timestamp(System.currentTimeMillis()));
        }

        PEDIDOS.put(copia.getidPedido(), copia);
        return copiarPedido(copia);
    }

    @Override
    public List<Pedidos> listarPedidos() {
        List<Pedidos> lista = new ArrayList<>();
        for (Pedidos pedido : PEDIDOS.values()) {
            lista.add(copiarPedido(pedido));
        }
        lista.sort(Comparator
                .comparing(Pedidos::getfechaPedido, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(Pedidos::getidPedido, Comparator.reverseOrder()));
        return lista;
    }

    @Override
    public Pedidos consultarPedidoPorId(int idPedido) {
        Pedidos pedido = PEDIDOS.get(idPedido);
        return pedido != null ? copiarPedido(pedido) : null;
    }

    private Pedidos copiarPedido(Pedidos pedido) {
        Pedidos copia = new Pedidos();
        copia.setidPedido(pedido.getidPedido());
        copia.setnumeroPedido(pedido.getnumeroPedido());
        copia.setusuarioId(pedido.getusuarioId());
        copia.setdireccionEnvioId(pedido.getdireccionEnvioId());
        copia.setestadoPedido(pedido.getestadoPedido());
        copia.setsubTotal(pedido.getsubTotal());
        copia.setdescuento(pedido.getdescuento());
        copia.setiva(pedido.getiva());
        copia.setcostoEnvio(pedido.getcostoEnvio());
        copia.setmontoTotal(pedido.getmontoTotal());
        copia.setcuponId(pedido.getcuponId());
        copia.setfechaPedido(pedido.getfechaPedido());
        return copia;
    }
}
