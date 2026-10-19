package co.edu.grado.control;

import java.util.ArrayList;
import java.util.List;

import co.edu.grado.dao.NotificacionDAO;
import co.edu.grado.dao.ProyectoDAO;
import co.edu.grado.modelo.Notificacion;
import co.edu.grado.modelo.Proyecto;
import co.edu.grado.modelo.Usuario;

/** Notificaciones en pantalla y recordatorios de vencimiento. (RF59) */
public class AvisoBean {

    private Usuario usuarioSesion;
    private final NotificacionDAO notificacionDAO = new NotificacionDAO();
    private final ProyectoDAO proyectoDAO = new ProyectoDAO();

    public void setUsuarioSesion(Usuario u) { this.usuarioSesion = u; }

    public int getNoLeidas() {
        return usuarioSesion == null ? 0 : notificacionDAO.contarNoLeidas(usuarioSesion.getId());
    }

    public List<Notificacion> getNotificaciones() {
        if (usuarioSesion == null) {
            return new ArrayList<>();
        }
        return notificacionDAO.listar(usuarioSesion.getId(), false);
    }

    public boolean marcarLeidas() {
        if (usuarioSesion != null) {
            notificacionDAO.marcarLeidas(usuarioSesion.getId());
        }
        return true;
    }

    /**
     * RF59: recordatorios que se muestran en pantalla (no por correo) cuando
     * faltan 30, 15 o 5 dias o menos para el vencimiento del proyecto.
     */
    public List<String> getRecordatorios() {
        List<String> avisos = new ArrayList<>();
        if (usuarioSesion == null) {
            return avisos;
        }
        if (usuarioSesion.esEstudiante()) {
            Proyecto p = proyectoDAO.proyectoActivoDeEstudiante(usuarioSesion.getId());
            agregarAviso(avisos, p);
        } else if (usuarioSesion.esDocente()) {
            for (Proyecto p : proyectoDAO.listarPorDocente(usuarioSesion.getId(),
                    usuarioSesion.getRol(), "EN_FLUJO", 1)) {
                agregarAviso(avisos, p);
            }
        }
        return avisos;
    }

    private void agregarAviso(List<String> avisos, Proyecto p) {
        if (p == null || p.getFechaVencimiento() == null || p.isFinalizado()) {
            return;
        }
        long dias = p.getDiasRestantes();
        if (dias <= 30 && dias >= 0) {
            avisos.add("El proyecto " + p.getCodigo() + " vence en " + dias
                    + " dia(s) (" + co.edu.grado.util.FechaUtil.formato(p.getFechaVencimiento()) + ").");
        } else if (dias < 0) {
            avisos.add("El proyecto " + p.getCodigo() + " se encuentra vencido.");
        }
    }
}
