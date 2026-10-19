package co.edu.grado.control;

import java.util.List;

import co.edu.grado.dao.HistorialDAO;
import co.edu.grado.dao.UsuarioDAO;
import co.edu.grado.modelo.Usuario;
import co.edu.grado.util.ValidacionUtil;

/**
 * Gestion de usuarios por parte del administrador. (RF02 - RF09)
 */
public class UsuarioBean {

    private int id;
    private String cedula;
    private String nombre;
    private String email;
    private String rol;
    private String nivel;
    private String password;
    private boolean activo = true;
    private boolean consentimiento;

    private String filtroRol;
    private String busqueda;
    private int pagina = 1;

    private String mensaje;
    private String error;

    private final UsuarioDAO dao = new UsuarioDAO();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public void setCedula(String cedula) { this.cedula = cedula; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setEmail(String email) { this.email = email; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }
    public String getCedula() { return cedula; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public void setPassword(String password) { this.password = password; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public void setConsentimiento(boolean consentimiento) { this.consentimiento = consentimiento; }
    public void setFiltroRol(String filtroRol) { this.filtroRol = filtroRol; }
    public void setBusqueda(String busqueda) { this.busqueda = busqueda; }
    public String getBusqueda() { return busqueda == null ? "" : busqueda; }
    public String getFiltroRol() { return filtroRol == null ? "" : filtroRol; }
    public int getPagina() { return pagina; }
    public void setPagina(int pagina) { this.pagina = pagina < 1 ? 1 : pagina; }
    public String getMensaje() { return mensaje; }
    public String getError() { return error; }

    public boolean guardar() {
        error = null;
        mensaje = null;
        if (ValidacionUtil.esVacio(nombre) || ValidacionUtil.esVacio(cedula)
                || ValidacionUtil.esVacio(email) || ValidacionUtil.esVacio(rol)) {
            error = "Cedula, nombre, correo y rol son obligatorios.";
            return false;
        }
        if (!ValidacionUtil.esCedula(cedula)) {
            error = "La cedula debe tener entre 6 y 10 digitos.";
            return false;
        }
        if (!ValidacionUtil.esEmail(email)) {
            error = "El correo no tiene un formato valido.";
            return false;
        }
        if ("ESTUDIANTE".equals(rol) && ValidacionUtil.esVacio(nivel)) {
            error = "Debe indicar el nivel del estudiante (Tecnologia o Ingenieria).";
            return false;
        }
        if (dao.existeEmail(email.trim().toLowerCase(), id)) {
            error = "Ya existe un usuario con ese correo.";
            return false;
        }
        Usuario u = new Usuario();
        u.setId(id);
        u.setCedula(cedula.trim());
        u.setNombre(nombre.trim());
        u.setEmail(email.trim().toLowerCase());
        u.setRol(rol);
        u.setNivel("ESTUDIANTE".equals(rol) ? nivel : null);
        u.setActivo(activo);
        u.setConsentimientoDatos(consentimiento || id > 0);
        if (id > 0) {
            u.setConsentimientoDatos(true);
            dao.actualizar(u);
            mensaje = "Usuario actualizado correctamente.";
            new HistorialDAO().registrar(id, "USUARIO", id, "ACTUALIZAR", "Usuario actualizado: " + u.getEmail());
        } else {
            if (ValidacionUtil.esVacio(password) || password.length() < 6) {
                error = "La contraseña inicial debe tener al menos 6 caracteres.";
                return false;
            }
            int nuevoId = dao.crear(u, password);
            mensaje = "Usuario registrado correctamente.";
            new HistorialDAO().registrar(nuevoId, "USUARIO", nuevoId, "CREAR",
                    "Usuario creado: " + u.getEmail() + " (" + u.getRol() + ")");
        }
        return true;
    }

    /** RF05: eliminar usuario validando dependencias (RF06, RNF17). */
    public boolean eliminar() {
        error = null;
        mensaje = null;
        Usuario u = dao.porId(id);
        if (u == null) {
            error = "El usuario no existe.";
            return false;
        }
        if (u.esDocente()) {
            int abiertos = dao.contarProyectosAbiertos(id);
            if (abiertos > 0) {
                error = "No se puede eliminar: el docente tiene " + abiertos
                        + " proyecto(s) abierto(s). Primero reasigne esos proyectos.";
                return false;
            }
        }
        dao.eliminar(id);
        mensaje = "Usuario eliminado correctamente.";
        new HistorialDAO().registrar(null, "USUARIO", id, "ELIMINAR",
                "Usuario eliminado: " + u.getEmail());
        return true;
    }

    public List<Usuario> getLista() {
        return dao.listar(ValidacionUtil.esVacio(filtroRol) ? null : filtroRol, busqueda, pagina);
    }

    public int getTotal() {
        return dao.contar(ValidacionUtil.esVacio(filtroRol) ? null : filtroRol, busqueda);
    }

    public int getTotalPaginas() {
        int total = getTotal();
        return total == 0 ? 1 : (int) Math.ceil(total / (double) UsuarioDAO.TAMANO_PAGINA);
    }

    public List<Usuario> getDocentes() {
        return dao.listarDocentes();
    }
}
