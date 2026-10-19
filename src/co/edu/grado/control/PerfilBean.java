package co.edu.grado.control;

import co.edu.grado.dao.HistorialDAO;
import co.edu.grado.dao.UsuarioDAO;
import co.edu.grado.modelo.Usuario;
import co.edu.grado.util.HashUtil;
import co.edu.grado.util.ValidacionUtil;

/** Perfil del usuario autenticado: datos basicos y cambio de contrasena. (RF09, RNF03) */
public class PerfilBean {

    private Usuario usuarioSesion;
    private String nombre;
    private String email;
    private String telefono;
    private String passwordActual;
    private String passwordNueva;
    private String passwordConfirmar;
    private String mensaje;
    private String error;

    private final UsuarioDAO dao = new UsuarioDAO();

    public void setUsuarioSesion(Usuario u) { this.usuarioSesion = u; }
    public Usuario getUsuarioSesion() { return usuarioSesion; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setPasswordActual(String v) { this.passwordActual = v; }
    public void setPasswordNueva(String v) { this.passwordNueva = v; }
    public void setPasswordConfirmar(String v) { this.passwordConfirmar = v; }
    public String getMensaje() { return mensaje; }
    public String getError() { return error; }

    public boolean actualizarDatos() {
        error = null;
        mensaje = null;
        if (usuarioSesion == null) {
            error = "Sesión no válida.";
            return false;
        }
        if (ValidacionUtil.esVacio(nombre) || ValidacionUtil.esVacio(email)) {
            error = "El nombre y el correo son obligatorios.";
            return false;
        }
        if (!ValidacionUtil.esEmail(email)) {
            error = "El correo no tiene un formato valido.";
            return false;
        }
        if (dao.existeEmail(email.trim().toLowerCase(), usuarioSesion.getId())) {
            error = "Ese correo ya está registrado por otro usuario.";
            return false;
        }
        usuarioSesion.setNombre(nombre.trim());
        usuarioSesion.setEmail(email.trim().toLowerCase());
        dao.actualizar(usuarioSesion);
        mensaje = "Datos actualizados correctamente.";
        new HistorialDAO().registrar(usuarioSesion.getId(), "USUARIO", usuarioSesion.getId(),
                "PERFIL", "Actualizó sus datos de perfil");
        return true;
    }

    public boolean cambiarPassword() {
        error = null;
        mensaje = null;
        if (usuarioSesion == null) {
            error = "Sesión no válida.";
            return false;
        }
        if (!HashUtil.verificar(passwordActual, usuarioSesion.getPasswordSalt(),
                usuarioSesion.getPasswordHash())) {
            error = "La contraseña actual no es correcta.";
            return false;
        }
        if (ValidacionUtil.esVacio(passwordNueva) || passwordNueva.length() < 6) {
            error = "La nueva contraseña debe tener al menos 6 caracteres.";
            return false;
        }
        if (!passwordNueva.equals(passwordConfirmar)) {
            error = "La confirmación no coincide con la nueva contraseña.";
            return false;
        }
        String sal = HashUtil.generarSal();
        String hash = HashUtil.hash(passwordNueva, sal);
        dao.actualizarPassword(usuarioSesion.getId(), passwordNueva);
        usuarioSesion.setPasswordSalt(sal);
        usuarioSesion.setPasswordHash(hash);
        mensaje = "Contraseña actualizada correctamente.";
        new HistorialDAO().registrar(usuarioSesion.getId(), "USUARIO", usuarioSesion.getId(),
                "CAMBIO_PASSWORD", "Actualizó su contraseña");
        return true;
    }
}
