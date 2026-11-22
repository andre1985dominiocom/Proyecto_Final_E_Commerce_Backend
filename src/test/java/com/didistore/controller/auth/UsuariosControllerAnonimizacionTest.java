package com.didistore.controller.auth;

import com.didistore.dao.interfaces.auth.IUsuariosDAO;
import com.didistore.model.auth.Usuarios;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * RF-13: derecho al olvido — anonimización con DAO falso (sin MySQL).
 */
class UsuariosControllerAnonimizacionTest {

    private UsuariosController controller;
    private FakeUsuariosDAO fakeDao;

    @BeforeEach
    void setUp() throws Exception {
        controller = new UsuariosController();
        fakeDao = new FakeUsuariosDAO();
        Field field = UsuariosController.class.getDeclaredField("usuariosDAO");
        field.setAccessible(true);
        field.set(controller, fakeDao);
    }

    @Test
    void anonimizarUsuarioInexistenteFalla() {
        Map<String, Object> r = controller.anonimizarUsuario(999);
        assertEquals(Boolean.FALSE, r.get("success"));
        assertTrue(String.valueOf(r.get("message")).contains("no encontrado"));
        assertFalse(fakeDao.anonimizo);
    }

    @Test
    void anonimizarBloqueadoSiHayPedidosActivos() {
        fakeDao.usuario = usuarioConId(5);
        fakeDao.pedidosActivos = true;

        Map<String, Object> r = controller.anonimizarUsuario(5);

        assertEquals(Boolean.FALSE, r.get("success"));
        assertTrue(String.valueOf(r.get("message")).toLowerCase().contains("activos"));
        assertFalse(fakeDao.anonimizo);
    }

    @Test
    void anonimizarExitosoSinPedidosActivos() {
        fakeDao.usuario = usuarioConId(7);
        fakeDao.pedidosActivos = false;
        fakeDao.resultadoAnonimizar = true;

        Map<String, Object> r = controller.anonimizarUsuario(7);

        assertEquals(Boolean.TRUE, r.get("success"));
        assertTrue(fakeDao.anonimizo);
        assertEquals(7, r.get("idUsuario"));
        assertEquals("usuario7@anonimizado.local", r.get("email"));
    }

    @Test
    void anonimizarFallaSiDaoRetornaFalse() {
        fakeDao.usuario = usuarioConId(8);
        fakeDao.pedidosActivos = false;
        fakeDao.resultadoAnonimizar = false;

        Map<String, Object> r = controller.anonimizarUsuario(8);

        assertEquals(Boolean.FALSE, r.get("success"));
        assertTrue(fakeDao.anonimizo);
    }

    @Test
    void tienePedidosActivosDelegaEnDao() {
        fakeDao.pedidosActivos = true;
        assertTrue(controller.tienePedidosActivos(1));
        fakeDao.pedidosActivos = false;
        assertFalse(controller.tienePedidosActivos(1));
    }

    private Usuarios usuarioConId(int id) {
        Usuarios u = new Usuarios();
        u.setidUsuario(id);
        u.setnombre("Ana");
        u.setapellido("Pérez");
        u.setemail("ana@ejemplo.com");
        u.setdocumento("1234567890");
        u.setestado(com.didistore.model.auth.enums.EstadoUsuarios.Activo);
        u.setfechaCreacion(new Timestamp(System.currentTimeMillis()));
        return u;
    }

    /** DAO en memoria que no toca MySQL. */
    static class FakeUsuariosDAO implements IUsuariosDAO {
        Usuarios usuario;
        boolean pedidosActivos;
        boolean resultadoAnonimizar = true;
        boolean anonimizo;

        @Override
        public boolean anonimizarUsuario(int idUsuario) {
            anonimizo = true;
            return resultadoAnonimizar;
        }

        @Override
        public boolean tienePedidosActivos(int idUsuario) {
            return pedidosActivos;
        }

        @Override
        public Usuarios consultarUsuariosPorId(int idUsuario) {
            if (usuario != null && usuario.getidUsuario() == idUsuario) {
                return usuario;
            }
            return null;
        }

        @Override
        public void insertarUsuarios(Usuarios u) { }

        @Override
        public List<Usuarios> listarUsuarios() {
            return new ArrayList<>();
        }

        @Override
        public Usuarios consultarUsuariosPorEmail(String email) {
            return null;
        }

        @Override
        public void actualizarUsuarios(Usuarios u) { }

        @Override
        public void actualizarContrasena(int idUsuario, String nuevaContrasena) { }

        @Override
        public void eliminarUsuarios(int idUsuario) { }

        @Override
        public boolean registrarUsuario(Usuarios u) {
            return false;
        }

        @Override
        public void registrarIntentoFallido(int idUsuario, int intentos, Timestamp bloqueadoHasta) { }

        @Override
        public void limpiarIntentosFallidos(int idUsuario, Timestamp fechaUltimoLogin) { }
    }
}
