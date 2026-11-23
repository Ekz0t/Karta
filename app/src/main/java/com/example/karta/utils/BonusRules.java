package com.example.karta.utils;

/** Все суммы в целых рублях, бонусы округляются вниз. */
public final class BonusRules {
    private BonusRules() {}
    public static int rate(long total) { return total >= 15000 ? 7 : total >= 5000 ? 5 : 3; }
    public static String level(long total) { return total >= 15000 ? "Золотой" : total >= 5000 ? "Серебряный" : "Бронзовый"; }
    public static int maxRedeem(int amount, int balance) { return Math.min(balance, amount * 30 / 100); }
    public static int earned(int amount, int redeemed, long total) { return (amount - redeemed) * rate(total) / 100; }
    public static String phone(String value) {
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.length() == 10) return "7" + digits;
        if (digits.length() == 11 && digits.startsWith("8")) return "7" + digits.substring(1);
        return digits;
    }
}
