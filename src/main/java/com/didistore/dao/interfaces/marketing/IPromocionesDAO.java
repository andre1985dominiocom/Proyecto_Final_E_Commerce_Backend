package com.didistore.dao.interfaces.marketing;

import com.didistore.model.marketing.Promociones;
import java.util.List;

// Interfaz para la gestión de promociones de marketing (RF-09 / banner).
public interface IPromocionesDAO {

    List<Promociones> listarPromociones();

    Promociones buscarPorId(int idPromocion);

    List<Promociones> listarActivas();

    boolean insertarPromocion(Promociones promocion);

    boolean actualizarPromocion(Promociones promocion);

    boolean eliminarPromocion(int idPromocion);
}
