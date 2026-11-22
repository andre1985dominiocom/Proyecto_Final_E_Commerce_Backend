
package com.didistore.util;

/**
 *
 * @author Sergio Andrés Álvarez Lache
 */
public class ValidationUtil {
    
    public static boolean esPositivo(int valor) {
        return valor > 0;
    }

    public static boolean esPositivo(double valor) {
        return valor > 0;
    }

    public static boolean textoValido(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    public static boolean emailValido(String email) {

        if (email == null) {
            return false;
        }

        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    /**
     * Código postal colombiano: 6 dígitos (ej. 110111, 050001).
     */
    public static boolean codigoPostalValido(String codigo) {
        return codigo != null && codigo.trim().matches("^\\d{6}$");
    }

    /**
     * Validación básica de dirección con formato colombiano (Calle/Carrera/Av + números).
     * Acepta variantes comunes: Calle, Cll, Carrera, Cra, Avenida, Av, Diagonal, Transversal, Km.
     */
    public static boolean direccionColombianaValida(String direccion) {
        if (!textoValido(direccion)) {
            return false;
        }
        String d = direccion.trim();
        if (d.length() < 5 || d.length() > 255) {
            return false;
        }
        // Debe contener al menos un dígito (número de vía/apto) y un prefijo de vía
        return d.matches("(?i)^(calle|carrera|cra|cll|cl|av|avenida|avda|diagonal|dg|transversal|"
                + "trv|tv|km|via|vía|cll\\.|cra\\.)\\s+.*\\d.*");
    }

    /**
     * Barrio: opcional, pero si viene debe ser razonable (2-100 chars).
     */
    public static boolean barrioValido(String barrio) {
        if (barrio == null || barrio.trim().isEmpty()) {
            return true; // opcional
        }
        int len = barrio.trim().length();
        return len >= 2 && len <= 100;
    }
}