package co.edu.grado.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.grado.modelo.Idea;
import co.edu.grado.util.Conexion;
import co.edu.grado.util.FechaUtil;

/** Acceso a datos del banco de ideas. (RF10 - RF19) */
public class IdeaDAO {

    public static final int TAMANO_PAGINA = 20;

    private static final String BASE =
            "SELECT i.*, pd.nombre AS nombre_proponente, pe.nombre AS nombre_estudiante, "
            + "pr.nombre AS nombre_revisor "
            + "FROM ideas i "
            + "LEFT JOIN usuarios pd ON pd.id = i.proponente_id "
            + "LEFT JOIN usuarios pe ON pe.id = i.estudiante_id "
            + "LEFT JOIN usuarios pr ON pr.id = i.revisada_por ";

    private Idea mapear(ResultSet rs) throws Exception {
        Idea i = new Idea();
        i.setId(rs.getInt("id"));
        i.setCodigo(rs.getString("codigo"));
        i.setTitulo(rs.getString("titulo"));
        i.setDescripcion(rs.getString("descripcion"));
        i.setNivel(rs.getString("nivel"));
        i.setLineaInvestigacion(rs.getString("linea_investigacion"));
        i.setModalidad(rs.getString("modalidad"));
        int prop = rs.getInt("proponente_id");
        i.setProponenteId(rs.wasNull() ? null : prop);
        i.setOrigen(rs.getString("origen"));
        int est = rs.getInt("estudiante_id");
        i.setEstudianteId(rs.wasNull() ? null : est);
        i.setEstado(rs.getString("estado"));
        i.setObservaciones(rs.getString("observaciones"));
        i.setDisponible(rs.getBoolean("disponible"));
        i.setFechaCreacion(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha_creacion")));
        i.setFechaRevision(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha_revision")));
        int rev = rs.getInt("revisada_por");
        i.setRevisadaPor(rs.wasNull() ? null : rev);
        i.setNombreProponente(rs.getString("nombre_proponente"));
        i.setNombreEstudiante(rs.getString("nombre_estudiante"));
        i.setNombreRevisor(rs.getString("nombre_revisor"));
        return i;
    }

    /** Genera un código único IDEA-AAAA-MM-NNN basado en el consecutivo del mes. */
    private String generarCodigo() {
        String ahora = java.time.LocalDate.now().toString(); // yyyy-MM-dd
        String anioMes = ahora.substring(0, 4) + ahora.substring(5, 7);
        String sql = "SELECT COUNT(*) FROM ideas WHERE codigo LIKE ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int consecutivo = 1;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, "IDEA-" + anioMes + "-%");
            rs = ps.executeQuery();
            if (rs.next()) {
                consecutivo = rs.getInt(1) + 1;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generando codigo de idea", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return String.format("IDEA-%s-%03d", anioMes, consecutivo);
    }

    public int crear(Idea i) {
        String codigo = generarCodigo();
        String sql = "INSERT INTO ideas (codigo, titulo, descripcion, nivel, linea_investigacion, modalidad, "
                + "proponente_id, origen, estudiante_id, estado, disponible) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, codigo);
            ps.setString(2, i.getTitulo());
            ps.setString(3, i.getDescripcion());
            ps.setString(4, i.getNivel());
            ps.setString(5, i.getLineaInvestigacion());
            ps.setString(6, i.getModalidad());
            if (i.getProponenteId() == null) {
                ps.setNull(7, java.sql.Types.INTEGER);
            } else {
                ps.setInt(7, i.getProponenteId());
            }
            ps.setString(8, i.getOrigen());
            if (i.getEstudianteId() == null) {
                ps.setNull(9, java.sql.Types.INTEGER);
            } else {
                ps.setInt(9, i.getEstudianteId());
            }
            ps.setString(10, i.getEstado());
            ps.setBoolean(11, i.isDisponible());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error creando idea", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public void actualizar(Idea i) {
        String sql = "UPDATE ideas SET titulo=?, descripcion=?, nivel=?, linea_investigacion=?, modalidad=? "
                + "WHERE id=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, i.getTitulo());
            ps.setString(2, i.getDescripcion());
            ps.setString(3, i.getNivel());
            ps.setString(4, i.getLineaInvestigacion());
            ps.setString(5, i.getModalidad());
            ps.setInt(6, i.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando idea", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM ideas WHERE id = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando idea", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public Idea porId(int id) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(BASE + "WHERE i.id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            return rs.next() ? mapear(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando idea", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    /**
     * Listado con filtros combinables. Usado por el banco de ideas, la bandeja
     * del comite y las propuestas de estudiantes. (RF13, RF14, RF18, RF19)
     */
    public List<Idea> listar(String estado, String nivel, String origen, boolean soloDisponibles,
                             Integer proponenteId, Integer estudianteId, String busqueda, int pagina) {
        List<Idea> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE).append("WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND i.estado = ?");
            params.add(estado);
        }
        if (nivel != null && !nivel.isEmpty()) {
            sql.append(" AND i.nivel = ?");
            params.add(nivel);
        }
        if (origen != null && !origen.isEmpty()) {
            sql.append(" AND i.origen = ?");
            params.add(origen);
        }
        if (soloDisponibles) {
            sql.append(" AND i.disponible = TRUE AND i.estado = 'APROBADA'");
        }
        if (proponenteId != null) {
            sql.append(" AND i.proponente_id = ?");
            params.add(proponenteId);
        }
        if (estudianteId != null) {
            sql.append(" AND i.estudiante_id = ?");
            params.add(estudianteId);
        }
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append(" AND (i.titulo ILIKE ? OR i.linea_investigacion ILIKE ? OR i.descripcion ILIKE ? OR i.codigo ILIKE ?)");
            String like = "%" + busqueda.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }
        sql.append(" ORDER BY i.fecha_creacion DESC LIMIT ? OFFSET ?");
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
            throw new RuntimeException("Error listando ideas", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    public int contar(String estado, String nivel, String origen, boolean soloDisponibles,
                      Integer proponenteId, Integer estudianteId, String busqueda) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM ideas i WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND i.estado = ?");
            params.add(estado);
        }
        if (nivel != null && !nivel.isEmpty()) {
            sql.append(" AND i.nivel = ?");
            params.add(nivel);
        }
        if (origen != null && !origen.isEmpty()) {
            sql.append(" AND i.origen = ?");
            params.add(origen);
        }
        if (soloDisponibles) {
            sql.append(" AND i.disponible = TRUE AND i.estado = 'APROBADA'");
        }
        if (proponenteId != null) {
            sql.append(" AND i.proponente_id = ?");
            params.add(proponenteId);
        }
        if (estudianteId != null) {
            sql.append(" AND i.estudiante_id = ?");
            params.add(estudianteId);
        }
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append(" AND (i.titulo ILIKE ? OR i.linea_investigacion ILIKE ? OR i.descripcion ILIKE ? OR i.codigo ILIKE ?)");
            String like = "%" + busqueda.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
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
            throw new RuntimeException("Error contando ideas", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    /** RF11, RF12: decide (aprobar/rechazar) una idea registrando observaciones. */
    public void decidir(int ideaId, String estado, String observaciones, int revisorId) {
        String sql = "UPDATE ideas SET estado = ?, observaciones = ?, revisada_por = ?, fecha_revision = ? "
                + "WHERE id = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setString(2, observaciones);
            ps.setInt(3, revisorId);
            ps.setTimestamp(4, FechaUtil.sql(FechaUtil.ahora()));
            ps.setInt(5, ideaId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error decidiendo idea", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public void marcarTomada(int ideaId) {
        String sql = "UPDATE ideas SET disponible = FALSE WHERE id = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, ideaId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error marcando idea como tomada", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public int contarPendientes() {
        String sql = "SELECT COUNT(*) FROM ideas WHERE estado = 'PENDIENTE'";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando ideas pendientes", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }
}
