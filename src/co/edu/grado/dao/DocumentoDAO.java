package co.edu.grado.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.grado.modelo.Documento;
import co.edu.grado.util.Conexion;
import co.edu.grado.util.FechaUtil;

/** Documentos (URL) con historial de versiones. (RF28 - RF47) */
public class DocumentoDAO {

    private Documento mapear(ResultSet rs) throws Exception {
        Documento d = new Documento();
        d.setId(rs.getInt("id"));
        d.setProyectoId(rs.getInt("proyecto_id"));
        d.setFase(rs.getString("fase"));
        d.setTipo(rs.getString("tipo"));
        d.setUrl(rs.getString("url"));
        d.setVersion(rs.getInt("version"));
        d.setAutorId(rs.getInt("autor_id"));
        d.setEstado(rs.getString("estado"));
        d.setObservaciones(rs.getString("observaciones"));
        d.setVistoBuenoDirector(rs.getBoolean("visto_bueno_director"));
        d.setFecha(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha")));
        try {
            d.setNombreAutor(rs.getString("nombre_autor"));
            d.setCodigoProyecto(rs.getString("codigo_proyecto"));
        } catch (Exception ignored) {
            // columnas de apoyo opcionales
        }
        return d;
    }

    private static final String BASE =
            "SELECT d.*, u.nombre AS nombre_autor, p.codigo AS codigo_proyecto "
            + "FROM documentos d JOIN usuarios u ON u.id=d.autor_id "
            + "JOIN proyectos p ON p.id=d.proyecto_id ";

    /** RF45: cada envio incrementa la version del documento del mismo tipo. */
    public int registrar(Documento d) {
        int version = versionSiguiente(d.getProyectoId(), d.getTipo());
        String sql = "INSERT INTO documentos (proyecto_id, fase, tipo, url, version, autor_id, estado, "
                + "visto_bueno_director) VALUES (?,?,?,?,?,?,'PENDIENTE',?)";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, d.getProyectoId());
            ps.setString(2, d.getFase());
            ps.setString(3, d.getTipo());
            ps.setString(4, d.getUrl());
            ps.setInt(5, version);
            ps.setInt(6, d.getAutorId());
            ps.setBoolean(7, d.isVistoBuenoDirector());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error registrando documento", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public int versionSiguiente(int proyectoId, String tipo) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement("SELECT COALESCE(MAX(version),0)+1 FROM documentos "
                    + "WHERE proyecto_id=? AND tipo=?");
            ps.setInt(1, proyectoId);
            ps.setString(2, tipo);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 1;
        } catch (Exception e) {
            throw new RuntimeException("Error calculando version", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public List<Documento> listarPorProyecto(int proyectoId) {
        List<Documento> lista = new ArrayList<>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(BASE + "WHERE d.proyecto_id=? ORDER BY d.fecha DESC");
            ps.setInt(1, proyectoId);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando documentos", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    public List<Documento> listarPorProyectoFase(int proyectoId, String fase) {
        List<Documento> lista = new ArrayList<>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(BASE + "WHERE d.proyecto_id=? AND d.fase=? ORDER BY d.fecha DESC");
            ps.setInt(1, proyectoId);
            ps.setString(2, fase);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando documentos de la fase", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    public Documento porId(int id) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(BASE + "WHERE d.id=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            return rs.next() ? mapear(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando documento", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    /** RF47: ultimo documento aprobado de un tipo. */
    public Documento ultimoAprobado(int proyectoId, String tipo) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(BASE + "WHERE d.proyecto_id=? AND d.tipo=? AND d.estado='APROBADO' "
                    + "ORDER BY d.version DESC LIMIT 1");
            ps.setInt(1, proyectoId);
            ps.setString(2, tipo);
            rs = ps.executeQuery();
            return rs.next() ? mapear(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando documento aprobado", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public Documento ultimo(int proyectoId, String tipo) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(BASE + "WHERE d.proyecto_id=? AND d.tipo=? ORDER BY d.version DESC LIMIT 1");
            ps.setInt(1, proyectoId);
            ps.setString(2, tipo);
            rs = ps.executeQuery();
            return rs.next() ? mapear(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando documento", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public void actualizarEstado(int id, String estado, String observaciones) {
        String sql = "UPDATE documentos SET estado=?, observaciones=? WHERE id=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setString(2, observaciones);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando documento", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    /** RF38: el director da el visto bueno al informe final. */
    public void darVistoBueno(int id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement("UPDATE documentos SET visto_bueno_director = TRUE, estado='APROBADO' WHERE id=?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error registrando visto bueno", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }
}
