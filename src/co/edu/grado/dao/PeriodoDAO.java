package co.edu.grado.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.grado.modelo.Periodo;
import co.edu.grado.modelo.PeriodoFase;
import co.edu.grado.util.Conexion;
import co.edu.grado.util.FechaUtil;

/** Periodos academicos y calendario por fase. (RF73, RF74) */
public class PeriodoDAO {

    private Periodo mapear(ResultSet rs) throws Exception {
        Periodo p = new Periodo();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setFechaInicio(FechaUtil.aLocalDate(rs.getDate("fecha_inicio")));
        p.setFechaFin(FechaUtil.aLocalDate(rs.getDate("fecha_fin")));
        p.setSemanaPagoMax(rs.getInt("semana_pago_max"));
        p.setSemanaPropuestaMax(rs.getInt("semana_propuesta_max"));
        p.setDiasDuracion(rs.getInt("dias_duracion"));
        p.setDiasMinInforme(rs.getInt("dias_min_informe"));
        p.setMaxProrrogas(rs.getInt("max_prorrogas"));
        p.setActivo(rs.getBoolean("activo"));
        return p;
    }

    public Periodo activo() {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement("SELECT * FROM periodos_academicos WHERE activo = TRUE "
                    + "ORDER BY fecha_inicio DESC LIMIT 1");
            rs = ps.executeQuery();
            return rs.next() ? mapear(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando periodo activo", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public Periodo porId(int id) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement("SELECT * FROM periodos_academicos WHERE id=?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            return rs.next() ? mapear(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando periodo", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    /** RF72: historico de periodos (incluye anteriores). */
    public List<Periodo> listar() {
        List<Periodo> lista = new ArrayList<>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement("SELECT * FROM periodos_academicos ORDER BY fecha_inicio DESC");
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando periodos", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    /** RF73: crea o actualiza el periodo y lo marca activo. */
    public int guardar(Periodo p) {
        String sql = "INSERT INTO periodos_academicos (nombre, fecha_inicio, fecha_fin, semana_pago_max, "
                + "semana_propuesta_max, dias_duracion, dias_min_informe, max_prorrogas, activo) "
                + "VALUES (?,?,?,?,?,?,?,?,TRUE)";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            // solo un periodo activo a la vez
            cn.setAutoCommit(false);
            PreparedStatement off = cn.prepareStatement("UPDATE periodos_academicos SET activo=FALSE");
            off.executeUpdate();
            off.close();

            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, p.getNombre());
            ps.setDate(2, FechaUtil.sql(p.getFechaInicio()));
            ps.setDate(3, FechaUtil.sql(p.getFechaFin()));
            ps.setInt(4, p.getSemanaPagoMax());
            ps.setInt(5, p.getSemanaPropuestaMax());
            ps.setInt(6, p.getDiasDuracion());
            ps.setInt(7, p.getDiasMinInforme());
            ps.setInt(8, p.getMaxProrrogas());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            int id = rs.next() ? rs.getInt(1) : 0;
            cn.commit();
            return id;
        } catch (Exception e) {
            throw new RuntimeException("Error guardando periodo", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public List<PeriodoFase> fases(int periodoId) {
        List<PeriodoFase> lista = new ArrayList<>();
        String sql = "SELECT * FROM periodo_fases WHERE periodo_id=? "
                + "ORDER BY CASE fase WHEN 'PAGO' THEN 1 WHEN 'PROPUESTA_I' THEN 2 WHEN 'PROPUESTA_II' THEN 3 "
                + "WHEN 'INFORME_I' THEN 4 WHEN 'INFORME_II' THEN 5 WHEN 'SUSTENTACION' THEN 6 WHEN 'CERRADO' THEN 7 ELSE 8 END";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, periodoId);
            rs = ps.executeQuery();
            while (rs.next()) {
                PeriodoFase f = new PeriodoFase();
                f.setId(rs.getInt("id"));
                f.setPeriodoId(rs.getInt("periodo_id"));
                f.setFase(rs.getString("fase"));
                f.setFechaMinima(FechaUtil.aLocalDate(rs.getDate("fecha_minima")));
                f.setFechaMaxima(FechaUtil.aLocalDate(rs.getDate("fecha_maxima")));
                f.setResponsable(rs.getString("responsable"));
                lista.add(f);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando fases del periodo", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    /** RF74: guarda o actualiza las fechas de una fase del calendario. */
    public void guardarFase(int periodoId, String fase, java.time.LocalDate min,
                            java.time.LocalDate max, String responsable) {
        String sql = "INSERT INTO periodo_fases (periodo_id, fase, fecha_minima, fecha_maxima, responsable) "
                + "VALUES (?,?,?,?,?) ON CONFLICT (periodo_id, fase) DO UPDATE SET "
                + "fecha_minima=EXCLUDED.fecha_minima, fecha_maxima=EXCLUDED.fecha_maxima, "
                + "responsable=EXCLUDED.responsable";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, periodoId);
            ps.setString(2, fase);
            ps.setDate(3, FechaUtil.sql(min));
            ps.setDate(4, FechaUtil.sql(max));
            ps.setString(5, responsable);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error guardando fase del periodo", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }
}
