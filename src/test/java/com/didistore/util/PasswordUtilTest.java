package com.didistore.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * RF-01 / registro: complejidad de contraseña y BCrypt.
 */
class PasswordUtilTest {

    @Test
    void passwordSeguraValida() {
        assertTrue(PasswordUtil.esSegura("Abcdef12"));
        assertTrue(PasswordUtil.esSegura("Segura#99x"));
        assertTrue(PasswordUtil.esSegura("MiClave2026"));
    }

    @Test
    void passwordSinMayusculaRechazada() {
        assertFalse(PasswordUtil.esSegura("abcdef12"));
    }

    @Test
    void passwordSinMinusculaRechazada() {
        assertFalse(PasswordUtil.esSegura("ABCDEFGH1"));
    }

    @Test
    void passwordSinNumeroRechazada() {
        assertFalse(PasswordUtil.esSegura("Abcdefgh"));
    }

    @Test
    void passwordCortaRechazada() {
        assertFalse(PasswordUtil.esSegura("Ab1defg"));
    }

    @Test
    void passwordNulaOVaciaRechazada() {
        assertFalse(PasswordUtil.esSegura(null));
        assertFalse(PasswordUtil.esSegura(""));
        assertFalse(PasswordUtil.esSegura("   "));
    }

    @Test
    void encryptYVerifyFuncionan() {
        String plain = "ClaveForte1";
        String hash = PasswordUtil.encrypt(plain);

        assertNotNull(hash);
        assertNotEquals(plain, hash);
        assertTrue(hash.startsWith("$2"));
        assertTrue(PasswordUtil.verify(plain, hash));
        assertFalse(PasswordUtil.verify("OtraClave1", hash));
    }

    @Test
    void encryptVacioLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.encrypt(null));
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.encrypt(""));
    }

    @Test
    void verifyConEntradasInvalidasFalso() {
        assertFalse(PasswordUtil.verify(null, "hash"));
        assertFalse(PasswordUtil.verify("clave", null));
        assertFalse(PasswordUtil.verify("clave", "no-hash"));
        assertFalse(PasswordUtil.verify("", ""));
    }
}
