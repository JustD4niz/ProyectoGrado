package co.edu.grado.modelo;

import java.time.LocalDate;

/** Fecha minima y maxima configurada para una fase dentro de un periodo. (RF74) **/
public class PeriodoFase {

    private int id;
    private int periodoId;
    private String fase;
    private LocalDate fechaMinima;
    private LocalDate fechaMaxima;
    private String responsable;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPeriodoId() { return periodoId; }
    public void setPeriodoId(int periodoId) { this.periodoId = periodoId; }

    public String getFase() { return fase; }
    public void setFase(String fase) { this.fase = fase; }

    public LocalDate getFechaMinima() { return fechaMinima; }
    public void setFechaMinima(LocalDate fechaMinima) { this.fechaMinima = fechaMinima; }

    public LocalDate getFechaMaxima() { return fechaMaxima; }
    public void setFechaMaxima(LocalDate fechaMaxima) { this.fechaMaxima = fechaMaxima; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }
}
