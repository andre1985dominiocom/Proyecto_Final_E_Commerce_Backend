package com.didistore.dao.interfaces.marketing;

import com.didistore.model.marketing.Cupones;
import java.util.List;

// Interfaz para la gestión de cupones de descuento (RF-09).
public interface ICuponesDAO {

    List<Cupones> listarCupones();

    Cupones buscarPorId(int idCupon);

    Cupones buscarPorCodigo(String codigo);

    boolean insertarCupon(Cupones cupon);

    boolean actualizarCupon(Cupones cupon);

    boolean eliminarCupon(int idCupon);

    boolean incrementarUsos(int idCupon);

    boolean codigoExiste(String codigo);
}
