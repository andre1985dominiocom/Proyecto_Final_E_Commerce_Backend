
package com.didistore.util;

import org.mindrot.jbcrypt.BCrypt;



/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

// Clase utilitaria para el manejo de contraseñas, incluyendo verificación de complejidad,
// encriptación y verificación de contraseñas encriptadas.
public class PasswordUtil {
    
    // Factor de trabajo para el algoritmo BCrypt, que determina la complejidad del hash generado.
    private static final int WORK_FACTOR = 12;
    private static final String REGEX_COMPLEJIDAD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
    
    // Método para verificar si una contraseña cumple con los criterios de complejidad definidos.
    public static boolean esSegura(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            return false;
        }
        return plainPassword.matches(REGEX_COMPLEJIDAD);
    }
    
    // Método para encriptar una contraseña utilizando el algoritmo BCrypt.
    public static String encrypt(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        String salt = BCrypt.gensalt(WORK_FACTOR);
        return BCrypt.hashpw(plainPassword, salt);
    }
        
        // Método para verificar si una contraseña en texto plano coincide con una contraseña encriptada.
        public static boolean verify(String plainPassword, String hashedPassword) {
            if (plainPassword == null || plainPassword.isBlank() || plainPassword == null || hashedPassword.isBlank()) {
                return false;
            }
            try {
                return BCrypt.checkpw(plainPassword, hashedPassword);
            } catch (IllegalArgumentException e) {
                return false;
        }
    }
}