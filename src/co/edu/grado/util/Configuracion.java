package co.edu.grado.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Parametros del sistema almacenados en la tabla configuracion.
 * RNF23: las fechas, periodos y plazos son parametrizables sin tocar el codigo.
 */
public final class Configuracion {

    private static final Map<String, String> CACHE = new LinkedHashMap<>();
    private static volatile boolean cargado = false;

    private Configuracion() {
    }

    public static synchronized String get(String clave) {
        asegurar();
        return CACHE.get(clave);
    }

    public static synchronized String get(String clave, String defecto) {
        asegurar();
        String v = CACHE.get(clave);
        return (v == null || v.trim().isEmpty()) ? defecto : v;
    }

    public static synchronized void recargar() {
        cargado = false;
        asegurar();
    }

    private static void asegurar() {
        if (cargado) {
            return;
        }
        CACHE.clear();
        Connection cn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            cn = Conexion.obtener();
            ps = cn.prepareStatement("SELECT clave, valor FROM configuracion");
            rs = ps.executeQuery();
            while (rs.next()) {
                CACHE.put(rs.getString("clave"), rs.getString("valor"));
            }
            cargado = true;
        } catch (Exception e) {
            cargado = true; // evita intentos repetidos si la tabla aun no existe
        } finally {
            Conexion.cerrar(rs, ps, cn);
        }
    }

    public static boolean booleano(String clave, boolean defecto) {
        String v = get(clave);
        if (v == null) {
            return defecto;
        }
        return "true".equalsIgnoreCase(v.trim()) || "1".equals(v.trim());
    }

    public static int entero(String clave, int defecto) {
        try {
            return Integer.parseInt(get(clave));
        } catch (Exception e) {
            return defecto;
        }
    }
}
