package co.edu.grado.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import co.edu.grado.modelo.Notificacion;
import co.edu.grado.util.Conexion;
import co.edu.grado.util.FechaUtil;

/** Notificaciones mostradas en la interfaz. (RF59) */
public class NotificacionDAO {

    public void crear(int usuarioId, String mensaje, String enlace) {
        String sql = "INSERT INTO notificaciones (usuario_id, mensaje, enlace) VALUES (?,?,?)";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, usuarioId);
            ps.setString(2, mensaje);
            ps.setString(3, enlace);
            ps.executeUpdate();
        } catch (Exception ignored) {
            // no interrumpe el flujo principal
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public List<Notificacion> listar(int usuarioId, boolean soloNoLeidas) {
        List<Notificacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM notificaciones WHERE usuario_id=? "
                + (soloNoLeidas ? "AND leida = FALSE " : "") + "ORDER BY fecha DESC LIMIT 50";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, usuarioId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Notificacion n = new Notificacion();
                n.setId(rs.getInt("id"));
                n.setUsuarioId(rs.getInt("usuario_id"));
                n.setMensaje(rs.getString("mensaje"));
                n.setEnlace(rs.getString("enlace"));
                n.setLeida(rs.getBoolean("leida"));
                n.setFecha(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha")));
                lista.add(n);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando notificaciones", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    public int contarNoLeidas(int usuarioId) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement("SELECT COUNT(*) FROM notificaciones WHERE usuario_id=? AND leida = FALSE");
            ps.setInt(1, usuarioId);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            return 0;
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public void marcarLeidas(int usuarioId) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement("UPDATE notificaciones SET leida = TRUE WHERE usuario_id=?");
            ps.setInt(1, usuarioId);
            ps.executeUpdate();
        } catch (Exception ignored) {
            // silencioso
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }
}
