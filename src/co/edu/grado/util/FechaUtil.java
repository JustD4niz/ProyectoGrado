package co.edu.grado.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Set;

/**
 * Manejo de fechas y plazos con la zona horaria de Colombia.
 * RNF19: los calculos distinguen dias calendario y dias habiles.
 */
public final class FechaUtil {

    public static final ZoneId ZONA = ZoneId.of("America/Bogota");
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private FechaUtil() {
    }

    public static LocalDate hoy() {
        return LocalDate.now(ZONA);
    }

    public static LocalDateTime ahora() {
        return LocalDateTime.now(ZONA);
    }

    public static java.sql.Date sql(LocalDate fecha) {
        return fecha == null ? null : java.sql.Date.valueOf(fecha);
    }

    public static java.sql.Timestamp sql(LocalDateTime fecha) {
        return fecha == null ? null : java.sql.Timestamp.valueOf(fecha);
    }

    public static LocalDate aLocalDate(java.sql.Date fecha) {
        return fecha == null ? null : fecha.toLocalDate();
    }

    public static LocalDate aLocalDate(java.util.Date fecha) {
        return fecha == null ? null
                : fecha.toInstant().atZone(ZONA).toLocalDate();
    }

    public static LocalDateTime aLocalDateTime(java.sql.Timestamp fecha) {
        return fecha == null ? null : fecha.toLocalDateTime();
    }

    /** Suma dias calendario. */
    public static LocalDate sumarDias(LocalDate fecha, int dias) {
        return fecha == null ? null : fecha.plusDays(dias);
    }

    /** Suma dias habiles saltando fines de semana y festivos colombianos. */
    public static LocalDate sumarDiasHabiles(LocalDate fecha, int habiles) {
        if (fecha == null) {
            return null;
        }
        LocalDate resultado = fecha;
        int contados = 0;
        while (contados < habiles) {
            resultado = resultado.plusDays(1);
            if (esHabil(resultado)) {
                contados++;
            }
        }
        return resultado;
    }

    public static boolean esHabil(LocalDate fecha) {
        DayOfWeek dia = fecha.getDayOfWeek();
        if (dia == DayOfWeek.SATURDAY || dia == DayOfWeek.SUNDAY) {
            return false;
        }
        return !festivos(fecha.getYear()).contains(fecha);
    }

    /** Dias entre dos fechas (b - a) en dias calendario. */
    public static long diasEntre(LocalDate a, LocalDate b) {
        return ChronoUnit.DAYS.between(a, b);
    }

    public static long diasTranscurridos(LocalDate desde) {
        return desde == null ? 0 : ChronoUnit.DAYS.between(desde, hoy());
    }

    public static long diasRestantes(LocalDate hasta) {
        return hasta == null ? Long.MAX_VALUE : ChronoUnit.DAYS.between(hoy(), hasta);
    }

    /** Numero de semana del periodo (1..n) para una fecha. */
    public static int semanaDelPeriodo(LocalDate inicio, LocalDate fecha) {
        if (inicio == null || fecha == null) {
            return 0;
        }
        return (int) (ChronoUnit.DAYS.between(inicio, fecha) / 7) + 1;
    }

    /** Ultimo dia de la semana indicada contado desde el inicio del periodo. */
    public static LocalDate fechaMaxSemana(LocalDate inicio, int semana) {
        return inicio.plusDays((long) semana * 7 - 1);
    }

    public static String formato(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FECHA);
    }

    public static String formato(LocalDateTime fecha) {
        return fecha == null ? "" : fecha.format(FECHA_HORA);
    }

    public static String formato(java.util.Date fecha) {
        LocalDate d = aLocalDate(fecha);
        return d == null ? "" : d.format(FECHA);
    }

    /** Festivos colombianos del anio (Ley 51 de 1983 / Ley Emiliani). */
    public static Set<LocalDate> festivos(int anio) {
        Set<LocalDate> f = new HashSet<>();
        f.add(LocalDate.of(anio, 1, 1));
        f.add(siguienteLunes(LocalDate.of(anio, 1, 6)));   // Reyes
        f.add(siguienteLunes(LocalDate.of(anio, 3, 19)));  // San Jose
        f.add(LocalDate.of(anio, 5, 1));                   // Trabajo
        f.add(LocalDate.of(anio, 7, 20));                  // Independencia
        f.add(LocalDate.of(anio, 8, 7));                   // Boyaca
        f.add(siguienteLunes(LocalDate.of(anio, 10, 12))); // Raza
        f.add(siguienteLunes(LocalDate.of(anio, 11, 1)));  // Todos los Santos
        f.add(siguienteLunes(LocalDate.of(anio, 11, 11))); // Independencia Cartagena
        f.add(LocalDate.of(anio, 12, 8));                  // Inmaculada
        f.add(LocalDate.of(anio, 12, 25));                 // Navidad

        LocalDate pascua = pascua(anio);
        f.add(pascua.minusDays(3));                        // Jueves Santo
        f.add(pascua.minusDays(2));                        // Viernes Santo
        f.add(siguienteLunes(pascua.plusDays(43)));        // Ascension
        f.add(siguienteLunes(pascua.plusDays(64)));        // Corpus Christi
        f.add(siguienteLunes(pascua.plusDays(71)));        // Sagrado Corazon
        return f;
    }

    private static LocalDate siguienteLunes(LocalDate fecha) {
        switch (fecha.getDayOfWeek()) {
            case MONDAY: return fecha;
            case TUESDAY: return fecha.plusDays(6);
            case WEDNESDAY: return fecha.plusDays(5);
            case THURSDAY: return fecha.plusDays(4);
            case FRIDAY: return fecha.plusDays(3);
            case SATURDAY: return fecha.plusDays(2);
            default: return fecha.plusDays(1);
        }
    }

    /** Domingo de Pascua (algoritmo de Gauss / Meeus). */
    private static LocalDate pascua(int anio) {
        int a = anio % 19;
        int b = anio / 100;
        int c = anio % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int mes = (h + l - 7 * m + 114) / 31;
        int dia = ((h + l - 7 * m + 114) % 31) + 1;
        return LocalDate.of(anio, mes, dia);
    }
}
