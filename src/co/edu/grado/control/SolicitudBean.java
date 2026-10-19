package co.edu.grado.control;

import java.util.List;

import co.edu.grado.dao.HistorialDAO;
import co.edu.grado.dao.PeriodoDAO;
import co.edu.grado.dao.ProyectoDAO;
import co.edu.grado.dao.SolicitudDAO;
import co.edu.grado.dao.UsuarioDAO;
import co.edu.grado.modelo.Periodo;
import co.edu.grado.modelo.Proyecto;
import co.edu.grado.modelo.Solicitud;
import co.edu.grado.modelo.Usuario;
import co.edu.grado.util.Correo;
import co.edu.grado.util.FechaUtil;
import co.edu.grado.util.Notificador;
import co.edu.grado.util.ValidacionUtil;

/**
 * Prorrogas, cambio de director, retiro de integrante y PQRS. (RF48 - RF54)
 */
public class SolicitudBean {

    private Usuario usuarioSesion;

    private int solicitudId;
    private int proyectoId;
    private String tipo;
    private String justificacion;
    private String urlCarta;
    private String urlComprobante;
    private Integer docenteSolicitadoId;

    private String observacionesResolucion;
    private String filtroTipo;
    private String filtroEstado;
    private int pagina = 1;

    private String mensaje;
    private String error;

    private final SolicitudDAO dao = new SolicitudDAO();
    private final ProyectoDAO proyectoDAO = new ProyectoDAO();
    private final PeriodoDAO periodoDAO = new PeriodoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void setUsuarioSesion(Usuario u) { this.usuarioSesion = u; }
    public Usuario getUsuarioSesion() { return usuarioSesion; }
    public void setSolicitudId(int v) { this.solicitudId = v; }
    public int getSolicitudId() { return solicitudId; }
    public void setProyectoId(int v) { this.proyectoId = v; }
    public int getProyectoId() { return proyectoId; }
    public void setTipo(String v) { this.tipo = v; }
    public String getTipo() { return tipo; }
    public void setJustificacion(String v) { this.justificacion = v; }
    public String getJustificacion() { return justificacion; }
    public void setUrlCarta(String v) { this.urlCarta = v; }
    public String getUrlCarta() { return urlCarta; }
    public void setUrlComprobante(String v) { this.urlComprobante = v; }
    public String getUrlComprobante() { return urlComprobante; }
    public void setDocenteSolicitadoId(Integer v) { this.docenteSolicitadoId = v; }
    public Integer getDocenteSolicitadoId() { return docenteSolicitadoId; }
    public void setObservacionesResolucion(String v) { this.observacionesResolucion = v; }
    public void setFiltroTipo(String v) { this.filtroTipo = v; }
    public void setFiltroEstado(String v) { this.filtroEstado = v; }
    public void setPagina(int v) { this.pagina = v < 1 ? 1 : v; }
    public int getPagina() { return pagina; }
    public String getMensaje() { return mensaje; }
    public String getError() { return error; }

    /** RF48, RF51, RF52, RF53: crea una solicitud. */
    public boolean crear() {
        error = null;
        mensaje = null;
        if (usuarioSesion == null) {
            error = "Sesion no valida.";
            return false;
        }
        if (ValidacionUtil.esVacio(tipo)) {
            error = "Debe seleccionar el tipo de solicitud.";
            return false;
        }
        if (ValidacionUtil.esVacio(justificacion)) {
            error = "Debe registrar la justificacion.";
            return false;
        }
        if (!ValidacionUtil.esVacio(urlCarta) && !ValidacionUtil.esUrl(urlCarta)) {
            error = "La URL de la carta no es valida.";
            return false;
        }
        if (!ValidacionUtil.esVacio(urlComprobante) && !ValidacionUtil.esUrl(urlComprobante)) {
            error = "La URL del comprobante no es valida.";
            return false;
        }
        Proyecto p = proyectoDAO.porId(proyectoId);
        if (p == null) {
            error = "Proyecto no encontrado.";
            return false;
        }
        if ("PRORROGA".equals(tipo)) {
            if (ValidacionUtil.esVacio(urlCarta) || ValidacionUtil.esVacio(urlComprobante)) {
                error = "La prorroga requiere la URL de la carta y del comprobante de pago.";
                return false;
            }
            if (p.getFechaVencimiento() != null && FechaUtil.diasRestantes(p.getFechaVencimiento()) < 0) {
                error = "La solicitud debe radicarse antes del vencimiento.";
                return false;
            }
        }
        if ("RETIRO".equals(tipo) && ValidacionUtil.esVacio(urlCarta)) {
            error = "El retiro requiere la carta dirigida al Comite.";
            return false;
        }
        if ("CAMBIO_DIRECTOR".equals(tipo) && docenteSolicitadoId == null) {
            error = "Debe indicar el docente propuesto como nuevo director.";
            return false;
        }
        Solicitud s = new Solicitud();
        s.setProyectoId(proyectoId);
        s.setTipo(tipo);
        s.setSolicitanteId(usuarioSesion.getId());
        s.setJustificacion(justificacion.trim());
        s.setUrlCarta(ValidacionUtil.esVacio(urlCarta) ? null : urlCarta.trim());
        s.setUrlComprobante(ValidacionUtil.esVacio(urlComprobante) ? null : urlComprobante.trim());
        s.setDocenteSolicitadoId(docenteSolicitadoId);
        int id = dao.crear(s);
        mensaje = "Solicitud radicada. El Comite la revisara.";
        new HistorialDAO().registrar(usuarioSesion.getId(), "SOLICITUD", id, "CREAR",
                tipo + ": " + justificacion);
        notificarComite("Nueva solicitud de " + tipo + " para el proyecto " + p.getCodigo() + ".");
        return true;
    }

    /** RF49, RF50, RF52, RF53: el Comite resuelve la solicitud. */
    public boolean resolver(boolean aprobar) {
        error = null;
        mensaje = null;
        Solicitud s = dao.porId(solicitudId);
        if (s == null) {
            error = "Solicitud no encontrada.";
            return false;
        }
        Proyecto p = proyectoDAO.porId(s.getProyectoId());
        if (aprobar) {
            if ("PRORROGA".equals(s.getTipo())) {
                Periodo per = periodoDAO.porId(p.getPeriodoId());
                int max = per != null ? per.getMaxProrrogas() : 2;
                if (p.getProrrogasUtilizadas() >= max) {
                    error = "El proyecto ya alcanzo el maximo de " + max + " prorrogas.";
                    return false;
                }
                proyectoDAO.aplicarProrroga(p.getId()); // +180 dias (RF49)
                mensaje = "Prorroga aprobada. El proyecto se habilita por 180 dias mas.";
            } else if ("CAMBIO_DIRECTOR".equals(s.getTipo())) {
                proyectoDAO.asignarDocente(p.getId(), s.getDocenteSolicitadoId(), "DIRECTOR");
                mensaje = "Cambio de director aprobado.";
            } else if ("RETIRO".equals(s.getTipo())) {
                proyectoDAO.retirarIntegrante(p.getId(), s.getSolicitanteId());
                mensaje = "Retiro del integrante aprobado.";
            } else {
                mensaje = "Solicitud aprobada.";
            }
            dao.resolver(solicitudId, "APROBADA", observacionesResolucion, usuarioSesion.getId());
        } else {
            if (ValidacionUtil.esVacio(observacionesResolucion)) {
                error = "Debe registrar las observaciones de la decision.";
                return false;
            }
            dao.resolver(solicitudId, "RECHAZADA", observacionesResolucion, usuarioSesion.getId());
            mensaje = "Solicitud rechazada.";
        }
        new HistorialDAO().registrar(usuarioSesion.getId(), "SOLICITUD", solicitudId,
                aprobar ? "APROBAR" : "RECHAZAR", s.getTipo());
        notificarSolicitante(s, mensaje + (observacionesResolucion == null ? "" : " " + observacionesResolucion));
        return true;
    }

    public List<Solicitud> getLista() {
        if (usuarioSesion != null && usuarioSesion.esEstudiante()) {
            return dao.listar(vacio(filtroTipo), vacio(filtroEstado), proyectoDeEstudiante(), pagina);
        }
        return dao.listar(vacio(filtroTipo), vacio(filtroEstado), null, pagina);
    }

    public int getTotal() {
        Integer proy = null;
        if (usuarioSesion != null && usuarioSesion.esEstudiante()) {
            proy = proyectoDeEstudiante();
        }
        return dao.contar(vacio(filtroTipo), vacio(filtroEstado), proy);
    }

    public int getTotalPaginas() {
        int total = getTotal();
        return total == 0 ? 1 : (int) Math.ceil(total / (double) SolicitudDAO.TAMANO_PAGINA);
    }

    public Solicitud porId(int id) {
        return dao.porId(id);
    }

    public List<Usuario> getDocentes() {
        return usuarioDAO.listarDocentes();
    }

    private Integer proyectoDeEstudiante() {
        Proyecto p = proyectoDAO.proyectoActivoDeEstudiante(usuarioSesion.getId());
        return p == null ? -1 : p.getId();
    }

    private String rol() {
        return usuarioSesion == null ? "" : usuarioSesion.getRol();
    }

    private String vacio(String v) {
        return ValidacionUtil.esVacio(v) ? null : v;
    }

    private void notificarComite(String msg) {
        for (Usuario c : usuarioDAO.listarPorRol("COMITE")) {
            new Notificador().avisar(c.getId(), msg, "vistas/solicitudes/listar.jsp");
            Correo.enviar(c.getEmail(), "Solicitud de trabajo de grado", msg);
        }
    }

    private void notificarSolicitante(Solicitud s, String msg) {
        Usuario u = usuarioDAO.porId(s.getSolicitanteId());
        new Notificador().avisar(s.getSolicitanteId(), msg, "vistas/solicitudes/listar.jsp");
        if (u != null) {
            Correo.enviar(u.getEmail(), "Respuesta a su solicitud", msg);
        }
    }
}
