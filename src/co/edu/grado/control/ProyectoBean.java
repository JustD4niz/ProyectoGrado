package co.edu.grado.control;

import java.util.ArrayList;
import java.util.List;

import co.edu.grado.dao.HistorialDAO;
import co.edu.grado.dao.IdeaDAO;
import co.edu.grado.dao.PeriodoDAO;
import co.edu.grado.dao.ProyectoDAO;
import co.edu.grado.dao.UsuarioDAO;
import co.edu.grado.modelo.AsignacionDocente;
import co.edu.grado.modelo.Idea;
import co.edu.grado.modelo.Periodo;
import co.edu.grado.modelo.Proyecto;
import co.edu.grado.modelo.Usuario;
import co.edu.grado.util.Correo;
import co.edu.grado.util.Notificador;
import co.edu.grado.util.ValidacionUtil;

/**
 * Inscripcion, conformacion y consulta de proyectos. (RF20 - RF26, RF52, RF61 - RF64)
 */
public class ProyectoBean {

    private Usuario usuarioSesion;

    private int proyectoId;
    private int ideaId;
    private String modalidad;
    private String titulo;
    private String objetivos;
    private String lineaInvestigacion;
    private String cedulas;

    private String busqueda;
    private String fase;
    private String estado;
    private String nivel;
    private Integer periodoId;
    private int pagina = 1;

    private String mensaje;
    private String error;

    private final ProyectoDAO proyectoDAO = new ProyectoDAO();
    private final IdeaDAO ideaDAO = new IdeaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final PeriodoDAO periodoDAO = new PeriodoDAO();

    public void setUsuarioSesion(Usuario u) { this.usuarioSesion = u; }
    public Usuario getUsuarioSesion() { return usuarioSesion; }
    public void setProyectoId(int v) { this.proyectoId = v; }
    public int getProyectoId() { return proyectoId; }
    public void setIdeaId(int v) { this.ideaId = v; }
    public int getIdeaId() { return ideaId; }
    public void setModalidad(String v) { this.modalidad = v; }
    public String getModalidad() { return modalidad; }
    public void setTitulo(String v) { this.titulo = v; }
    public String getTitulo() { return titulo; }
    public void setObjetivos(String v) { this.objetivos = v; }
    public String getObjetivos() { return objetivos; }
    public void setLineaInvestigacion(String v) { this.lineaInvestigacion = v; }
    public void setCedulas(String v) { this.cedulas = v; }
    public String getCedulas() { return cedulas; }
    public void setBusqueda(String v) { this.busqueda = v; }
    public String getBusqueda() { return busqueda == null ? "" : busqueda; }
    public void setFase(String v) { this.fase = v; }
    public String getFase() { return fase == null ? "" : fase; }
    public void setEstado(String v) { this.estado = v; }
    public String getEstado() { return estado == null ? "" : estado; }
    public void setNivel(String v) { this.nivel = v; }
    public void setPeriodoId(Integer v) { this.periodoId = v; }
    public Integer getPeriodoId() { return periodoId; }
    public void setPagina(int v) { this.pagina = v < 1 ? 1 : v; }
    public int getPagina() { return pagina; }
    public String getMensaje() { return mensaje; }
    public String getError() { return error; }

    /**
     * RF15, RF16, RF17, RF20, RF21, RF22: el estudiante inscribe un proyecto
     * a partir de una idea aprobada (del banco o propia) y conforma el equipo.
     */
    public boolean crearDesdeIdea() {
        error = null;
        mensaje = null;
        if (usuarioSesion == null || !usuarioSesion.esEstudiante()) {
            error = "Solo un estudiante puede inscribir un proyecto.";
            return false;
        }
        Idea idea = ideaDAO.porId(ideaId);
        if (idea == null || !"APROBADA".equals(idea.getEstado())) {
            error = "La idea seleccionada no está disponible.";
            return false;
        }
        boolean propia = idea.getEstudianteId() != null
                && idea.getEstudianteId() == usuarioSesion.getId();
        if (!idea.isDisponible() && !propia) {
            error = "La idea ya fue tomada por otro estudiante.";
            return false;
        }
        if (!idea.getNivel().equals(usuarioSesion.getNivel())) {
            error = "Solo puede seleccionar ideas de su nivel (" + usuarioSesion.getNivel() + ").";
            return false;
        }
        if (proyectoDAO.estudianteTieneProyectoActivo(usuarioSesion.getId())) {
            error = "Ya se encuentra inscrito en un proyecto de grado. Solo puede tener uno a la vez.";
            return false;
        }
        String mod = ValidacionUtil.esVacio(modalidad) ? idea.getModalidad() : modalidad;
        if (proyectoDAO.modalidadCancelada(usuarioSesion.getId(), mod)) {
            error = "No puede repetir la modalidad " + mod + " porque un proyecto anterior fue cancelado.";
            return false;
        }
        int max = "MONOGRAFIA".equals(mod) ? 2 : 3;

        // Equipo: el estudiante que inscribe + las cedulas indicadas
        List<Usuario> equipo = new ArrayList<>();
        equipo.add(usuarioSesion);
        if (!ValidacionUtil.esVacio(cedulas)) {
            for (String ced : cedulas.split("[,;\\s]+")) {
                if (ValidacionUtil.esVacio(ced)) {
                    continue;
                }
                Usuario comp = usuarioDAO.porCedula(ced.trim());
                if (comp == null || !comp.esEstudiante()) {
                    error = "La cedula " + ced + " no corresponde a un estudiante registrado.";
                    return false;
                }
                if (comp.getId() == usuarioSesion.getId()) {
                    continue;
                }
                if (!comp.getNivel().equals(usuarioSesion.getNivel())) {
                    error = "El integrante " + comp.getNombre() + " no pertenece a su nivel.";
                    return false;
                }
                if (proyectoDAO.estudianteTieneProyectoActivo(comp.getId())) {
                    error = "El integrante " + comp.getNombre() + " ya esta en un proyecto activo.";
                    return false;
                }
                equipo.add(comp);
            }
        }
        if (equipo.size() > max) {
            error = "La modalidad " + mod + " permite maximo " + max + " integrantes.";
            return false;
        }
        Periodo periodo = periodoDAO.activo();
        if (periodo == null) {
            error = "No hay un periodo academico activo.";
            return false;
        }
        String tituloFinal = ValidacionUtil.esVacio(titulo) ? idea.getTitulo() : titulo.trim();
        Proyecto p = new Proyecto();
        p.setCodigo(proyectoDAO.generarCodigo(idea.getNivel(), periodo.getNombre()));
        p.setTitulo(tituloFinal);
        p.setObjetivos(objetivos);
        p.setModalidad(mod);
        p.setNivel(idea.getNivel());
        p.setLineaInvestigacion(idea.getLineaInvestigacion());
        p.setPeriodoId(periodo.getId());
        p.setIdeaId(idea.getId());
        int nuevoId = proyectoDAO.crear(p);
        for (Usuario u : equipo) {
            proyectoDAO.agregarIntegrante(nuevoId, u.getId());
        }
        ideaDAO.marcarTomada(idea.getId());
        new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", nuevoId, "CREAR",
                "Inscripcion de proyecto " + p.getCodigo() + " (modalidad " + mod + ")");

        // RF23: asignacion automatica de director y evaluador
        Usuario director = elegirDirector(idea);
        if (director != null) {
            proyectoDAO.asignarDocente(nuevoId, director.getId(), "DIRECTOR");
            new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", nuevoId, "ASIGNAR_DIRECTOR",
                    director.getNombre() + " asignado automáticamente como DIRECTOR");
            new Notificador().avisar(director.getId(),
                    "Fue asignado como director del proyecto " + p.getCodigo(), "vistas/proyectos/mis-proyectos.jsp");
            Correo.enviar(director.getEmail(), "Asignacion como director del proyecto " + p.getCodigo(),
                    "Usted fue asignado como director del proyecto " + p.getCodigo() + " (" + p.getTitulo() + ").");
        }
        Usuario evaluador = elegirDocenteMenosCargado("EVALUADOR", director == null ? null : director.getId());
        if (evaluador != null) {
            proyectoDAO.asignarDocente(nuevoId, evaluador.getId(), "EVALUADOR");
            new HistorialDAO().registrar(usuarioSesion.getId(), "PROYECTO", nuevoId, "ASIGNAR_EVALUADOR",
                    evaluador.getNombre() + " asignado automáticamente como EVALUADOR");
            new Notificador().avisar(evaluador.getId(),
                    "Fue asignado como evaluador del proyecto " + p.getCodigo(), "vistas/proyectos/proyectos-evaluador.jsp");
            Correo.enviar(evaluador.getEmail(), "Asignacion como evaluador del proyecto " + p.getCodigo(),
                    "Usted fue asignado como evaluador del proyecto " + p.getCodigo() + " (" + p.getTitulo() + ").");
        }
        if (director == null || evaluador == null) {
            notificarComite("Nuevo proyecto inscrito " + p.getCodigo()
                    + ". No fue posible asignar director o evaluador automáticamente.");
        }
        mensaje = "Proyecto inscrito con el código " + p.getCodigo() + "."
                + (director != null ? " Director asignado: " + director.getNombre() + "." : "");

        for (Usuario u : equipo) {
            if (u.getId() != usuarioSesion.getId()) {
                new Notificador().avisar(u.getId(), "Fue vinculado al proyecto " + p.getCodigo(),
                        "vistas/proyectos/mi-proyecto.jsp");
                Correo.enviar(u.getEmail(), "Vinculacion a proyecto de grado",
                        "Usted fue vinculado al proyecto " + p.getCodigo() + " (" + p.getTitulo() + ").");
            }
            if (director != null) {
                new Notificador().avisar(u.getId(),
                        "Se asigno director a su proyecto: " + director.getNombre(), "vistas/proyectos/mi-proyecto.jsp");
            }
            if (evaluador != null) {
                new Notificador().avisar(u.getId(),
                        "Se asigno evaluador a su proyecto: " + evaluador.getNombre(), "vistas/proyectos/mi-proyecto.jsp");
            }
        }
        return true;
    }

    /** RF23: elige el director segun el origen de la idea (autor del banco o el de menor carga). */
    private Usuario elegirDirector(Idea idea) {
        if (idea != null && !idea.isDeEstudiante() && idea.getProponenteId() != null) {
            Usuario autor = usuarioDAO.porId(idea.getProponenteId());
            if (autor != null && autor.esDocente()) {
                return autor;
            }
        }
        return elegirDocenteMenosCargado("DIRECTOR", null);
    }

    /** RF23: docente activo con menor carga en el rol indicado, pudiendo excluir a uno. */
    private Usuario elegirDocenteMenosCargado(String rol, Integer excluir) {
        Usuario mejor = null;
        int menor = Integer.MAX_VALUE;
        for (Usuario d : usuarioDAO.listarDocentes()) {
            if (excluir != null && d.getId() == excluir.intValue()) {
                continue;
            }
            int carga = proyectoDAO.contarProyectosDeDocente(d.getId(), rol);
            if (carga < menor) {
                menor = carga;
                mejor = d;
            }
        }
        return mejor;
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    public List<Proyecto> getLista() {
        Integer est = null;
        Integer dir = null;
        Integer eva = null;
        return proyectoDAO.listar(getBusqueda(), vacio(fase), vacio(modalidad), vacio(nivel),
                vacio(estado), periodoId, est, dir, eva, pagina);
    }

    public int getTotal() {
        return proyectoDAO.contar(getBusqueda(), vacio(fase), vacio(modalidad), vacio(nivel),
                vacio(estado), periodoId, null, null, null);
    }

    public int getTotalPaginas() {
        int total = getTotal();
        return total == 0 ? 1 : (int) Math.ceil(total / (double) ProyectoDAO.TAMANO_PAGINA);
    }

    /** Proyectos en flujo donde el COMITE es responsable actual. */
    public List<Proyecto> getPendientesComite() {
        return proyectoDAO.listarPendientesComite();
    }

    /** RF64: proyectos del docente actual en el rol indicado. */
    public List<Proyecto> getMisProyectos(String rol) {
        if (usuarioSesion == null) {
            return new ArrayList<>();
        }
        return proyectoDAO.listarPorDocente(usuarioSesion.getId(), rol, vacio(estado), pagina);
    }

    public int getTotalMisProyectos(String rol) {
        if (usuarioSesion == null) {
            return 0;
        }
        return proyectoDAO.contarPorDocente(usuarioSesion.getId(), rol, vacio(estado));
    }

    /** RF17, RF25: proyecto actual del estudiante. */
    public Proyecto getMiProyecto() {
        if (usuarioSesion == null) {
            return null;
        }
        return proyectoDAO.proyectoActivoDeEstudiante(usuarioSesion.getId());
    }

    public Proyecto getProyecto() {
        return proyectoId > 0 ? proyectoDAO.porId(proyectoId) : null;
    }

    public Proyecto getDetalle() {
        return proyectoId > 0 ? proyectoDAO.porId(proyectoId) : null;
    }

    /** RF25: grado de avance de un proyecto (fases aprobadas / total). */
    public int getAvance(int proyectoId) {
        List<co.edu.grado.modelo.FaseProyecto> fases = proyectoDAO.fases(proyectoId);
        if (fases.isEmpty()) {
            return 0;
        }
        int aprobadas = 0;
        for (co.edu.grado.modelo.FaseProyecto f : fases) {
            if ("APROBADA".equals(f.getEstado())) {
                aprobadas++;
            }
        }
        return (int) Math.round(aprobadas * 100.0 / fases.size());
    }

    /** Integrantes de un proyecto (vista de detalle). */
    public List<co.edu.grado.modelo.Integrante> integrantes(int proyectoId) {
        return proyectoDAO.integrantes(proyectoId);
    }

    /** Fases del proyecto actual (vista de detalle). */
    public List<co.edu.grado.modelo.FaseProyecto> getFases() {
        return proyectoId > 0 ? proyectoDAO.fases(proyectoId) : new ArrayList<>();
    }

    public List<Usuario> getDocentes() {
        return usuarioDAO.listarDocentes();
    }

    public List<Idea> getIdeasDisponibles() {
        String nivelFiltro = usuarioSesion != null && usuarioSesion.esEstudiante()
                ? usuarioSesion.getNivel() : null;
        return ideaDAO.listar("APROBADA", nivelFiltro, "BANCO", true, null, null, null, 1);
    }

    public List<Periodo> getPeriodos() {
        return periodoDAO.listar();
    }

    public AsignacionDocente getAsignacion(String rol) {
        return proyectoId > 0 ? proyectoDAO.asignacionActiva(proyectoId, rol) : null;
    }

    /** Indica si el usuario en sesion es estudiante integrante del proyecto actual. */
    public boolean isEsIntegranteDelProyecto() {
        if (usuarioSesion == null || proyectoId <= 0) {
            return false;
        }
        for (co.edu.grado.modelo.Integrante i : proyectoDAO.integrantes(proyectoId)) {
            if (i.isActivo() && i.getEstudianteId() == usuarioSesion.getId()) {
                return true;
            }
        }
        return false;
    }

    private String vacio(String v) {
        return ValidacionUtil.esVacio(v) ? null : v;
    }

    private void notificarComite(String msg) {
        for (Usuario c : usuarioDAO.listarPorRol("COMITE")) {
            new Notificador().avisar(c.getId(), msg, "vistas/proyectos/listar.jsp");
        }
    }
}
