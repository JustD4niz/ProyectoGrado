package co.edu.grado.modelo;

import java.time.LocalDateTime;

/** Integrante de un proyecto de grado. (RF21, RF53, RF54) **/
public class Integrante {

    private int id;
    private int proyectoId;
    private int estudianteId;
    private String estado;      // ACTIVO, RETIRADO
    private LocalDateTime fechaUnion;
    private LocalDateTime fechaRetiro;

    // Datos del estudiante para la vista
    private String nombre;
    private String cedula;
    private String email;
    private String nivel;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public int getEstudianteId() { return estudianteId; }
    public void setEstudianteId(int estudianteId) { this.estudianteId = estudianteId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaUnion() { return fechaUnion; }
    public void setFechaUnion(LocalDateTime fechaUnion) { this.fechaUnion = fechaUnion; }

    public LocalDateTime getFechaRetiro() { return fechaRetiro; }
    public void setFechaRetiro(LocalDateTime fechaRetiro) { this.fechaRetiro = fechaRetiro; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public boolean isActivo() {
        return "ACTIVO".equals(estado);
    }
}
