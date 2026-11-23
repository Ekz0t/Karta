package com.example.karta.utils;

/** Проверка данных, вводимых пользователем. Возвращает текст ошибки или null. */
public final class Validator {
    public static final int MAX_NAME = 40;
    public static final int MIN_PASSWORD = 6;
    public static final int MAX_AMOUNT = 100000;

    private Validator() {}

    public static String nameError(String name) {
        if (name == null || name.trim().isEmpty()) return "Введите имя";
        if (name.trim().length() > MAX_NAME) return "Имя не длиннее " + MAX_NAME + " символов";
        return null;
    }

    public static String registrationError(String name, String phone, String password, String confirm) {
        if (isEmpty(name) || isEmpty(phone) || isEmpty(password) || isEmpty(confirm)) return "Заполните все поля";
        String nameError = nameError(name);
        if (nameError != null) return nameError;
        String digits = BonusRules.phone(phone);
        if (digits.length() != 11 || !digits.startsWith("7")) return "Введите корректный номер телефона";
        if (!password.equals(confirm)) return "Пароли не совпадают";
        if (password.length() < MIN_PASSWORD) return "Пароль должен быть не короче " + MIN_PASSWORD + " символов";
        return null;
    }

    public static String amountError(int amount) {
        return amount < 1 || amount > MAX_AMOUNT ? "Сумма: от 1 до 100 000 ₽" : null;
    }

    public static String redeemError(int amount, int redeemed, int balance) {
        if (redeemed < 0 || redeemed > BonusRules.maxRedeem(amount, balance))
            return "Можно списать не больше 30% суммы и доступного баланса";
        return null;
    }

    /** Целое неотрицательное число из поля ввода или null, если ввод некорректен. */
    public static Integer parseWhole(String text) {
        if (text == null) return null;
        String value = text.trim();
        if (value.isEmpty() || value.length() > 9 || !value.matches("[0-9]+")) return null;
        return Integer.parseInt(value);
    }

    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
