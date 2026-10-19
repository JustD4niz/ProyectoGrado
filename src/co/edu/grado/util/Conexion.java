package co.edu.grado.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * Fabrica de conexiones. Usa el DataSource JNDI definido en META-INF/context.xml
 * y, si no esta disponible, cae en DriverManager con los datos de db.properties.
 * (RNF22: base de datos relacional)
 */
public final class Conexion {

    private static DataSource dataSource;
    private static final Properties props = new Properties();
    private static final String JNDI = "java:comp/env/jdbc/trabajoGrado";

    static {
        try (InputStream in = Conexion.class.getResourceAsStream("/db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception ignored) {
            // El pool JNDI sigue siendo la via principal
        }
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup(JNDI);
        } catch (Exception e) {
            dataSource = null;
        }
    }

    private Conexion() {
    }

    public static Connection obtener() throws SQLException {
        if (dataSource != null) {
            return dataSource.getConnection();
        }
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontro el controlador de PostgreSQL", e);
        }
        String url = props.getProperty("url",
                "jdbc:postgresql://localhost:5432/trabajo_grado?sslmode=require&prepareThreshold=0");
        String usuario = props.getProperty("usuario", "postgres");
        String clave = props.getProperty("clave", "");
        return DriverManager.getConnection(url, usuario, clave);
    }

    /** Cierra de forma silenciosa los recursos JDBC abiertos. */
    public static void cerrar(ResultSet rs, PreparedStatement ps, Connection cn) {
        if (rs != null) {
            try { rs.close(); } catch (Exception ignored) { }
        }
        if (ps != null) {
            try { ps.close(); } catch (Exception ignored) { }
        }
        if (cn != null) {
            try { cn.close(); } catch (Exception ignored) { }
        }
    }
}
