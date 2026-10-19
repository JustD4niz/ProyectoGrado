package co.edu.grado.util;

import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Cifrado de contrasenas con PBKDF2WithHmacSHA256 y sal aleatoria por usuario.
 * RNF03: las contrasenas nunca se almacenan en texto plano.
 */
public final class HashUtil {

    private static final int ITERACIONES = 10000;
    private static final int BITS = 512;
    private static final SecureRandom AZAR = new SecureRandom();

    private HashUtil() {
    }

    /** Genera una sal aleatoria de 16 bytes expresada en hexadecimal. */
    public static String generarSal() {
        byte[] bytes = new byte[16];
        AZAR.nextBytes(bytes);
        return aHex(bytes);
    }

    /** Calcula el hash PBKDF2 de la contrasena usando la sal indicada. */
    public static String hash(String contrasena, String salHex) {
        try {
            PBEKeySpec spec = new PBEKeySpec(contrasena.toCharArray(), deHex(salHex), ITERACIONES, BITS);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return aHex(factory.generateSecret(spec).getEncoded());
        } catch (Exception e) {
            throw new IllegalStateException("Error al cifrar la contrasena", e);
        }
    }

    public static boolean verificar(String contrasena, String salHex, String hashEsperado) {
        if (contrasena == null || hashEsperado == null) {
            return false;
        }
        return hash(contrasena, salHex).equalsIgnoreCase(hashEsperado);
    }

    private static String aHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    private static byte[] deHex(String hex) {
        int len = hex.length();
        byte[] datos = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            datos[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return datos;
    }
}
