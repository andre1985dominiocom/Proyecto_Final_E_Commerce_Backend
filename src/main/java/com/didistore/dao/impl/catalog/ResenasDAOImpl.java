
package com.didistore.dao.impl.catalog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import com.didistore.config.Conexion;
import com.didistore.dao.interfaces.catalog.IResenasDAO;
import com.didistore.model.catalog.Resenas;
import com.didistore.model.catalog.enums.EstadoResenas;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Implementación de la interfaz IResenasDAO para gestionar las reseñas de productos en la base de datos
public class ResenasDAOImpl implements IResenasDAO {
    
    // Implementación de los métodos para gestionar las reseñas de productos
    @Override
    public boolean agregarResena(Resenas resena) {
        String sql = "INSERT INTO resenas (Usuario_ID, Producto_ID, Calificacion, Comentario, Estado) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getConexion();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, resena.getusuarioId());
            ps.setInt(2, resena.getproductoId());
            ps.setInt(3, resena.getcalificacion());
            ps.setString(4, resena.getcomentario());
            ps.setString(5, resena.getestado() != null ? resena.getestado().name() : null);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // UNIQUE (Usuario_ID, Producto_ID) → ya reseñó
            e.printStackTrace();
            return false;
        }
    }

    // Implementación de los métodos para gestionar las reseñas de productos
    @Override
    public boolean eliminarResena(int idResena) {
        // Lógica para eliminar una reseña de la base de datos
        String sql = "DELETE FROM Resenas WHERE ID_Resena = ?";

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idResena);

            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Reseña eliminada correctamente en la BD!");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Implementación de los métodos para gestionar las reseñas de productos
    @Override
    public List<Resenas> obtenerResenasPorProducto(int productoId) {
        // Lógica para obtener todas las reseñas de un producto específico
        List<Resenas> lista = new ArrayList<>();
        
        String sql = "SELECT ID_Resena, Usuario_ID, Producto_ID, Calificacion, Comentario, Estado, Fecha_creacion FROM Resenas WHERE Producto_ID = ? AND Estado = 'Aprobada' ORDER BY Fecha_creacion DESC";

        try (Connection con = Conexion.getConexion();
        PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, productoId);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearResena(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Implementación de los métodos para gestionar las reseñas de productos
    @Override
    public List<Resenas> obtenerResenasPorUsuario(int usuarioId) {
        // Lógica para obtener todas las reseñas realizadas por un usuario específico
        List<Resenas> lista = new ArrayList<>();
        
        String sql = "SELECT ID_Resena, Usuario_ID, Producto_ID, Calificacion, Comentario, Estado FROM Resenas WHERE Usuario_ID = ?";
        
        try (Connection con = Conexion.getConexion();
        PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, usuarioId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResena(rs));
                }
            }
        } catch (SQLException e) {
        e.printStackTrace();
        }
        return lista;
    }

    // Implementación de los métodos para gestionar las reseñas de productos
    @Override
    public List<Resenas> obtenerResenasPorEstado(String estado) {
        // Lógica para obtener todas las reseñas con un estado específico (aprobada, pendiente, rechazada)
        
        List<Resenas> lista = new ArrayList<>();
        
        String sql = "SELECT ID_Resena, Usuario_ID, Producto_ID, Calificacion, Comentario, Estado FROM Resenas WHERE Estado = ? ORDER BY Fecha_creacion DESC";

        try (Connection con = Conexion.getConexion();
        PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, estado);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearResena(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Implementación de los métodos para gestionar las reseñas de productos
    @Override
    public boolean actualizarResena(int idResena, String nuevoComentario, int nuevaCalificacion) {
        // Lógica para actualizar el comentario y la calificación de una reseña existente
        String sql = "UPDATE Resenas SET Calificacion = ?, Comentario = ? WHERE ID_Resena = ?";
                
        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, nuevaCalificacion);
            ps.setString(2, nuevoComentario);
            ps.setInt(3, idResena);
            
            int filasAfectadas = ps.executeUpdate();

            if (!con.getAutoCommit()) {
                con.commit();
            }

            if (filasAfectadas > 0) {
                System.out.println("¡Reseña actualizada correctamente en la BD!");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Implementación de los métodos para gestionar las reseñas de productos
    @Override
    public Resenas obtenerResenaPorId(int idResena) {
        // Lógica para obtener una reseña específica por su ID
        String sql = "SELECT ID_Resena, Usuario_ID, Producto_ID, Calificacion, Comentario, Estado FROM Resenas WHERE ID_Resena = ?";

        try (Connection con = Conexion.getConexion();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idResena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResena(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Implementación de los métodos para gestionar las reseñas de productos
    @Override
    public Resenas obtenerResenaPorUsuarioYProducto(int usuarioId, int productoId) {
        // Lógica para obtener la reseña realizada por un usuario específico en un producto específico

        String sql = "SELECT ID_Resena, Usuario_ID, Producto_ID, Calificacion, Comentario, Estado FROM Resenas WHERE Usuario_ID = ? AND Producto_ID = ?";
        
        try (Connection con = Conexion.getConexion();
        PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, usuarioId);
        ps.setInt(2, productoId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearResena(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Implementación de los métodos para gestionar las reseñas de productos
    @Override
    public Resenas consultarResena(int idResena) {

        return obtenerResenaPorId(idResena);
    }

    // Método privado para mapear los resultados de la consulta a un objeto Resenas
    private Resenas mapearResena(ResultSet rs) throws SQLException {

        Resenas resena = new Resenas();
        try {
            resena.setidResena(rs.getInt("ID_Resena"));
        } catch (SQLException ignored) {
            // columna no seleccionada en consultas antiguas
        }
        resena.setusuarioId(rs.getInt("Usuario_ID"));
        resena.setproductoId(rs.getInt("Producto_ID"));
        resena.setcalificacion(rs.getInt("Calificacion"));
        resena.setcomentario(rs.getString("Comentario"));

        String estadoStr = rs.getString("Estado");
        if (estadoStr != null && !estadoStr.isEmpty()) {
            resena.setestado(EstadoResenas.valueOf(estadoStr));
        }
        return resena;
    }

    // RF-21: transición de moderación Pendiente → Aprobada/Rechazada.
    @Override
    public boolean actualizarEstadoResena(int idResena, String nuevoEstado) {
        String sql = "UPDATE Resenas SET Estado = ? WHERE ID_Resena = ? AND Estado = 'Pendiente'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, idResena);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // RF-21: verifica compra con pedido en estado Entregado.
    @Override
    public boolean haCompradoEntregado(int usuarioId, int productoId) {
        String sql = "SELECT COUNT(*) FROM Detalles_Pedidos d "
                + "INNER JOIN Pedidos p ON p.ID_Pedido = d.Pedido_ID "
                + "WHERE p.Usuario_ID = ? AND d.Producto_ID = ? AND p.Estado_pedido = 'Entregado'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setInt(2, productoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public double promedioAprobadas(int productoId) {
        String sql = "SELECT AVG(Calificacion) FROM Resenas WHERE Producto_ID = ? AND Estado = 'Aprobada'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double v = rs.getDouble(1);
                    return rs.wasNull() ? 0.0 : v;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public int contarAprobadas(int productoId) {
        String sql = "SELECT COUNT(*) FROM Resenas WHERE Producto_ID = ? AND Estado = 'Aprobada'";
        try (Connection con = Conexion.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productoId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}