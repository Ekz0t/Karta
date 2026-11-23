package com.example.karta.utils;

/** Форматирование номеров для отображения на экране. */
public final class Formatters {
    private Formatters() {}

    /** 79991234567 → +7 (999) 123-45-67 */
    public static String phone(String phone) {
        if (phone == null || phone.length() < 10) return phone;
        String last10 = phone.substring(phone.length() - 10);
        return "+7 (" + last10.substring(0, 3) + ") " + last10.substring(3, 6) + "-"
                + last10.substring(6, 8) + "-" + last10.substring(8, 10);
    }

    /** 123456789012 → 1234 5678 9012 */
    public static String card(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 12) return cardNumber;
        return cardNumber.substring(0, 4) + " " + cardNumber.substring(4, 8) + " " + cardNumber.substring(8, 12);
    }
}
