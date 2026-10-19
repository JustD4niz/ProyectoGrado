package co.edu.grado.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.grado.modelo.Solicitud;
import co.edu.grado.util.Conexion;
import co.edu.grado.util.FechaUtil;

/** Solicitudes de prorroga, cambio de director, retiro y PQRS. (RF48 - RF54) */
public class SolicitudDAO {

    public static final int TAMANO_PAGINA = 20;

    private static final String BASE =
            "SELECT s.*, u.nombre AS nombre_solicitante, p.titulo AS nombre_proyecto, "
            + "p.codigo AS codigo_proyecto, d.nombre AS nombre_docente_solicitado "
            + "FROM solicitudes s "
            + "JOIN usuarios u ON u.id=s.solicitante_id "
            + "JOIN proyectos p ON p.id=s.proyecto_id "
            + "LEFT JOIN usuarios d ON d.id=s.docente_solicitado_id ";

    private Solicitud mapear(ResultSet rs) throws Exception {
        Solicitud s = new Solicitud();
        s.setId(rs.getInt("id"));
        s.setProyectoId(rs.getInt("proyecto_id"));
        s.setTipo(rs.getString("tipo"));
        s.setSolicitanteId(rs.getInt("solicitante_id"));
        s.setJustificacion(rs.getString("justificacion"));
        s.setUrlCarta(rs.getString("url_carta"));
        s.setUrlComprobante(rs.getString("url_comprobante"));
        int doc = rs.getInt("docente_solicitado_id");
        s.setDocenteSolicitadoId(rs.wasNull() ? null : doc);
        s.setEstado(rs.getString("estado"));
        s.setObservacionesResolucion(rs.getString("observaciones_resolucion"));
        int rp = rs.getInt("resuelta_por");
        s.setResueltaPor(rs.wasNull() ? null : rp);
        s.setFecha(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha")));
        s.setFechaResolucion(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha_resolucion")));
        s.setNombreSolicitante(rs.getString("nombre_solicitante"));
        s.setNombreProyecto(rs.getString("nombre_proyecto"));
        s.setCodigoProyecto(rs.getString("codigo_proyecto"));
        s.setNombreDocenteSolicitado(rs.getString("nombre_docente_solicitado"));
        return s;
    }

    public int crear(Solicitud s) {
        String sql = "INSERT INTO solicitudes (proyecto_id, tipo, solicitante_id, justificacion, url_carta, "
                + "url_comprobante, docente_solicitado_id, estado) VALUES (?,?,?,?,?,?,?,'PENDIENTE')";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, s.getProyectoId());
            ps.setString(2, s.getTipo());
            ps.setInt(3, s.getSolicitanteId());
            ps.setString(4, s.getJustificacion());
            ps.setString(5, s.getUrlCarta());
            ps.setString(6, s.getUrlComprobante());
            if (s.getDocenteSolicitadoId() == null) {
                ps.setNull(7, java.sql.Types.INTEGER);
            } else {
                ps.setInt(7, s.getDocenteSolicitadoId());
            }
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error creando solicitud", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public void resolver(int id, String estado, String observaciones, int resueltaPor) {
        String sql = "UPDATE solicitudes SET estado=?, observaciones_resolucion=?, resuelta_por=?, "
                + "fecha_resolucion=? WHERE id=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setString(2, observaciones);
            ps.setInt(3, resueltaPor);
            ps.setTimestamp(4, FechaUtil.sql(FechaUtil.ahora()));
            ps.setInt(5, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error resolviendo solicitud", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public Solicitud porId(int id) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(BASE + "WHERE s.id=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            return rs.next() ? mapear(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando solicitud", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public List<Solicitud> listar(String tipo, String estado, Integer proyectoId, int pagina) {
        List<Solicitud> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE).append("WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (tipo != null && !tipo.isEmpty()) {
            sql.append(" AND s.tipo=?");
            params.add(tipo);
        }
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND s.estado=?");
            params.add(estado);
        }
        if (proyectoId != null) {
            sql.append(" AND s.proyecto_id=?");
            params.add(proyectoId);
        }
        sql.append(" ORDER BY s.fecha DESC LIMIT ? OFFSET ?");
        params.add(TAMANO_PAGINA);
        params.add((pagina - 1) * TAMANO_PAGINA);
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
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando solicitudes", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    public int contar(String tipo, String estado, Integer proyectoId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM solicitudes s WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (tipo != null && !tipo.isEmpty()) {
            sql.append(" AND s.tipo=?");
            params.add(tipo);
        }
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND s.estado=?");
            params.add(estado);
        }
        if (proyectoId != null) {
            sql.append(" AND s.proyecto_id=?");
            params.add(proyectoId);
        }
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
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando solicitudes", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public int contarPendientes() {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement("SELECT COUNT(*) FROM solicitudes WHERE estado='PENDIENTE'");
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando solicitudes pendientes", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }
}
