package co.edu.grado.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.edu.grado.util.Conexion;

/**
 * Consultas agregadas para informes y reportes. (RF66 - RF70)
 * Devuelve filas genericas (List<Map<String,Object>>) listas para tablas y exportacion.
 */
public class ReporteDAO {

    private List<Map<String, Object>> consultar(String sql, Object... params) {
        List<Map<String, Object>> filas = new ArrayList<>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            while (rs.next()) {
                Map<String, Object> fila = new LinkedHashMap<>();
                for (int c = 1; c <= cols; c++) {
                    fila.put(meta.getColumnLabel(c), rs.getObject(c));
                }
                filas.add(fila);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return filas;
    }

    /** RF66: proyectos por estudiante. */
    public List<Map<String, Object>> proyectosPorEstudiante(Integer periodoId) {
        String sql = "SELECT u.nombre AS estudiante, u.cedula, u.nivel, p.codigo, p.titulo, "
                + "p.modalidad, p.fase_actual, p.estado "
                + "FROM proyecto_integrantes pi "
                + "JOIN usuarios u ON u.id=pi.estudiante_id "
                + "JOIN proyectos p ON p.id=pi.proyecto_id "
                + (periodoId != null ? "WHERE p.periodo_id=? " : "")
                + "ORDER BY u.nombre";
        return periodoId != null ? consultar(sql, periodoId) : consultar(sql);
    }

    /** RF66, RF69: carga de proyectos por director o evaluador. */
    public List<Map<String, Object>> cargaDocentes(String rol) {
        String sql = "SELECT u.nombre AS docente, u.email, "
                + "SUM(CASE WHEN p.estado='EN_FLUJO' THEN 1 ELSE 0 END) AS en_flujo, "
                + "SUM(CASE WHEN p.estado='FINALIZADO' THEN 1 ELSE 0 END) AS finalizados, "
                + "COUNT(*) AS total "
                + "FROM asignaciones_docente a "
                + "JOIN usuarios u ON u.id=a.docente_id "
                + "JOIN proyectos p ON p.id=a.proyecto_id "
                + "WHERE a.rol=? AND a.activo = TRUE "
                + "GROUP BY u.id, u.nombre, u.email ORDER BY total DESC";
        return consultar(sql, rol);
    }

    /** RF67: proyectos por modalidad. */
    public List<Map<String, Object>> proyectosPorModalidad(Integer periodoId) {
        String sql = "SELECT modalidad, COUNT(*) AS total FROM proyectos p "
                + (periodoId != null ? "WHERE p.periodo_id=? " : "")
                + "GROUP BY modalidad ORDER BY total DESC";
        return periodoId != null ? consultar(sql, periodoId) : consultar(sql);
    }

    /** RF67: proyectos por nivel. */
    public List<Map<String, Object>> proyectosPorNivel(Integer periodoId) {
        String sql = "SELECT nivel, COUNT(*) AS total FROM proyectos p "
                + (periodoId != null ? "WHERE p.periodo_id=? " : "")
                + "GROUP BY nivel ORDER BY total DESC";
        return periodoId != null ? consultar(sql, periodoId) : consultar(sql);
    }

    /** RF67: proyectos por fase. */
    public List<Map<String, Object>> proyectosPorFase(Integer periodoId) {
        String sql = "SELECT fase_actual, COUNT(*) AS total FROM proyectos p "
                + (periodoId != null ? "WHERE p.periodo_id=? " : "")
                + "GROUP BY fase_actual ORDER BY CASE fase_actual WHEN 'PAGO' THEN 1 WHEN 'PROPUESTA_I' THEN 2 "
                + "WHEN 'PROPUESTA_II' THEN 3 WHEN 'INFORME_I' THEN 4 WHEN 'INFORME_II' THEN 5 "
                + "WHEN 'SUSTENTACION' THEN 6 WHEN 'CERRADO' THEN 7 ELSE 8 END";
        return periodoId != null ? consultar(sql, periodoId) : consultar(sql);
    }

    /** RF68: cuantos estudiantes hay por proyecto y por nivel. */
    public List<Map<String, Object>> estudiantesPorProyecto(Integer periodoId) {
        String sql = "SELECT p.codigo, p.titulo, p.nivel, COUNT(pi.id) AS estudiantes "
                + "FROM proyectos p "
                + "JOIN proyecto_integrantes pi ON pi.proyecto_id=p.id AND pi.estado='ACTIVO' "
                + (periodoId != null ? "WHERE p.periodo_id=? " : "")
                + "GROUP BY p.id, p.codigo, p.titulo, p.nivel ORDER BY estudiantes DESC";
        return periodoId != null ? consultar(sql, periodoId) : consultar(sql);
    }

    /** RF70: listado de estudiantes que aprueban o no el informe final. */
    public List<Map<String, Object>> informeFinal(boolean aprueba, Integer periodoId) {
        String sql = "SELECT p.codigo, p.titulo, u.nombre AS estudiante, rr.nota_final, "
                + "rr.cumple_objetivo_general, rr.cumple_objetivos_especificos, p.estado "
                + "FROM rubrica_resultado rr "
                + "JOIN proyectos p ON p.id=rr.proyecto_id "
                + "JOIN proyecto_integrantes pi ON pi.proyecto_id=p.id AND pi.estado='ACTIVO' "
                + "JOIN usuarios u ON u.id=pi.estudiante_id "
                + "WHERE rr.nota_final " + (aprueba ? ">=" : "<") + " 3.0 "
                + (periodoId != null ? "AND p.periodo_id=? " : "")
                + "ORDER BY u.nombre";
        return periodoId != null ? consultar(sql, periodoId) : consultar(sql);
    }

    /** Conteo rapido por estado para el panel del comite. */
    public List<Map<String, Object>> proyectosPorEstado(Integer periodoId) {
        String sql = "SELECT estado, COUNT(*) AS total FROM proyectos p "
                + (periodoId != null ? "WHERE p.periodo_id=? " : "")
                + "GROUP BY estado ORDER BY total DESC";
        return periodoId != null ? consultar(sql, periodoId) : consultar(sql);
    }
}
