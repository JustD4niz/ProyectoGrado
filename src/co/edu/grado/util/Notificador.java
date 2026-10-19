package co.edu.grado.util;

import co.edu.grado.dao.NotificacionDAO;

/**
 * Helper para registrar notificaciones en pantalla desde los beans de control.
 * Centraliza el acceso a NotificacionDAO. (RF55 - RF60)
 */
public class Notificador {

    private final NotificacionDAO dao = new NotificacionDAO();

    public void avisar(int usuarioId, String mensaje, String enlace) {
        dao.crear(usuarioId, mensaje, enlace);
    }
}
