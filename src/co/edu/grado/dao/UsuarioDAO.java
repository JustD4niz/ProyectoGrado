package co.edu.grado.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import co.edu.grado.modelo.Usuario;
import co.edu.grado.util.Conexion;
import co.edu.grado.util.FechaUtil;
import co.edu.grado.util.HashUtil;

/**
 * Acceso a datos de usuarios: autenticacion, CRUD y consultas de carga docente.
 * (RF01 - RF09)
 */
public class UsuarioDAO {

    public static final int TAMANO_PAGINA = 20;

    private Usuario mapear(ResultSet rs) throws Exception {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setCedula(rs.getString("cedula"));
        u.setNombre(rs.getString("nombre"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setPasswordSalt(rs.getString("password_salt"));
        u.setRol(rs.getString("rol"));
        u.setNivel(rs.getString("nivel"));
        u.setActivo(rs.getBoolean("activo"));
        u.setConsentimientoDatos(rs.getBoolean("consentimiento_datos"));
        u.setFechaRegistro(FechaUtil.aLocalDateTime(rs.getTimestamp("fecha_registro")));
        return u;
    }

    /** RF01: busqueda por correo institucional para autenticar. */
    public Usuario porEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, email);
            rs = ps.executeQuery();
            return rs.next() ? mapear(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando usuario por correo", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public Usuario porCedula(String cedula) {
        String sql = "SELECT * FROM usuarios WHERE cedula = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, cedula);
            rs = ps.executeQuery();
            return rs.next() ? mapear(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando usuario por cedula", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public Usuario porId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();
            return rs.next() ? mapear(rs) : null;
        } catch (Exception e) {
            throw new RuntimeException("Error consultando usuario", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    /** RF05: consulta de usuarios con filtro por rol/busqueda y paginacion (RF62). */
    public List<Usuario> listar(String rol, String busqueda, int pagina) {
        List<Usuario> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM usuarios WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (rol != null && !rol.isEmpty()) {
            sql.append(" AND rol = ?");
            params.add(rol);
        }
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append(" AND (nombre ILIKE ? OR email ILIKE ? OR cedula ILIKE ?)");
            String like = "%" + busqueda.trim() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        sql.append(" ORDER BY fecha_registro DESC LIMIT ? OFFSET ?");
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
            throw new RuntimeException("Error listando usuarios", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    public int contar(String rol, String busqueda) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM usuarios WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (rol != null && !rol.isEmpty()) {
            sql.append(" AND rol = ?");
            params.add(rol);
        }
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            sql.append(" AND (nombre ILIKE ? OR email ILIKE ? OR cedula ILIKE ?)");
            String like = "%" + busqueda.trim() + "%";
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
            throw new RuntimeException("Error contando usuarios", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public List<Usuario> listarPorRol(String rol) {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol = ? AND activo = TRUE ORDER BY nombre";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, rol);
            rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando usuarios por rol", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    /** Docentes que pueden ser directores o evaluadores (RF03, RF23). */
    public List<Usuario> listarDocentes() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE rol IN ('DOCENTE','DIRECTOR','EVALUADOR') AND activo = TRUE "
                + "ORDER BY nombre";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = mapear(rs);
                u.setCargaProyectos(contarProyectosAbiertos(u.getId()));
                u.setTieneProyectosAbiertos(u.getCargaProyectos() > 0);
                lista.add(u);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error listando docentes", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
        return lista;
    }

    /** RF02: alta de usuario con contrasena cifrada (RNF03). */
    public int crear(Usuario u, String contrasena) {
        String sal = HashUtil.generarSal();
        String hash = HashUtil.hash(contrasena, sal);
        String sql = "INSERT INTO usuarios (cedula, nombre, email, password_hash, password_salt, rol, nivel, "
                + "activo, consentimiento_datos) VALUES (?,?,?,?,?,?,?,?,?)";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, u.getCedula());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getEmail());
            ps.setString(4, hash);
            ps.setString(5, sal);
            ps.setString(6, u.getRol());
            ps.setString(7, u.getNivel());
            ps.setBoolean(8, u.isActivo());
            ps.setBoolean(9, u.isConsentimientoDatos());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error creando usuario", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public void actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET cedula=?, nombre=?, email=?, rol=?, nivel=?, activo=? WHERE id=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, u.getCedula());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getRol());
            ps.setString(5, u.getNivel());
            ps.setBoolean(6, u.isActivo());
            ps.setInt(7, u.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando usuario", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public void actualizarPassword(int id, String contrasena) {
        String sal = HashUtil.generarSal();
        String hash = HashUtil.hash(contrasena, sal);
        String sql = "UPDATE usuarios SET password_hash=?, password_salt=? WHERE id=?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, hash);
            ps.setString(2, sal);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando contrasena", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    public void eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        Connection cn = null;
        PreparedStatement ps = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Error eliminando usuario", e);
        } finally {
            Conexion.cerrar(null, ps, cn);
        }
    }

    /** RF06: cuenta proyectos abiertos de un docente como director o evaluador. */
    public int contarProyectosAbiertos(int docenteId) {
        String sql = "SELECT COUNT(DISTINCT a.proyecto_id) FROM asignaciones_docente a "
                + "JOIN proyectos p ON p.id = a.proyecto_id "
                + "WHERE a.docente_id = ? AND a.activo = TRUE AND p.estado = 'EN_FLUJO'";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setInt(1, docenteId);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error contando proyectos del docente", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    /**
     * RNF17: describe el historial que impide borrar un usuario, o null si puede eliminarse.
     * Cubre las claves foraneas que referencian usuarios(id).
     */
    public String motivoBloqueoEliminacion(int id) {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM ideas WHERE proponente_id=? OR estudiante_id=? OR revisada_por=?) AS ideas, "
                + "(SELECT COUNT(*) FROM proyecto_integrantes WHERE estudiante_id=?) AS integrantes, "
                + "(SELECT COUNT(*) FROM asignaciones_docente WHERE docente_id=?) AS asignaciones, "
                + "(SELECT COUNT(*) FROM documentos WHERE autor_id=?) AS documentos, "
                + "(SELECT COUNT(*) FROM seguimientos WHERE director_id=?) AS seguimientos, "
                + "(SELECT COUNT(*) FROM sustentaciones WHERE registrado_por=?) AS sustentaciones, "
                + "(SELECT COUNT(*) FROM rubrica_informe WHERE evaluador_id=?) AS rubrica_informe, "
                + "(SELECT COUNT(*) FROM rubrica_resultado WHERE evaluador_id=?) AS rubrica_resultado, "
                + "(SELECT COUNT(*) FROM solicitudes WHERE solicitante_id=? OR docente_solicitado_id=? OR resuelta_por=?) AS solicitudes";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            int p = 1;
            for (int k = 0; k < 13; k++) {
                ps.setInt(p++, id);
            }
            rs = ps.executeQuery();
            if (!rs.next()) {
                return null;
            }
            List<String> motivos = new ArrayList<>();
            int ideas = rs.getInt("ideas");
            if (ideas > 0) motivos.add(ideas + " idea(s) o propuesta(s)");
            int integrantes = rs.getInt("integrantes");
            if (integrantes > 0) motivos.add(integrantes + " proyecto(s) como integrante");
            int asignaciones = rs.getInt("asignaciones");
            if (asignaciones > 0) motivos.add(asignaciones + " asignación(es) como director/evaluador");
            int documentos = rs.getInt("documentos");
            if (documentos > 0) motivos.add(documentos + " documento(s) cargado(s)");
            int seguimientos = rs.getInt("seguimientos");
            if (seguimientos > 0) motivos.add(seguimientos + " seguimiento(s)");
            int sustentaciones = rs.getInt("sustentaciones");
            if (sustentaciones > 0) motivos.add(sustentaciones + " sustentación(es)");
            int rubrica = rs.getInt("rubrica_informe") + rs.getInt("rubrica_resultado");
            if (rubrica > 0) motivos.add(rubrica + " evaluación(es) de rúbrica");
            int solicitudes = rs.getInt("solicitudes");
            if (solicitudes > 0) motivos.add(solicitudes + " solicitud(es)");
            return motivos.isEmpty() ? null : String.join(", ", motivos);
        } catch (Exception e) {
            throw new RuntimeException("Error verificando dependencias del usuario", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public boolean existeEmail(String email, int exceptoId) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ? AND id <> ?";
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setInt(2, exceptoId);
            rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error validando correo", e);
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }
}
