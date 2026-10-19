package co.edu.grado.util;

import java.util.regex.Pattern;

/**
 * Validaciones de formato reutilizables. RNF18: validacion en cliente y servidor,
 * RNF06: validacion de URL antes de guardar.
 */
public final class ValidacionUtil {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern CEDULA = Pattern.compile("^[0-9]{6,10}$");
    private static final Pattern URL = Pattern.compile("^https?://[^\\s]+$", Pattern.CASE_INSENSITIVE);

    private ValidacionUtil() {
    }

    public static boolean esVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static boolean esEmail(String valor) {
        return valor != null && EMAIL.matcher(valor.trim()).matches();
    }

    public static boolean esCedula(String valor) {
        return valor != null && CEDULA.matcher(valor.trim()).matches();
    }

    /** RNF06: valida que la URL tenga un formato valido (no se almacenan archivos). */
    public static boolean esUrl(String valor) {
        return valor != null && URL.matcher(valor.trim()).matches();
    }

    public static boolean longitud(String valor, int min, int max) {
        if (valor == null) {
            return false;
        }
        int n = valor.trim().length();
        return n >= min && n <= max;
    }

    /** Limpia el texto basicamente para evitar inyeccion de etiquetas en la salida. */
    public static String limpiar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
