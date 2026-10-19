package co.edu.grado.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.grado.modelo.AsignacionDocente;
import co.edu.grado.modelo.FaseProyecto;
import co.edu.grado.modelo.Integrante;
import co.edu.grado.modelo.Proyecto;
import co.edu.grado.modelo.Seguimiento;
import co.edu.grado.modelo.Sustentacion;
import co.edu.grado.util.Conexion;
import co.edu.grado.util.FechaUtil;

/**
 * Acceso a datos de proyectos: proyecto, integrantes, asignaciones de docentes,
 * fases, seguimientos y sustentacion. (RF20 - RF54)
 */
public class ProyectoDAO {

    public static final int TAMANO_PAGINA = 20;

    private static final String BASE =
            "SELECT p.*, per.nombre AS nombre_periodo, per.dias_min_informe AS dias_min_informe, "
            + "(SELECT COUNT(*) FROM proyecto_integrantes pi WHERE pi.proyecto_id=p.id AND pi.estado='ACTIVO') AS num_integrantes, "
            + "(SELECT string_agg(u.nombre, ', ') FROM proyecto_integrantes pi "
            + "   JOIN usuarios u ON u.id=pi.estudiante_id "
            + "   WHERE pi.proyecto_id=p.id AND pi.estado='ACTIVO') AS nombres_integrantes, "
            + "(SELECT u.nombre FROM asignaciones_docente a JOIN usuarios u ON u.id=a.docente_id "
            + "   WHERE a.proyecto_id=p.id AND a.rol='DIRECTOR' AND a.activo = TRUE ORDER BY a.id DESC LIMIT 1) AS nombre_director, "
            + "(SELECT u.nombre FROM asignaciones_docente a JOIN usuarios u ON u.id=a.docente_id "
            + "   WHERE a.proyecto_id=p.id AND a.rol='EVALUADOR' AND a.activo = TRUE ORDER BY a.id DESC LIMIT 1) AS nombre_evaluador "
            + "FROM proyectos p JOIN periodos_academicos per ON per.id=p.periodo_id ";

    private Proyecto mapear(ResultSet rs) throws Exception {
        Proyecto p = new Proyecto();
        p.setId(rs.getInt("id"));
        p.setCodigo(rs.getString("codigo"));
        p.setTitulo(rs.getString("titulo"));
        p.setObjetivos(rs.getString("objetivos"));
        p.setModalidad(rs.getString("modalidad"));
        p.setNivel(rs.getString("nivel"));
        p.setLineaInvestigacion(rs.getString("linea_investigacion"));
        p.setPeriodoId(rs.getInt("periodo_id"));
        int idea = rs.getInt("idea_id");
        p.setIdeaId(rs.wasNull() ? null : idea);
        p.setEstado(rs.getString("estado"));
        p.setFaseActual(rs.getString("fase_actual"));
        p.setResponsableActual(rs.getString("responsable_actual"));
        p.setFechaAprobacionPropuesta(FechaUtil.aLocalDate(rs.getDate("fecha_aprobacion_propuesta")));
        p.setFechaInicio180(FechaUtil.aLocalDate(rs.getDate("fecha_inicio_180")));
        p.setFechaVencimiento(FechaUtil.aLocalDate(rs.getDate("fecha_vencimiento")));
        p.setProrrogasUtilizadas(rs.getInt("prorrogas_utilizadas"));
        double cal = rs.getDouble("calificacion_final");
        p.setCalificacionFinal(rs.wasNull() ? null : cal);
        p.setConceptoEvaluador(rs.getString("concepto_evaluador"));
        p.setConceptoComite(rs.getString("concepto_comite"));
        p.setFechaCreacion(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha_creacion")));
        p.setNombrePeriodo(rs.getString("nombre_periodo"));
        p.setIntegrantes(rs.getInt("num_integrantes"));
        p.setNombresIntegrantes(rs.getString("nombres_integrantes"));
        p.setNombreDirector(rs.getString("nombre_director"));
        p.setNombreEvaluador(rs.getString("nombre_evaluador"));

        p.setVencido(p.calcularVencido());
        if (p.getFechaVencimiento() != null) {
            p.setDiasRestantes(FechaUtil.diasRestantes(p.getFechaVencimiento()));
        }
        int diasMin = rs.getInt("dias_min_informe");
        if (p.getFechaAprobacionPropuesta() != null && diasMin > 0) {
            long transcurridos = FechaUtil.diasTranscurridos(p.getFechaAprobacionPropuesta());
            p.setInformeHabilitado(transcurridos >= diasMin);
            if (!p.isInformeHabilitado()) {
                long faltan = diasMin - transcurridos;
                p.setMotivoBloqueoInforme("Faltan " + faltan + " dias (minimo " + diasMin
                        + " dias desde la aprobacion de la propuesta)");
            }
        }
        return p;
    }

    // ------------------------------------------------------------------
    // PROYECTO
    // ------------------------------------------------------------------

    /** RF22: genera el codigo unico nivel + periodo + consecutivo. */
    public String generarCodigo(String nivel, String nombrePeriodo) {
        String prefijo = "INGENIERIA".equals(nivel) ? "ING" : "TEC";
        String per = nombrePeriodo == null ? "" : nombrePeriodo.replace("-", "").replace("/", "");
        String sql = "SELECT COUNT(*) FROM proyectos p JOIN periodos_academicos pe ON pe.id=p.periodo_id "
                + "WHERE p.nivel = ? AND pe.nombre = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int consecutivo = 1;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, nivel);
            ps.setString(2, nombrePeriodo);
            rs = ps.executeQuery();
            if (rs.next()) {
                consecutivo = rs.getInt(1) + 1;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generando codigo", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return String.format("%s-%s-%03d", prefijo, per, consecutivo);
    }

    public int crear(Proyecto p) {
        String sql = "INSERT INTO proyectos (codigo, titulo, objetivos, modalidad, nivel, linea_investigacion, "
                + "periodo_id, idea_id, estado, fase_actual, responsable_actual) "
                + "VALUES (?,?,?,?,?,?,?,?, 'EN_FLUJO','PAGO','ESTUDIANTE')";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getTitulo());
            ps.setString(3, p.getObjetivos());
            ps.setString(4, p.getModalidad());
            ps.setString(5, p.getNivel());
            ps.setString(6, p.getLineaInvestigacion());
            ps.setInt(7, p.getPeriodoId());
            if (p.getIdeaId() == null) {
                ps.setNull(8, java.sql.Types.INTEGER);
            } else {
                ps.setInt(8, p.getIdeaId());
            }
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            int id = rs.next() ? rs.getInt(1) : 0;
            if (id > 0) {
                crearFasesIniciales(id);
            }
            return id;
        } catch (Exception e) {
            throw new RuntimeException("Error creando proyecto", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public Proyecto porId(int id) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(BASE + "WHERE p.id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Proyecto p = mapear(rs);
                p.getFases().addAll(fases(id));
                p.getListaIntegrantes().addAll(integrantes(id));
                return p;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando proyecto", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public Proyecto porCodigo(String codigo) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(BASE + "WHERE p.codigo = ?");
            ps.setString(1, codigo);
            rs = ps.executeQuery();
            return rs.next() ? mapear(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando proyecto por codigo", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    /** Proyectos en flujo donde el COMITE es el responsable actual. */
    public List<Proyecto> listarPendientesComite() {
        String sql = BASE + "WHERE p.estado='EN_FLUJO' AND p.responsable_actual='COMITE' ORDER BY p.fase_actual, p.codigo";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Proyecto> lista = new ArrayList<>();
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando pendientes del comite", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    /** RF61, RF62, RF64, RF72: buscador unico con filtros y paginacion. */
    public List<Proyecto> listar(String busqueda, String fase, String modalidad, String nivel,
                                 String estado, Integer periodoId, Integer estudianteId,
                                 Integer directorId, Integer evaluadorId, int pagina) {
        List<Proyecto> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE).append("WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append(" AND (p.titulo ILIKE ? OR p.codigo ILIKE ? "
                    + "OR EXISTS (SELECT 1 FROM proyecto_integrantes pi JOIN usuarios u ON u.id=pi.estudiante_id "
                    + "           WHERE pi.proyecto_id=p.id AND u.nombre ILIKE ?) "
                    + "OR EXISTS (SELECT 1 FROM asignaciones_docente a JOIN usuarios u ON u.id=a.docente_id "
                    + "           WHERE a.proyecto_id=p.id AND a.activo = TRUE AND u.nombre ILIKE ?))");
            String like = "%" + busqueda.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (fase != null && !fase.isEmpty()) {
            sql.append(" AND p.fase_actual = ?");
            params.add(fase);
        }
        if (modalidad != null && !modalidad.isEmpty()) {
            sql.append(" AND p.modalidad = ?");
            params.add(modalidad);
        }
        if (nivel != null && !nivel.isEmpty()) {
            sql.append(" AND p.nivel = ?");
            params.add(nivel);
        }
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND p.estado = ?");
            params.add(estado);
        }
        if (periodoId != null) {
            sql.append(" AND p.periodo_id = ?");
            params.add(periodoId);
        }
        if (estudianteId != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM proyecto_integrantes pi "
                    + "WHERE pi.proyecto_id=p.id AND pi.estudiante_id = ?)");
            params.add(estudianteId);
        }
        if (directorId != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM asignaciones_docente a WHERE a.proyecto_id=p.id "
                    + "AND a.rol='DIRECTOR' AND a.activo = TRUE AND a.docente_id = ?)");
            params.add(directorId);
        }
        if (evaluadorId != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM asignaciones_docente a WHERE a.proyecto_id=p.id "
                    + "AND a.rol='EVALUADOR' AND a.activo = TRUE AND a.docente_id = ?)");
            params.add(evaluadorId);
        }
        sql.append(" ORDER BY p.fecha_creacion DESC LIMIT ? OFFSET ?");
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
            throw new RuntimeException("Error listando proyectos", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    public int contar(String busqueda, String fase, String modalidad, String nivel,
                      String estado, Integer periodoId, Integer estudianteId,
                      Integer directorId, Integer evaluadorId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM proyectos p WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append(" AND (p.titulo ILIKE ? OR p.codigo ILIKE ? "
                    + "OR EXISTS (SELECT 1 FROM proyecto_integrantes pi JOIN usuarios u ON u.id=pi.estudiante_id "
                    + "           WHERE pi.proyecto_id=p.id AND u.nombre ILIKE ?) "
                    + "OR EXISTS (SELECT 1 FROM asignaciones_docente a JOIN usuarios u ON u.id=a.docente_id "
                    + "           WHERE a.proyecto_id=p.id AND a.activo = TRUE AND u.nombre ILIKE ?))");
            String like = "%" + busqueda.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (fase != null && !fase.isEmpty()) {
            sql.append(" AND p.fase_actual = ?");
            params.add(fase);
        }
        if (modalidad != null && !modalidad.isEmpty()) {
            sql.append(" AND p.modalidad = ?");
            params.add(modalidad);
        }
        if (nivel != null && !nivel.isEmpty()) {
            sql.append(" AND p.nivel = ?");
            params.add(nivel);
        }
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND p.estado = ?");
            params.add(estado);
        }
        if (periodoId != null) {
            sql.append(" AND p.periodo_id = ?");
            params.add(periodoId);
        }
        if (estudianteId != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM proyecto_integrantes pi "
                    + "WHERE pi.proyecto_id=p.id AND pi.estudiante_id = ?)");
            params.add(estudianteId);
        }
        if (directorId != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM asignaciones_docente a WHERE a.proyecto_id=p.id "
                    + "AND a.rol='DIRECTOR' AND a.activo = TRUE AND a.docente_id = ?)");
            params.add(directorId);
        }
        if (evaluadorId != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM asignaciones_docente a WHERE a.proyecto_id=p.id "
                    + "AND a.rol='EVALUADOR' AND a.activo = TRUE AND a.docente_id = ?)");
            params.add(evaluadorId);
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
            throw new RuntimeException("Error contando proyectos", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public void actualizarEstado(int id, String estado) {
        ejecutar("UPDATE proyectos SET estado=? WHERE id=?", estado, id);
    }

    public void actualizarFase(int id, String fase, String responsable) {
        String sql = "UPDATE proyectos SET fase_actual=?, responsable_actual=? WHERE id=?";
        String sqlFase = "UPDATE fases_proyecto SET responsable=? WHERE proyecto_id=? AND fase=?";
        Connection cn = null;
        PreparedStatement ps = null;
        PreparedStatement psFase = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, fase);
            ps.setString(2, responsable);
            ps.setInt(3, id);
            ps.executeUpdate();
            psFase = cn.prepareStatement(sqlFase);
            psFase.setString(1, responsable);
            psFase.setInt(2, id);
            psFase.setString(3, fase);
            psFase.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando fase", e);
        } finally {
            Conexion.cerrar(null, psFase, cn);
        }
    }

    /** RF36: al aprobar la propuesta inicia el conteo de 180 dias. */
    public void aprobarPropuesta(int id, int diasDuracion) {
        java.time.LocalDate hoy = FechaUtil.hoy();
        java.time.LocalDate venc = FechaUtil.sumarDias(hoy, diasDuracion);
        String sql = "UPDATE proyectos SET fecha_aprobacion_propuesta=?, fecha_inicio_180=?, "
                + "fecha_vencimiento=?, fase_actual='INFORME_I', responsable_actual='ESTUDIANTE', "
                + "concepto_comite='APROBADO' WHERE id=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setDate(1, FechaUtil.sql(hoy));
            ps.setDate(2, FechaUtil.sql(hoy));
            ps.setDate(3, FechaUtil.sql(venc));
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error aprobando propuesta", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public void registrarConceptoEvaluador(int id, String concepto) {
        ejecutarString("UPDATE proyectos SET concepto_evaluador=? WHERE id=?", concepto, id);
    }

    public void rechazarPropuesta(int id, String conceptoComite) {
        ejecutarString("UPDATE proyectos SET concepto_comite=?, fase_actual='PROPUESTA_I', "
                + "responsable_actual='ESTUDIANTE' WHERE id=?", conceptoComite, id);
    }

    /** RF49: habilita el proyecto por otros 180 dias. */
    public void aplicarProrroga(int id) {
        String sql = "UPDATE proyectos SET prorrogas_utilizadas = prorrogas_utilizadas + 1, "
                + "fecha_vencimiento = COALESCE(fecha_vencimiento, CURRENT_DATE) + INTERVAL '180 days' "
                + "WHERE id = ?";
        ejecutar(sql, id);
    }

    public void finalizar(int id, Double calificacion) {
        String sql = "UPDATE proyectos SET estado='FINALIZADO', fase_actual='CERRADO', "
                + "responsable_actual='COMITE', calificacion_final=? WHERE id=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            if (calificacion == null) {
                ps.setNull(1, java.sql.Types.DECIMAL);
            } else {
                ps.setDouble(1, calificacion);
            }
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error finalizando proyecto", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    /** RF46, RF50: marca vencidos los proyectos y cancela los que agotaron prorrogas. */
    public int marcarVencidos() {
        String sqlCancelados = "UPDATE proyectos SET estado='CANCELADO' FROM periodos_academicos pe "
                + "WHERE pe.id = proyectos.periodo_id AND proyectos.estado='EN_FLUJO' "
                + "AND proyectos.fecha_vencimiento IS NOT NULL AND proyectos.fecha_vencimiento < CURRENT_DATE "
                + "AND proyectos.prorrogas_utilizadas >= pe.max_prorrogas";
        String sqlVencidos = "UPDATE proyectos SET estado='VENCIDO' FROM periodos_academicos pe "
                + "WHERE pe.id = proyectos.periodo_id AND proyectos.estado='EN_FLUJO' "
                + "AND proyectos.fecha_vencimiento IS NOT NULL AND proyectos.fecha_vencimiento < CURRENT_DATE "
                + "AND proyectos.prorrogas_utilizadas < pe.max_prorrogas";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sqlCancelados);
            int a = ps.executeUpdate();
            ps.close();
            ps = cn.prepareStatement(sqlVencidos);
            int b = ps.executeUpdate();
            return a + b;
        } catch (Exception e) {
            return 0;
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    // ------------------------------------------------------------------
    // INTEGRANTES
    // ------------------------------------------------------------------

    public void agregarIntegrante(int proyectoId, int estudianteId) {
        String sql = "INSERT INTO proyecto_integrantes (proyecto_id, estudiante_id) VALUES (?,?)";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, proyectoId);
            ps.setInt(2, estudianteId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error agregando integrante", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public List<Integrante> integrantes(int proyectoId) {
        List<Integrante> lista = new ArrayList<>();
        String sql = "SELECT pi.*, u.nombre, u.cedula, u.email, u.nivel FROM proyecto_integrantes pi "
                + "JOIN usuarios u ON u.id=pi.estudiante_id WHERE pi.proyecto_id=? ORDER BY pi.fecha_union";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, proyectoId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Integrante i = new Integrante();
                i.setId(rs.getInt("id"));
                i.setProyectoId(rs.getInt("proyecto_id"));
                i.setEstudianteId(rs.getInt("estudiante_id"));
                i.setEstado(rs.getString("estado"));
                i.setFechaUnion(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha_union")));
                i.setFechaRetiro(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha_retiro")));
                i.setNombre(rs.getString("nombre"));
                i.setCedula(rs.getString("cedula"));
                i.setEmail(rs.getString("email"));
                i.setNivel(rs.getString("nivel"));
                lista.add(i);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando integrantes", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    public int contarIntegrantesActivos(int proyectoId) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement("SELECT COUNT(*) FROM proyecto_integrantes "
                    + "WHERE proyecto_id=? AND estado='ACTIVO'");
            ps.setInt(1, proyectoId);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando integrantes", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    /** RF53: retiro de un integrante. */
    public void retirarIntegrante(int proyectoId, int estudianteId) {
        String sql = "UPDATE proyecto_integrantes SET estado='RETIRADO', fecha_retiro=? "
                + "WHERE proyecto_id=? AND estudiante_id=? AND estado='ACTIVO'";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setTimestamp(1, FechaUtil.sql(FechaUtil.ahora()));
            ps.setInt(2, proyectoId);
            ps.setInt(3, estudianteId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error retirando integrante", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    /** RF17: verifica si un estudiante ya tiene un proyecto activo. */
    public boolean estudianteTieneProyectoActivo(int estudianteId) {
        String sql = "SELECT COUNT(*) FROM proyecto_integrantes pi JOIN proyectos p ON p.id=pi.proyecto_id "
                + "WHERE pi.estudiante_id=? AND pi.estado='ACTIVO' "
                + "AND p.estado IN ('EN_FLUJO','FINALIZADO')";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, estudianteId);
            rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error verificando proyecto del estudiante", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    /** RF17: proyecto activo del estudiante (para mostrarlo en su panel). */
    public Proyecto proyectoActivoDeEstudiante(int estudianteId) {
        String sql = BASE + "WHERE EXISTS (SELECT 1 FROM proyecto_integrantes pi WHERE pi.proyecto_id=p.id "
                + "AND pi.estudiante_id=? AND pi.estado='ACTIVO') "
                + "AND p.estado IN ('EN_FLUJO','FINALIZADO') ORDER BY p.fecha_creacion DESC LIMIT 1";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, estudianteId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Proyecto p = mapear(rs);
                p.getFases().addAll(fases(p.getId()));
                p.getListaIntegrantes().addAll(integrantes(p.getId()));
                return p;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando proyecto del estudiante", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    /** RF50: valida que el estudiante no repita una modalidad cancelada por prorrogas. */
    public boolean modalidadCancelada(int estudianteId, String modalidad) {
        String sql = "SELECT COUNT(*) FROM proyectos p JOIN proyecto_integrantes pi ON pi.proyecto_id=p.id "
                + "WHERE pi.estudiante_id=? AND p.modalidad=? AND p.estado='CANCELADO'";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, estudianteId);
            ps.setString(2, modalidad);
            rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error validando modalidad", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    // ------------------------------------------------------------------
    // ASIGNACIONES DE DOCENTES
    // ------------------------------------------------------------------

    public void asignarDocente(int proyectoId, int docenteId, String rol) {
        desactivarAsignacion(proyectoId, rol);
        String sql = "INSERT INTO asignaciones_docente (proyecto_id, docente_id, rol, activo) VALUES (?,?,?,TRUE)";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, proyectoId);
            ps.setInt(2, docenteId);
            ps.setString(3, rol);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error asignando docente", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public void desactivarAsignacion(int proyectoId, String rol) {
        String sql = "UPDATE asignaciones_docente SET activo = FALSE WHERE proyecto_id=? AND rol=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, proyectoId);
            ps.setString(2, rol);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error desactivando asignacion", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public AsignacionDocente asignacionActiva(int proyectoId, String rol) {
        String sql = "SELECT a.*, u.nombre AS nombre_docente, u.email AS email_docente "
                + "FROM asignaciones_docente a JOIN usuarios u ON u.id=a.docente_id "
                + "WHERE a.proyecto_id=? AND a.rol=? AND a.activo = TRUE ORDER BY a.id DESC LIMIT 1";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, proyectoId);
            ps.setString(2, rol);
            rs = ps.executeQuery();
            if (rs.next()) {
                AsignacionDocente a = new AsignacionDocente();
                a.setId(rs.getInt("id"));
                a.setProyectoId(rs.getInt("proyecto_id"));
                a.setDocenteId(rs.getInt("docente_id"));
                a.setRol(rs.getString("rol"));
                a.setActivo(rs.getBoolean("activo"));
                a.setFechaAsignacion(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha_asignacion")));
                a.setNombreDocente(rs.getString("nombre_docente"));
                a.setEmailDocente(rs.getString("email_docente"));
                return a;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando asignacion", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    /** RF24, RF69: proyectos abiertos de un docente segun su rol. */
    public int contarProyectosDeDocente(int docenteId, String rol) {
        String sql = "SELECT COUNT(*) FROM asignaciones_docente a JOIN proyectos p ON p.id=a.proyecto_id "
                + "WHERE a.docente_id=? AND a.rol=? AND a.activo = TRUE AND p.estado='EN_FLUJO'";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, docenteId);
            ps.setString(2, rol);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando proyectos del docente", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    /** RF07: reasigna en bloque todos los proyectos de un docente a otro. */
    public int reasignarEnBloque(int docenteOrigen, int docenteDestino, String rol) {
        String sql = "UPDATE asignaciones_docente SET docente_id=?, fecha_asignacion=? "
                + "WHERE docente_id=? AND rol=? AND activo = TRUE";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, docenteDestino);
            ps.setTimestamp(2, FechaUtil.sql(FechaUtil.ahora()));
            ps.setInt(3, docenteOrigen);
            ps.setString(4, rol);
            return ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error reasignando en bloque", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    /** Proyectos de un docente en un rol (para vistas del docente, RF64).
     *  Si rol es null o "DOCENTE" se listan tanto los proyectos como director
     *  como evaluador, ya que un docente puede desempenar ambos roles. */
    public List<Proyecto> listarPorDocente(int docenteId, String rol, String estado, int pagina) {
        List<Proyecto> lista = new ArrayList<>();
        boolean ambos = rol == null || rol.isEmpty() || "DOCENTE".equals(rol);
        StringBuilder sql = new StringBuilder(BASE)
                .append("WHERE EXISTS (SELECT 1 FROM asignaciones_docente a WHERE a.proyecto_id=p.id ")
                .append("AND a.activo = TRUE AND a.docente_id=?");
        List<Object> params = new ArrayList<>();
        params.add(docenteId);
        if (ambos) {
            sql.append(" AND a.rol IN ('DIRECTOR','EVALUADOR')");
        } else {
            sql.append(" AND a.rol=?");
            params.add(rol);
        }
        sql.append(")");
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND p.estado=?");
            params.add(estado);
        }
        sql.append(" ORDER BY p.fecha_creacion DESC LIMIT ? OFFSET ?");
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
            throw new RuntimeException("Error listando proyectos del docente", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    public int contarPorDocente(int docenteId, String rol, String estado) {
        boolean ambos = rol == null || rol.isEmpty() || "DOCENTE".equals(rol);
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM proyectos p "
                + "JOIN asignaciones_docente a ON a.proyecto_id=p.id AND a.activo = TRUE "
                + "WHERE a.docente_id=?");
        List<Object> params = new ArrayList<>();
        params.add(docenteId);
        if (ambos) {
            sql.append(" AND a.rol IN ('DIRECTOR','EVALUADOR')");
        } else {
            sql.append(" AND a.rol=?");
            params.add(rol);
        }
        if (estado != null && !estado.isEmpty()) {
            sql.append(" AND p.estado=?");
            params.add(estado);
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
            throw new RuntimeException("Error contando proyectos del docente", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    // ------------------------------------------------------------------
    // FASES
    // ------------------------------------------------------------------

    private void crearFasesIniciales(int proyectoId) {
        String sql = "INSERT INTO fases_proyecto (proyecto_id, fase, estado, responsable) VALUES "
                  + "(?,'PAGO','PENDIENTE','ESTUDIANTE'),"
                  + "(?,'PROPUESTA_I','PENDIENTE','ESTUDIANTE'),"
                  + "(?,'PROPUESTA_II','PENDIENTE','COMITE'),"
                  + "(?,'INFORME_I','PENDIENTE','ESTUDIANTE'),"
                  + "(?,'INFORME_II','PENDIENTE','EVALUADOR'),"
                  + "(?,'SUSTENTACION','PENDIENTE','COMITE'),"
                  + "(?,'CERRADO','PENDIENTE','COMITE')";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            for (int i = 1; i <= 7; i++) {
                ps.setInt(i, proyectoId);
            }
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error creando fases del proyecto", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public List<FaseProyecto> fases(int proyectoId) {
        List<FaseProyecto> lista = new ArrayList<>();
        String sql = "SELECT * FROM fases_proyecto WHERE proyecto_id=? "
                  + "ORDER BY CASE fase WHEN 'PAGO' THEN 1 WHEN 'PROPUESTA_I' THEN 2 WHEN 'PROPUESTA_II' THEN 3 "
                  + "WHEN 'INFORME_I' THEN 4 WHEN 'INFORME_II' THEN 5 WHEN 'SUSTENTACION' THEN 6 WHEN 'CERRADO' THEN 7 ELSE 8 END";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, proyectoId);
            rs = ps.executeQuery();
            while (rs.next()) {
                FaseProyecto f = new FaseProyecto();
                f.setId(rs.getInt("id"));
                f.setProyectoId(rs.getInt("proyecto_id"));
                f.setFase(rs.getString("fase"));
                f.setEstado(rs.getString("estado"));
                f.setResponsable(rs.getString("responsable"));
                f.setFechaAprobacion(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha_aprobacion")));
                lista.add(f);
            }
            return lista;
        } catch (Exception e) {
            throw new RuntimeException("Error listando fases", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public FaseProyecto fase(int proyectoId, String fase) {
        for (FaseProyecto f : fases(proyectoId)) {
            if (f.getFase().equals(fase)) {
                return f;
            }
        }
        return null;
    }

    /** Solo demostracion: cambia la fecha de aprobacion de la propuesta. */
    public void actualizarFechaAprobacionPropuesta(int proyectoId, java.util.Date fecha) {
        String sql = "UPDATE proyectos SET fecha_aprobacion_propuesta=? WHERE id=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(fecha.getTime()));
            ps.setInt(2, proyectoId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando fecha de aprobacion", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public void actualizarEstadoFase(int proyectoId, String fase, String estado) {
        String sql = "UPDATE fases_proyecto SET estado=?, fecha_aprobacion=? WHERE proyecto_id=? AND fase=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setTimestamp(2, "APROBADA".equals(estado) ? FechaUtil.sql(FechaUtil.ahora()) : null);
            ps.setInt(3, proyectoId);
            ps.setString(4, fase);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando estado de la fase", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    // ------------------------------------------------------------------
    // SEGUIMIENTOS (RF42)
    // ------------------------------------------------------------------

    public void crearSeguimiento(Seguimiento s) {
        String sql = "INSERT INTO seguimientos (proyecto_id, director_id, fecha, descripcion, url_evidencia) "
                + "VALUES (?,?,?,?,?)";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, s.getProyectoId());
            ps.setInt(2, s.getDirectorId());
            ps.setDate(3, FechaUtil.sql(s.getFecha()));
            ps.setString(4, s.getDescripcion());
            ps.setString(5, s.getUrlEvidencia());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error creando seguimiento", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public List<Seguimiento> seguimientos(int proyectoId) {
        List<Seguimiento> lista = new ArrayList<>();
        String sql = "SELECT s.*, u.nombre AS nombre_director FROM seguimientos s "
                + "JOIN usuarios u ON u.id=s.director_id WHERE s.proyecto_id=? ORDER BY s.fecha DESC";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, proyectoId);
            rs = ps.executeQuery();
            while (rs.next()) {
                Seguimiento s = new Seguimiento();
                s.setId(rs.getInt("id"));
                s.setProyectoId(rs.getInt("proyecto_id"));
                s.setDirectorId(rs.getInt("director_id"));
                s.setFecha(FechaUtil.aLocalDate(rs.getDate("fecha")));
                s.setDescripcion(rs.getString("descripcion"));
                s.setUrlEvidencia(rs.getString("url_evidencia"));
                s.setNombreDirector(rs.getString("nombre_director"));
                lista.add(s);
            }
            return lista;
        } catch (Exception e) {
            throw new RuntimeException("Error listando seguimientos", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public int contarSeguimientos(int proyectoId) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement("SELECT COUNT(*) FROM seguimientos WHERE proyecto_id=?");
            ps.setInt(1, proyectoId);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando seguimientos", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    // ------------------------------------------------------------------
    // SUSTENTACION (RF43)
    // ------------------------------------------------------------------

    public void guardarSustentacion(Sustentacion s) {
        String sql = "INSERT INTO sustentaciones (proyecto_id, fecha, calificacion, url_acta, registrado_por) "
                + "VALUES (?,?,?,?,?) ON CONFLICT (proyecto_id) DO UPDATE SET fecha=EXCLUDED.fecha, "
                + "calificacion=EXCLUDED.calificacion, url_acta=EXCLUDED.url_acta, registrado_por=EXCLUDED.registrado_por";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, s.getProyectoId());
            ps.setDate(2, FechaUtil.sql(s.getFecha()));
            ps.setDouble(3, s.getCalificacion());
            ps.setString(4, s.getUrlActa());
            ps.setInt(5, s.getRegistradoPor());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error guardando sustentacion", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public Sustentacion sustentacion(int proyectoId) {
        String sql = "SELECT * FROM sustentaciones WHERE proyecto_id=?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, proyectoId);
            rs = ps.executeQuery();
            if (rs.next()) {
                Sustentacion s = new Sustentacion();
                s.setId(rs.getInt("id"));
                s.setProyectoId(rs.getInt("proyecto_id"));
                s.setFecha(FechaUtil.aLocalDate(rs.getDate("fecha")));
                s.setCalificacion(rs.getDouble("calificacion"));
                s.setUrlActa(rs.getString("url_acta"));
                s.setRegistradoPor(rs.getInt("registrado_por"));
                return s;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando sustentacion", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    // ------------------------------------------------------------------
    // UTILIDADES
    // ------------------------------------------------------------------

    private void ejecutar(String sql, Object... params) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error ejecutando sentencia", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    private void ejecutarString(String sql, String valor, int id) {
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, valor);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error ejecutando sentencia", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }
}
