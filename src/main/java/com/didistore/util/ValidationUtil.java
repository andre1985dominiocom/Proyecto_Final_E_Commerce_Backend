
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
}