package co.edu.grado.modelo;

import java.time.LocalDate;

import co.edu.grado.util.FechaUtil;

/** Periodo academico con sus plazos. (RF73, RF74) **/
public class Periodo {

    private int id;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int semanaPagoMax;
    private int semanaPropuestaMax;
    private int diasDuracion;
    private int diasMinInforme;
    private int maxProrrogas;
    private boolean activo;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public int getSemanaPagoMax() { return semanaPagoMax; }
    public void setSemanaPagoMax(int semanaPagoMax) { this.semanaPagoMax = semanaPagoMax; }

    public int getSemanaPropuestaMax() { return semanaPropuestaMax; }
    public void setSemanaPropuestaMax(int semanaPropuestaMax) { this.semanaPropuestaMax = semanaPropuestaMax; }

    public int getDiasDuracion() { return diasDuracion; }
    public void setDiasDuracion(int diasDuracion) { this.diasDuracion = diasDuracion; }

    public int getDiasMinInforme() { return diasMinInforme; }
    public void setDiasMinInforme(int diasMinInforme) { this.diasMinInforme = diasMinInforme; }

    public int getMaxProrrogas() { return maxProrrogas; }
    public void setMaxProrrogas(int maxProrrogas) { this.maxProrrogas = maxProrrogas; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    // Plazos calculados (RF35)
    public LocalDate getFechaMaxPago() {
        return fechaInicio == null ? null : FechaUtil.fechaMaxSemana(fechaInicio, semanaPagoMax);
    }

    public LocalDate getFechaMaxPropuesta() {
        return fechaInicio == null ? null : FechaUtil.fechaMaxSemana(fechaInicio, semanaPropuestaMax);
    }
}
