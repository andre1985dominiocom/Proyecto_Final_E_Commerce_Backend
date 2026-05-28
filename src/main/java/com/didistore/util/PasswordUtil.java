
package com.didistore.util;

import org.mindrot.jbcrypt.BCrypt;



/**
 *
 * @author Sergio Andrés Álvarez Lache
 */

public class PasswordUtil {
    
    private static final int WORK_FACTOR = 12;
    
    public static String encrypt(String plainPassword) {
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");          
        }
        String salt = BCrypt.gensalt(WORK_FACTOR);
        return BCrypt.hashpw(plainPassword, salt);
    }
        
        public static boolean verify(String plainPassword, String hashePassword) {
            if (plainPassword == null || hashePassword == null || hashePassword.isBlank()) {
                return false;
            }
            try {
                return BCrypt.checkpw(plainPassword, hashePassword);
            } catch (IllegalArgumentException e) {
                return false;
        }
    }
}