package co.edu.grado.control;

import javax.servlet.http.HttpSession;

import co.edu.grado.dao.HistorialDAO;
import co.edu.grado.dao.UsuarioDAO;
import co.edu.grado.modelo.Usuario;
import co.edu.grado.util.HashUtil;
import co.edu.grado.util.ValidacionUtil;

/**
 * Autenticacion de usuarios. (RF01, RNF01)
 * Se usa desde login.jsp / acciones/login.jsp mediante jsp:useBean.
 */
public class LoginBean {

    private String email;
    private String password;
    private String error;
    private Usuario usuario;
    private HttpSession sesion;

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public String getError() { return error; }
    public Usuario getUsuario() { return usuario; }
    public void setSesion(HttpSession sesion) { this.sesion = sesion; }

    public boolean iniciarSesion() {
        error = null;
        if (ValidacionUtil.esVacio(email) || ValidacionUtil.esVacio(password)) {
            error = "Debe ingresar el correo y la contrasena.";
            return false;
        }
        UsuarioDAO dao = new UsuarioDAO();
        Usuario u = dao.porEmail(email.trim().toLowerCase());
        if (u == null) {
            error = "El correo no esta registrado en el sistema.";
            return false;
        }
        if (!u.isActivo()) {
            error = "El usuario se encuentra inactivo. Contacte al administrador.";
            return false;
        }
        if (!HashUtil.verificar(password, u.getPasswordSalt(), u.getPasswordHash())) {
            error = "La contrasena es incorrecta.";
            return false;
        }
        usuario = u;
        if (sesion != null) {
            sesion.setAttribute("usuarioSesion", u);
        }
        new HistorialDAO().registrar(u.getId(), "USUARIO", u.getId(), "INICIO_SESION",
                "Ingreso al sistema: " + u.getEmail());
        return true;
    }
}
