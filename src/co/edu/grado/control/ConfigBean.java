package co.edu.grado.control;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.edu.grado.dao.HistorialDAO;
import co.edu.grado.dao.PeriodoDAO;
import co.edu.grado.modelo.Periodo;
import co.edu.grado.modelo.PeriodoFase;
import co.edu.grado.modelo.Usuario;
import co.edu.grado.util.Configuracion;
import co.edu.grado.util.ValidacionUtil;

/**
 * Configuracion del periodo academico, calendario por fase y base documental.
 * (RF73 - RF76, RNF23)
 */
public class ConfigBean {

    private Usuario usuarioSesion;

    private String nombre;
    private String fechaInicio;
    private String fechaFin;
    private int semanaPagoMax = 8;
    private int semanaPropuestaMax = 12;
    private int diasDuracion = 180;
    private int diasMinInforme = 90;
    private int maxProrrogas = 2;

    private String faseSeleccionada;
    private String faseMin;
    private String faseMax;
    private String faseResponsable;

    private String claveConfig;
    private String valorConfig;

    private String mensaje;
    private String error;

    private final PeriodoDAO dao = new PeriodoDAO();

    public void setUsuarioSesion(Usuario u) { this.usuarioSesion = u; }
    public void setNombre(String v) { this.nombre = v; }
    public void setFechaInicio(String v) { this.fechaInicio = v; }
    public void setFechaFin(String v) { this.fechaFin = v; }
    public void setSemanaPagoMax(int v) { this.semanaPagoMax = v; }
    public void setSemanaPropuestaMax(int v) { this.semanaPropuestaMax = v; }
    public void setDiasDuracion(int v) { this.diasDuracion = v; }
    public void setDiasMinInforme(int v) { this.diasMinInforme = v; }
    public void setMaxProrrogas(int v) { this.maxProrrogas = v; }
    public void setFaseSeleccionada(String v) { this.faseSeleccionada = v; }
    public void setFaseMin(String v) { this.faseMin = v; }
    public void setFaseMax(String v) { this.faseMax = v; }
    public void setFaseResponsable(String v) { this.faseResponsable = v; }
    public void setClaveConfig(String v) { this.claveConfig = v; }
    public void setValorConfig(String v) { this.valorConfig = v; }
    public String getMensaje() { return mensaje; }
    public String getError() { return error; }

    /** RF73: crea/actualiza el periodo y sus plazos. */
    public boolean guardarPeriodo() {
        error = null;
        mensaje = null;
        if (ValidacionUtil.esVacio(nombre) || ValidacionUtil.esVacio(fechaInicio) || ValidacionUtil.esVacio(fechaFin)) {
            error = "El nombre y las fechas de inicio y fin son obligatorios.";
            return false;
        }
        try {
            Periodo p = new Periodo();
            p.setNombre(nombre.trim());
            p.setFechaInicio(LocalDate.parse(fechaInicio));
            p.setFechaFin(LocalDate.parse(fechaFin));
            p.setSemanaPagoMax(semanaPagoMax);
            p.setSemanaPropuestaMax(semanaPropuestaMax);
            p.setDiasDuracion(diasDuracion);
            p.setDiasMinInforme(diasMinInforme);
            p.setMaxProrrogas(maxProrrogas);
            p.setActivo(true);
            int id = dao.guardar(p);
            // RF74: calendario por defecto segun los plazos
            dao.guardarFase(id, "PAGO", p.getFechaInicio(), p.getFechaMaxPago(), "COMITE");
            dao.guardarFase(id, "PROPUESTA_I", p.getFechaMaxPago().plusDays(1), p.getFechaMaxPropuesta(), "COMITE");
            mensaje = "Periodo academico " + nombre + " configurado y activado.";
            new HistorialDAO().registrar(id, "PERIODO", id, "GUARDAR", "Periodo " + nombre + " configurado");
            return true;
        } catch (Exception e) {
            error = "Verifique el formato de las fechas (AAAA-MM-DD).";
            return false;
        }
    }

    /** RF74: guarda las fechas minimas y maximas de una fase. */
    public boolean guardarFase() {
        error = null;
        mensaje = null;
        Periodo p = dao.activo();
        if (p == null) {
            error = "No hay un periodo activo.";
            return false;
        }
        if (ValidacionUtil.esVacio(faseSeleccionada)) {
            error = "Seleccione la fase.";
            return false;
        }
        try {
            LocalDate min = ValidacionUtil.esVacio(faseMin) ? null : LocalDate.parse(faseMin);
            LocalDate max = ValidacionUtil.esVacio(faseMax) ? null : LocalDate.parse(faseMax);
            dao.guardarFase(p.getId(), faseSeleccionada, min, max, faseResponsable);
            mensaje = "Calendario actualizado para la fase seleccionada.";
            new HistorialDAO().registrar(usuarioSesion == null ? null : usuarioSesion.getId(),
                    "PERIODO", p.getId(), "CALENDARIO", "Fase " + faseSeleccionada + " " + faseMin + " - " + faseMax);
            return true;
        } catch (Exception e) {
            error = "Verifique el formato de las fechas (AAAA-MM-DD).";
            return false;
        }
    }

    /** RF75, RF76: actualiza un enlace de la base documental. */
    public boolean guardarConfig() {
        error = null;
        mensaje = null;
        if (ValidacionUtil.esVacio(claveConfig) || ValidacionUtil.esVacio(valorConfig)) {
            error = "Clave y valor son obligatorios.";
            return false;
        }
        if (claveConfig.startsWith("documental.") && !ValidacionUtil.esUrl(valorConfig)) {
            error = "El enlace debe ser una URL valida.";
            return false;
        }
        try {
            java.sql.Connection cn = co.edu.grado.util.Conexion.obtener();
            java.sql.PreparedStatement ps = cn.prepareStatement(
                    "UPDATE configuracion SET valor=? WHERE clave=?");
            ps.setString(1, valorConfig.trim());
            ps.setString(2, claveConfig);
            ps.executeUpdate();
            co.edu.grado.util.Conexion.cerrar(null, ps, cn);
            Configuracion.recargar();
            mensaje = "Configuración actualizada.";
            return true;
        } catch (Exception e) {
            error = "No se pudo actualizar la configuración.";
            return false;
        }
    }

    public Periodo getPeriodoActivo() {
        return dao.activo();
    }

    public List<Periodo> getPeriodos() {
        return dao.listar();
    }

    public List<PeriodoFase> getFases() {
        Periodo p = dao.activo();
        return p == null ? new ArrayList<PeriodoFase>() : dao.fases(p.getId());
    }

    /** RF75, RF76: enlaces de la base documental tomados de la configuracion. */
    public List<Map<String, String>> getDocumental() {
        List<Map<String, String>> lista = new ArrayList<>();
        agregar(lista, "Reglamento de trabajo de grado", Configuracion.get("documental.reglamento"));
        agregar(lista, "Rejilla institucional de evaluación", Configuracion.get("documental.rejilla"));
        agregar(lista, "F-DC 124 - Formato de propuesta", Configuracion.get("documental.fdc124"));
        agregar(lista, "F-DC 125 - Formato de informe final", Configuracion.get("documental.fdc125"));
        agregar(lista, "Reglamento de estimulos", Configuracion.get("documental.estinculos"));
        return lista;
    }

    private void agregar(List<Map<String, String>> lista, String etiqueta, String url) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("etiqueta", etiqueta);
        m.put("url", url == null || url.trim().isEmpty() ? "#" : url);
        m.put("clave", claveDocumental(etiqueta));
        lista.add(m);
    }

    private String claveDocumental(String etiqueta) {
        if (etiqueta.startsWith("F-DC 124")) {
            return "documental.fdc124";
        }
        if (etiqueta.startsWith("F-DC 125")) {
            return "documental.fdc125";
        }
        if (etiqueta.contains("Rejilla")) {
            return "documental.rejilla";
        }
        if (etiqueta.contains("trabajo de grado")) {
            return "documental.reglamento";
        }
        return "documental.estinculos";
    }
}
