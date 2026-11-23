package com.example.karta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.karta.utils.Formatters;

import org.junit.Test;

public class FormattersTest {
    @Test public void phoneFormatted() {
        assertEquals("+7 (999) 123-45-67", Formatters.phone("79991234567"));
        assertEquals("123", Formatters.phone("123"));
        assertNull(Formatters.phone(null));
    }

    @Test public void cardFormatted() {
        assertEquals("1234 5678 9012", Formatters.card("123456789012"));
        assertEquals("123", Formatters.card("123"));
    }
}
