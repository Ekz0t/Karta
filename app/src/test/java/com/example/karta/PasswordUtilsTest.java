package com.example.karta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.example.karta.utils.PasswordUtils;

import org.junit.Test;

public class PasswordUtilsTest {
    @Test public void knownSha256() {
        assertEquals("8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92",
                PasswordUtils.hashPassword("123456"));
    }

    @Test public void cyrillicPasswordUsesUtf8() {
        assertEquals("00093d2e9cc78b15340026b0da4cad0a8cef7f7e126c9a24cf0a3618af628063",
                PasswordUtils.hashPassword("пароль1"));
    }

    @Test public void differentPasswordsGiveDifferentHashes() {
        String a = PasswordUtils.hashPassword("123456");
        String b = PasswordUtils.hashPassword("123457");
        assertNotEquals(a, b);
        assertEquals(64, b.length());
    }
}
