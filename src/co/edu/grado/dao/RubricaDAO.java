package co.edu.grado.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import co.edu.grado.modelo.RubricaItem;
import co.edu.grado.modelo.RubricaResultado;
import co.edu.grado.util.Conexion;
import co.edu.grado.util.FechaUtil;

/** Rubrica institucional del informe final. (RF39) */
public class RubricaDAO {

    public void guardarItem(RubricaItem item) {
        String sql = "INSERT INTO rubrica_informe (proyecto_id, evaluador_id, criterio, puntaje, maximo, "
                + "observaciones) VALUES (?,?,?,?,?,?)";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, item.getProyectoId());
            ps.setInt(2, item.getEvaluadorId());
            ps.setString(3, item.getCriterio());
            ps.setDouble(4, item.getPuntaje());
            ps.setDouble(5, item.getMaximo());
            ps.setString(6, item.getObservaciones());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error guardando rubrica", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public void guardarResultado(RubricaResultado r) {
        String sql = "INSERT INTO rubrica_resultado (proyecto_id, evaluador_id, cumple_objetivo_general, "
                + "cumple_objetivos_especificos, nota_final, observaciones) VALUES (?,?,?,?,?,?) "
                + "ON CONFLICT (proyecto_id) DO UPDATE SET evaluador_id=EXCLUDED.evaluador_id, "
                + "cumple_objetivo_general=EXCLUDED.cumple_objetivo_general, "
                + "cumple_objetivos_especificos=EXCLUDED.cumple_objetivos_especificos, "
                + "nota_final=EXCLUDED.nota_final, observaciones=EXCLUDED.observaciones, fecha=NOW()";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, r.getProyectoId());
            ps.setInt(2, r.getEvaluadorId());
            ps.setBoolean(3, r.isCumpleObjetivoGeneral());
            ps.setBoolean(4, r.isCumpleObjetivosEspecificos());
            ps.setDouble(5, r.getNotaFinal());
            ps.setString(6, r.getObservaciones());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error guardando resultado de rubrica", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public List<RubricaItem> items(int proyectoId) {
        List<RubricaItem> lista = new ArrayList<>();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement("SELECT * FROM rubrica_informe WHERE proyecto_id=? ORDER BY id");
            ps.setInt(1, proyectoId);
            rs = ps.executeQuery();
            while (rs.next()) {
                RubricaItem i = new RubricaItem();
                i.setId(rs.getInt("id"));
                i.setProyectoId(rs.getInt("proyecto_id"));
                i.setEvaluadorId(rs.getInt("evaluador_id"));
                i.setCriterio(rs.getString("criterio"));
                i.setPuntaje(rs.getDouble("puntaje"));
                i.setMaximo(rs.getDouble("maximo"));
                i.setObservaciones(rs.getString("observaciones"));
                lista.add(i);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando rubrica", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    public RubricaResultado resultado(int proyectoId) {
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement("SELECT * FROM rubrica_resultado WHERE proyecto_id=?");
            ps.setInt(1, proyectoId);
            rs = ps.executeQuery();
            if (rs.next()) {
                RubricaResultado r = new RubricaResultado();
                r.setId(rs.getInt("id"));
                r.setProyectoId(rs.getInt("proyecto_id"));
                r.setEvaluadorId(rs.getInt("evaluador_id"));
                r.setCumpleObjetivoGeneral(rs.getBoolean("cumple_objetivo_general"));
                r.setCumpleObjetivosEspecificos(rs.getBoolean("cumple_objetivos_especificos"));
                r.setNotaFinal(rs.getDouble("nota_final"));
                r.setObservaciones(rs.getString("observaciones"));
                r.setFecha(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha")));
                return r;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando resultado de rubrica", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }
}
