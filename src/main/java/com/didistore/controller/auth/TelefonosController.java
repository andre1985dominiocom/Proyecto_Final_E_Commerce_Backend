
package com.didistore.controller.auth;

import com.didistore.dao.impl.auth.TelefonosDAOImpl;
import com.didistore.dao.interfaces.auth.ITelefonosDAO;
import com.didistore.model.auth.Telefonos;
import com.didistore.model.auth.enums.TipoTelefonos;

import java.sql.Timestamp;
import java.util.List;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class TelefonosController {

    private final ITelefonosDAO telefonosDAO;

    public TelefonosController() {
        this.telefonosDAO = new TelefonosDAOImpl();
    }

    public void agregarTelefono(int usuarioId, String numero, TipoTelefonos tipo) {
        if (usuarioId <= 0 || numero == null || numero.trim().isEmpty() || tipo == null) {
            throw new IllegalArgumentException("Datos de teléfono inválidos");
        }

        Telefonos telefono = new Telefonos();
        telefono.setusuarioId(usuarioId);
        telefono.setnumero(numero);
        telefono.settipo(tipo);
        telefono.setfechaAgregado(new Timestamp(System.currentTimeMillis()));

        telefonosDAO.insertarTelefono(telefono);
    }

    public Telefonos consultarTelefonoPorUsuario(int usuarioId) {
        return telefonosDAO.consultarTelefonoPorUsuario(usuarioId);
    }

    public List<Telefonos> listarTelefonos() {
        return telefonosDAO.listarTelefono();
    }

    public void actualizarTelefono(Telefonos telefono) {
        telefonosDAO.actualizarTelefono(telefono);
    }

    public void eliminarTelefonoPorUsuario(int usuarioId) {
        telefonosDAO.eliminarTelefonoPorUsuario(usuarioId);
    }

    public void eliminarTelefonoPorId(int telefonoId) {
        telefonosDAO.eliminarTelefonoPorId(telefonoId);
    }
}