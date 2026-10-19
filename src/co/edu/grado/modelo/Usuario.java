package co.edu.grado.modelo;

import java.time.LocalDateTime;

/** Usuario del sistema (cualquier rol). **/
public class Usuario {

    private int id;
    private String cedula;
    private String nombre;
    private String email;
    private String passwordHash;
    private String passwordSalt;
    private String rol;       // ADMIN, COMITE, DOCENTE, DIRECTOR, EVALUADOR, ESTUDIANTE
    private String nivel;     // TECNOLOGIA, INGENIERIA (solo estudiantes)
    private boolean activo;
    private boolean consentimientoDatos;
    private LocalDateTime fechaRegistro;

    // Campos de apoyo para listados
    private int cargaProyectos;
    private boolean tieneProyectosAbiertos;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getPasswordSalt() { return passwordSalt; }
    public void setPasswordSalt(String passwordSalt) { this.passwordSalt = passwordSalt; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean isConsentimientoDatos() { return consentimientoDatos; }
    public void setConsentimientoDatos(boolean consentimientoDatos) { this.consentimientoDatos = consentimientoDatos; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public int getCargaProyectos() { return cargaProyectos; }
    public void setCargaProyectos(int cargaProyectos) { this.cargaProyectos = cargaProyectos; }

    public boolean isTieneProyectosAbiertos() { return tieneProyectosAbiertos; }
    public void setTieneProyectosAbiertos(boolean tieneProyectosAbiertos) { this.tieneProyectosAbiertos = tieneProyectosAbiertos; }

    public boolean esDocente() {
        return "DOCENTE".equals(rol) || "DIRECTOR".equals(rol) || "EVALUADOR".equals(rol);
    }

    /** Un docente puede ser director y evaluador de distintos proyectos. */
    public boolean esDirectorOEvaluador() {
        return esDocente();
    }

    public boolean esEstudiante() {
        return "ESTUDIANTE".equals(rol);
    }

    public String getIniciales() {
        if (nombre == null || nombre.trim().isEmpty()) {
            return "?";
        }
        String[] partes = nombre.trim().split("\\s+");
        String iniciales = partes[0].substring(0, 1);
        if (partes.length > 1) {
            iniciales += partes[1].substring(0, 1);
        }
        return iniciales.toUpperCase();
    }
}
