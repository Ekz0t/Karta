package com.example.karta;

import static org.junit.Assert.assertEquals;

import com.example.karta.utils.BonusRules;

import org.junit.Test;

public class BonusRulesTest {
    @Test public void thresholds() {
        assertEquals(3, BonusRules.rate(4999));
        assertEquals(5, BonusRules.rate(5000));
        assertEquals(5, BonusRules.rate(14999));
        assertEquals(7, BonusRules.rate(15000));
    }

    @Test public void levelNames() {
        assertEquals("Бронзовый", BonusRules.level(0));
        assertEquals("Серебряный", BonusRules.level(5000));
        assertEquals("Золотой", BonusRules.level(15000));
    }

    @Test public void redemptionLimits() {
        assertEquals(150, BonusRules.maxRedeem(500, 999));
        assertEquals(30, BonusRules.maxRedeem(1000, 30));
        assertEquals(0, BonusRules.maxRedeem(1, 100));
    }

    @Test public void purchaseSequence() {
        int first = BonusRules.earned(1000, 0, 0);
        assertEquals(30, first);
        int second = BonusRules.earned(500, 30, 1000);
        assertEquals(14, second);
        assertEquals(14, first - 30 + second);
    }

    @Test public void crossingThresholdUsesOldRate() {
        assertEquals(150, BonusRules.earned(5000, 0, 0));
        assertEquals(50, BonusRules.earned(1000, 0, 5000));
    }

    @Test public void normalizePhone() {
        assertEquals("79991234567", BonusRules.phone("+7 (999) 123-45-67"));
        assertEquals("79991234567", BonusRules.phone("8 999 1234567"));
        assertEquals("79991234567", BonusRules.phone("9991234567"));
        assertEquals("123456789012", BonusRules.phone("1234 5678 9012"));
    }
}
