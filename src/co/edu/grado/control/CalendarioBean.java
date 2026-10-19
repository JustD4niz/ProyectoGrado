package co.edu.grado.control;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import co.edu.grado.dao.PeriodoDAO;
import co.edu.grado.modelo.Periodo;
import co.edu.grado.modelo.PeriodoFase;
import co.edu.grado.util.Etiquetas;
import co.edu.grado.util.FechaUtil;

/**
 * Calendario academico de solo consulta: periodo, fases y fechas clave. (RF73, RF74)
 * Expuesto a las vistas JSP mediante jsp:useBean.
 */
public class CalendarioBean {

    private static final String[] MESES = {
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    };
    private static final String[] DIAS = { "L", "M", "M", "J", "V", "S", "D" };
    private static final int MAX_MESES = 24;

    private final PeriodoDAO dao = new PeriodoDAO();
    private final Etiquetas etiquetas = new Etiquetas();

    private boolean cargado;
    private Periodo periodo;
    private List<PeriodoFase> fases;
    private Map<LocalDate, List<Evento>> eventos;

    private void cargar() {
        if (cargado) {
            return;
        }
        cargado = true;
        periodo = dao.activo();
        fases = periodo == null ? new ArrayList<PeriodoFase>() : dao.fases(periodo.getId());
        eventos = new LinkedHashMap<>();
        if (periodo != null) {
            evento(periodo.getFechaInicio(), "Inicio del periodo academico", "inicio");
            for (PeriodoFase f : fases) {
                evento(f.getFechaMinima(), "Inicia " + etiquetas.fase(f.getFase()), "fase");
                evento(f.getFechaMaxima(), "Cierra " + etiquetas.fase(f.getFase()), "fase");
            }
            evento(periodo.getFechaFin(), "Fin del periodo academico", "fin");
        }
    }

    private void evento(LocalDate fecha, String etiqueta, String tipo) {
        if (fecha == null) {
            return;
        }
        List<Evento> lista = eventos.get(fecha);
        if (lista == null) {
            lista = new ArrayList<>();
            eventos.put(fecha, lista);
        }
        lista.add(new Evento(etiqueta, tipo));
    }

    public String[] getDias() {
        cargar();
        return DIAS;
    }

    public Periodo getPeriodoActivo() {
        cargar();
        return periodo;
    }

    public List<PeriodoFase> getFases() {
        cargar();
        return fases;
    }

    public String getInicioTexto() {
        cargar();
        return periodo == null ? "" : FechaUtil.formato(periodo.getFechaInicio());
    }

    public String getFinTexto() {
        cargar();
        return periodo == null ? "" : FechaUtil.formato(periodo.getFechaFin());
    }

    /** Fechas clave del proceso (dia, anio, fecha completa, etiqueta y tipo). */
    public List<Map<String, String>> getFechasClave() {
        cargar();
        List<Map<String, String>> lista = new ArrayList<>();
        if (periodo == null) {
            return lista;
        }
        LocalDate[] conEventos = aFechasOrdenadas();
        for (LocalDate d : conEventos) {
            for (Evento e : eventos.get(d)) {
                Map<String, String> m = new LinkedHashMap<>();
                m.put("dia", String.format("%02d/%02d", d.getDayOfMonth(), d.getMonthValue()));
                m.put("anio", String.valueOf(d.getYear()));
                m.put("fecha", FechaUtil.formato(d));
                m.put("etiqueta", e.getEtiqueta());
                m.put("tipo", e.getTipo());
                lista.add(m);
            }
        }
        return lista;
    }

    /** Meses que cubren el periodo y todas las fechas de sus fases. */
    public List<Mes> getMeses() {
        cargar();
        List<Mes> meses = new ArrayList<>();
        if (periodo == null) {
            return meses;
        }
        LocalDate[] rango = rango();
        if (rango == null) {
            return meses;
        }
        LocalDate hoy = FechaUtil.hoy();
        YearMonth fin = YearMonth.from(rango[1]);
        YearMonth ym = YearMonth.from(rango[0]);
        int guard = 0;
        while (!ym.isAfter(fin) && guard < MAX_MESES) {
            meses.add(construirMes(ym, hoy));
            ym = ym.plusMonths(1);
            guard++;
        }
        return meses;
    }

    private LocalDate[] rango() {
        LocalDate desde = periodo.getFechaInicio();
        LocalDate hasta = periodo.getFechaFin();
        for (LocalDate d : eventos.keySet()) {
            if (desde == null || d.isBefore(desde)) {
                desde = d;
            }
            if (hasta == null || d.isAfter(hasta)) {
                hasta = d;
            }
        }
        if (desde == null || hasta == null) {
            return null;
        }
        return new LocalDate[] { desde, hasta };
    }

    private LocalDate[] aFechasOrdenadas() {
        List<LocalDate> fechas = new ArrayList<>(eventos.keySet());
        java.util.Collections.sort(fechas);
        return fechas.toArray(new LocalDate[0]);
    }

    private Mes construirMes(YearMonth ym, LocalDate hoy) {
        List<List<Dia>> semanas = new ArrayList<>();
        LocalDate primero = ym.atDay(1);
        int offset = primero.getDayOfWeek().getValue() - 1;
        int largo = ym.lengthOfMonth();
        int numSemanas = (int) Math.ceil((offset + largo) / 7.0);
        LocalDate cursor = primero.minusDays(offset);
        for (int s = 0; s < numSemanas; s++) {
            List<Dia> semana = new ArrayList<>();
            for (int d = 0; d < 7; d++) {
                boolean enMes = YearMonth.from(cursor).equals(ym);
                List<Evento> lista = eventos.get(cursor);
                semana.add(new Dia(cursor.getDayOfMonth(), enMes, cursor.equals(hoy),
                        lista == null ? new ArrayList<Evento>() : lista));
                cursor = cursor.plusDays(1);
            }
            semanas.add(semana);
        }
        String titulo = MESES[ym.getMonthValue() - 1] + " " + ym.getYear();
        return new Mes(titulo, semanas);
    }

    /** Evento del calendario. */
    public static class Evento {
        private final String etiqueta;
        private final String tipo;

        public Evento(String etiqueta, String tipo) {
            this.etiqueta = etiqueta;
            this.tipo = tipo;
        }

        public String getEtiqueta() { return etiqueta; }
        public String getTipo() { return tipo; }
    }

    /** Un dia del calendario. */
    public static class Dia {
        private final int dia;
        private final boolean enMes;
        private final boolean hoy;
        private final List<Evento> eventos;

        public Dia(int dia, boolean enMes, boolean hoy, List<Evento> eventos) {
            this.dia = dia;
            this.enMes = enMes;
            this.hoy = hoy;
            this.eventos = eventos;
        }

        public int getDia() { return dia; }
        public boolean isEnMes() { return enMes; }
        public boolean isHoy() { return hoy; }
        public List<Evento> getEventos() { return eventos; }
        public boolean isTieneEvento() { return !eventos.isEmpty(); }

        public String getTitulo() {
            StringBuilder sb = new StringBuilder();
            for (Evento e : eventos) {
                if (sb.length() > 0) {
                    sb.append(" · ");
                }
                sb.append(e.getEtiqueta());
            }
            return sb.toString();
        }
    }

    /** Un mes del calendario con sus semanas. */
    public static class Mes {
        private final String titulo;
        private final List<List<Dia>> semanas;

        public Mes(String titulo, List<List<Dia>> semanas) {
            this.titulo = titulo;
            this.semanas = semanas;
        }

        public String getTitulo() { return titulo; }
        public List<List<Dia>> getSemanas() { return semanas; }
    }
}
