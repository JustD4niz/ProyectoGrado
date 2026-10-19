package co.edu.grado.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import co.edu.grado.modelo.HistorialAccion;
import co.edu.grado.util.Conexion;
import co.edu.grado.util.FechaUtil;

/** Historial de acciones del sistema (quien, que y cuando). (RNF16) */
public class HistorialDAO {

    public void registrar(Integer usuarioId, String entidad, Integer entidadId, String accion, String detalle) {
        String sql = "INSERT INTO historial_acciones (usuario_id, entidad, entidad_id, accion, detalle) "
                + "VALUES (?,?,?,?,?)";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            if (usuarioId == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, usuarioId);
            }
            ps.setString(2, entidad);
            if (entidadId == null) {
                ps.setNull(3, java.sql.Types.INTEGER);
            } else {
                ps.setInt(3, entidadId);
            }
            ps.setString(4, accion);
            ps.setString(5, detalle);
            ps.executeUpdate();
        } catch (Exception ignored) {
            // el historial no debe interrumpir la operacion principal
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public List<HistorialAccion> listar(String entidad, Integer entidadId, int pagina) {
        List<HistorialAccion> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT h.*, u.nombre AS nombre_usuario FROM historial_acciones h "
                + "LEFT JOIN usuarios u ON u.id=h.usuario_id WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (entidad != null && !entidad.isEmpty()) {
            sql.append(" AND h.entidad=?");
            params.add(entidad);
        }
        if (entidadId != null) {
            sql.append(" AND h.entidad_id=?");
            params.add(entidadId);
        }
        sql.append(" ORDER BY h.fecha DESC LIMIT 50 OFFSET ?");
        params.add((pagina - 1) * 50);
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                HistorialAccion h = new HistorialAccion();
                h.setId(rs.getInt("id"));
                int uid = rs.getInt("usuario_id");
                h.setUsuarioId(rs.wasNull() ? null : uid);
                h.setEntidad(rs.getString("entidad"));
                int eid = rs.getInt("entidad_id");
                h.setEntidadId(rs.wasNull() ? null : eid);
                h.setAccion(rs.getString("accion"));
                h.setDetalle(rs.getString("detalle"));
                h.setFecha(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha")));
                h.setNombreUsuario(rs.getString("nombre_usuario"));
                lista.add(h);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando historial", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }
}
