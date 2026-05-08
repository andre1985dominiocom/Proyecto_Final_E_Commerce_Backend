
package com.didistore.model.modulesalesfinance;

import java.sql.Timestamp;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class Pedidos {
    private int idPedido;
    private String numeroPedido;
    private int usuarioId;
    private int direccionEnvioId;
    private String estadoPedido;
    private double subTotal;
    private double descuento;
    private double iva;
    private double costoEnvio;
    private double montoTotal;
    private int cuponId;
    private Timestamp fechaPedido;
    
    
    
}
