package co.edu.grado.control;

import javax.servlet.http.HttpSession;

import co.edu.grado.dao.HistorialDAO;
import co.edu.grado.modelo.Usuario;

/** Cierre de sesion sin scriptlets. **/
public class SesionBean {

    private HttpSession sesion;

    public void setSesion(HttpSession sesion) { this.sesion = sesion; }

    public boolean cerrar() {
        if (sesion != null) {
            Object u = sesion.getAttribute("usuarioSesion");
            if (u instanceof Usuario) {
                Usuario usuario = (Usuario) u;
                new HistorialDAO().registrar(usuario.getId(), "USUARIO", usuario.getId(),
                        "CIERRE_SESION", "Salida del sistema: " + usuario.getEmail());
            }
            sesion.invalidate();
        }
        return true;
    }
}
