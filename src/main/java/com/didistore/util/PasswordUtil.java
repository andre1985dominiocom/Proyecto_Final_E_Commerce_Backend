
package com.didistore.util;

import org.mindrot.jbcrypt.BCrypt;



/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

public class PasswordUtil {
    
    private static final int WORK_FACTOR = 12;
    private static final String REGEX_COMPLEJIDAD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
    
    public static boolean esSegura(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            return false;
        }
        return plainPassword.matches(REGEX_COMPLEJIDAD);
    }
    
    public static String encrypt(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");          
        }
        String salt = BCrypt.gensalt(WORK_FACTOR);
        return BCrypt.hashpw(plainPassword, salt);
    }
        
        public static boolean verify(String plainPassword, String hashedPassword) {
            if (plainPassword == null || plainPassword.isBlank() || hashedPassword == null || hashedPassword.isBlank()) {
                return false;
            }
            try {
                return BCrypt.checkpw(plainPassword, hashedPassword);
            } catch (IllegalArgumentException e) {
                return false;
        }
    }
}