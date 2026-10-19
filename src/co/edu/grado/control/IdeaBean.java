package co.edu.grado.control;

import java.util.List;

import co.edu.grado.dao.HistorialDAO;
import co.edu.grado.dao.IdeaDAO;
import co.edu.grado.dao.UsuarioDAO;
import co.edu.grado.modelo.Idea;
import co.edu.grado.modelo.Usuario;
import co.edu.grado.util.Correo;
import co.edu.grado.util.Notificador;
import co.edu.grado.util.ValidacionUtil;

/**
 * Banco de ideas y propuestas de estudiantes. (RF10 - RF19)
 */
public class IdeaBean {

    private Usuario usuarioSesion;

    private int id;
    private String titulo;
    private String descripcion;
    private String nivel;
    private String lineaInvestigacion;
    private String modalidad;
    private String observaciones;

    private String filtroEstado;
    private String filtroNivel;
    private String filtroOrigen;
    private boolean soloDisponibles;
    private int pagina = 1;
    private String busqueda;

    private String mensaje;
    private String error;

    private final IdeaDAO dao = new IdeaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void setUsuarioSesion(Usuario u) { this.usuarioSesion = u; }
    public Usuario getUsuarioSesion() { return usuarioSesion; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public String getLineaInvestigacion() { return lineaInvestigacion; }
    public void setLineaInvestigacion(String v) { this.lineaInvestigacion = v; }
    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setFiltroEstado(String v) { this.filtroEstado = v; }
    public void setFiltroNivel(String v) { this.filtroNivel = v; }
    public void setFiltroOrigen(String v) { this.filtroOrigen = v; }
    public void setSoloDisponibles(boolean v) { this.soloDisponibles = v; }
    public void setPagina(int pagina) { this.pagina = pagina < 1 ? 1 : pagina; }
    public int getPagina() { return pagina; }
    public void setBusqueda(String busqueda) { this.busqueda = busqueda; }
    public String getBusqueda() { return busqueda; }
    public String getMensaje() { return mensaje; }
    public String getError() { return error; }

    /** RF10, RF16, RF19: registra o actualiza una idea. */
    public boolean guardar() {
        error = null;
        mensaje = null;
        if (usuarioSesion == null) {
            error = "Sesión no válida.";
            return false;
        }
        if (ValidacionUtil.esVacio(titulo) || ValidacionUtil.esVacio(descripcion)
                || ValidacionUtil.esVacio(nivel) || ValidacionUtil.esVacio(modalidad)
                || ValidacionUtil.esVacio(lineaInvestigacion)) {
            error = "Todos los campos de la idea son obligatorios.";
            return false;
        }
        Idea i = new Idea();
        i.setId(id);
        i.setTitulo(titulo.trim());
        i.setDescripcion(descripcion.trim());
        i.setNivel(nivel);
        i.setModalidad(modalidad);
        i.setLineaInvestigacion(lineaInvestigacion.trim());
        boolean esEstudiante = usuarioSesion.esEstudiante();
        if (id > 0) {
            Idea actual = dao.porId(id);
            if (actual != null && ("APROBADA".equals(actual.getEstado())
                    || "RECHAZADA".equals(actual.getEstado()))) {
                error = "No se puede editar una idea que ya fue "
                        + ("APROBADA".equals(actual.getEstado()) ? "aprobada" : "rechazada")
                        + ". Solo se pueden editar las ideas pendientes de revision del Comité.";
                return false;
            }
            dao.actualizar(i);
            mensaje = "Idea actualizada correctamente.";
            new HistorialDAO().registrar(usuarioSesion.getId(), "IDEA", id, "ACTUALIZAR",
                    "Idea actualizada: " + i.getTitulo());
        } else {
            i.setOrigen(esEstudiante ? "PROPUESTA_ESTUDIANTE" : "BANCO");
            if (esEstudiante) {
                i.setEstudianteId(usuarioSesion.getId());
                i.setNivel(usuarioSesion.getNivel());
            } else {
                i.setProponenteId(usuarioSesion.getId());
            }
            i.setEstado("PENDIENTE");
            i.setDisponible(!esEstudiante);
            int nuevo = dao.crear(i);
            mensaje = esEstudiante ? "Propuesta enviada al Comité para su evaluación."
                    : "Idea registrada en el banco, pendiente de aprobación.";
            new HistorialDAO().registrar(usuarioSesion.getId(), "IDEA", nuevo, "CREAR",
                    (esEstudiante ? "Propuesta de estudiante: " : "Idea de banco: ") + i.getTitulo());
            notificarComite(i.getTitulo());
        }
        return true;
    }

    /** RF11: aprueba una idea. */
    public boolean aprobar(int ideaId) {
        Idea i = dao.porId(ideaId);
        if (i == null) {
            return false;
        }
        dao.decidir(ideaId, "APROBADA", null, usuarioSesion.getId());
        new HistorialDAO().registrar(usuarioSesion.getId(), "IDEA", ideaId, "APROBAR",
                "Idea aprobada: " + i.getTitulo());
        notificarInteresado(i, "Su idea fue APROBADA: " + i.getTitulo());
        return true;
    }

    /** RF11, RF12: rechaza una idea registrando las observaciones. */
    public boolean rechazar(int ideaId) {
        if (ValidacionUtil.esVacio(observaciones)) {
            error = "El rechazo requiere observaciones que lo justifiquen.";
            return false;
        }
        Idea i = dao.porId(ideaId);
        if (i == null) {
            return false;
        }
        dao.decidir(ideaId, "RECHAZADA", observaciones, usuarioSesion.getId());
        new HistorialDAO().registrar(usuarioSesion.getId(), "IDEA", ideaId, "RECHAZAR",
                "Idea rechazada: " + i.getTitulo() + " - " + observaciones);
        notificarInteresado(i, "Su idea fue RECHAZADA: " + i.getTitulo() + ". Observaciones: " + observaciones);
        return true;
    }

    /** RF11: aprueba varias ideas de una vez. */
    public int aprobarVarias(List<Integer> ids) {
        int n = 0;
        for (Integer ideaId : ids) {
            if (aprobar(ideaId)) {
                n++;
            }
        }
        return n;
    }

    /** RF19: elimina una idea (solo comite/administrador). */
    public boolean eliminar() {
        error = null;
        Idea i = dao.porId(id);
        if (i == null) {
            error = "La idea no existe.";
            return false;
        }
        if ("APROBADA".equals(i.getEstado()) || "RECHAZADA".equals(i.getEstado())) {
            error = "No se puede eliminar una propuesta que ya fue "
                    + ("APROBADA".equals(i.getEstado()) ? "aprobada" : "rechazada")
                    + " por el Comité.";
            return false;
        }
        if (!i.isDisponible()) {
            error = "No se puede eliminar una idea que ya fue tomada por un estudiante.";
            return false;
        }
        dao.eliminar(id);
        mensaje = "Idea eliminada correctamente.";
        new HistorialDAO().registrar(usuarioSesion == null ? null : usuarioSesion.getId(),
                "IDEA", id, "ELIMINAR", "Idea eliminada: " + i.getTitulo());
        return true;
    }

    // ------------------------------------------------------------------
    // Consultas para las vistas
    // ------------------------------------------------------------------

    public List<Idea> getLista() {
        String nivelFiltro = filtroNivel;
        Integer proponente = null;
        Integer estudiante = null;
        if (usuarioSesion != null && usuarioSesion.esEstudiante()) {
            nivelFiltro = usuarioSesion.getNivel(); // RF14 solo su nivel
        }
        return dao.listar(vacio(filtroEstado), vacio(nivelFiltro), vacio(filtroOrigen),
                soloDisponibles, proponente, estudiante, vacio(busqueda), pagina);
    }

    /** RF13, RF14: banco visible para el estudiante (solo ideas de su nivel, disponibles). */
    public List<Idea> getBanco() {
        String nivelFiltro = usuarioSesion != null && usuarioSesion.esEstudiante()
                ? usuarioSesion.getNivel() : vacio(filtroNivel);
        return dao.listar("APROBADA", nivelFiltro, "BANCO", true, null, null, vacio(busqueda), pagina);
    }

    /** Bandeja de ideas pendientes para el comite (RF11). */
    public List<Idea> getBandeja() {
        return dao.listar("PENDIENTE", vacio(filtroNivel), vacio(filtroOrigen),
                false, null, null, vacio(busqueda), pagina);
    }

    /** RF18: trazabilidad completa del banco. */
    public List<Idea> getTrazabilidad() {
        return dao.listar(vacio(filtroEstado), vacio(filtroNivel), vacio(filtroOrigen),
                false, null, null, vacio(busqueda), pagina);
    }

    /** Ideas registradas por el director actual. */
    public List<Idea> getMisIdeas() {
        Integer prop = usuarioSesion == null ? null : usuarioSesion.getId();
        return dao.listar(vacio(filtroEstado), null, "BANCO", false, prop, null, vacio(busqueda), pagina);
    }

    /** RF16: propuestas propias del estudiante. */
    public List<Idea> getMisPropuestas() {
        Integer est = usuarioSesion == null ? null : usuarioSesion.getId();
        return dao.listar(null, null, "PROPUESTA_ESTUDIANTE", false, null, est, vacio(busqueda), pagina);
    }

    public List<Idea> getPropuestas() {
        return dao.listar(vacio(filtroEstado), vacio(filtroNivel), "PROPUESTA_ESTUDIANTE", false, null, null,
                vacio(busqueda), pagina);
    }

    public int getTotal() {
        String nivelFiltro = filtroNivel;
        if (usuarioSesion != null && usuarioSesion.esEstudiante()) {
            nivelFiltro = usuarioSesion.getNivel();
        }
        return dao.contar(vacio(filtroEstado), vacio(nivelFiltro), vacio(filtroOrigen),
                soloDisponibles, null, null, vacio(busqueda));
    }

    public int getTotalPaginas() {
        int total = getTotal();
        return total == 0 ? 1 : (int) Math.ceil(total / (double) IdeaDAO.TAMANO_PAGINA);
    }

    public Idea porId(int ideaId) {
        return dao.porId(ideaId);
    }

    private String vacio(String v) {
        return ValidacionUtil.esVacio(v) ? null : v;
    }

    private void notificarComite(String titulo) {
        for (Usuario c : usuarioDAO.listarPorRol("COMITE")) {
            new Notificador().avisar(c.getId(), "Nueva idea registrada: " + titulo,
                    "vistas/ideas/bandeja.jsp");
            Correo.enviar(c.getEmail(), "Nueva idea registrada",
                    "Se registró una nueva idea en el banco: " + titulo);
        }
    }

    private void notificarInteresado(Idea i, String msg) {
        Integer destino = i.getProponenteId() != null ? i.getProponenteId() : i.getEstudianteId();
        if (destino == null) {
            return;
        }
        Usuario u = usuarioDAO.porId(destino);
        new Notificador().avisar(destino, msg, "vistas/ideas/mis-ideas.jsp");
        if (u != null) {
            Correo.enviar(u.getEmail(), "Decision sobre su idea", msg);
        }
    }
}
