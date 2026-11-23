package com.example.karta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.example.karta.utils.Validator;

import org.junit.Test;

public class ValidatorTest {
    @Test public void emptyFieldsRejected() {
        assertEquals("Заполните все поля", Validator.registrationError("", "79991234567", "123456", "123456"));
        assertEquals("Заполните все поля", Validator.registrationError("Анна", "79991234567", "123456", " "));
    }

    @Test public void longNameRejected() {
        String name = new String(new char[41]).replace('\0', 'а');
        assertEquals("Имя не длиннее 40 символов", Validator.registrationError(name, "79991234567", "123456", "123456"));
        assertNull(Validator.nameError(name.substring(1)));
    }

    @Test public void phoneChecked() {
        assertEquals("Введите корректный номер телефона", Validator.registrationError("Анна", "12345", "123456", "123456"));
        assertNull(Validator.registrationError("Анна", "8 (999) 123-45-67", "123456", "123456"));
    }

    @Test public void passwordsChecked() {
        assertEquals("Пароли не совпадают", Validator.registrationError("Анна", "79991234567", "123456", "123457"));
        assertEquals("Пароль должен быть не короче 6 символов", Validator.registrationError("Анна", "79991234567", "12345", "12345"));
    }

    @Test public void amountBounds() {
        assertEquals("Сумма: от 1 до 100 000 ₽", Validator.amountError(0));
        assertEquals("Сумма: от 1 до 100 000 ₽", Validator.amountError(100001));
        assertNull(Validator.amountError(1));
        assertNull(Validator.amountError(100000));
    }

    @Test public void redeemBounds() {
        assertNull(Validator.redeemError(1000, 300, 500));
        assertEquals("Можно списать не больше 30% суммы и доступного баланса", Validator.redeemError(1000, 301, 500));
        assertEquals("Можно списать не больше 30% суммы и доступного баланса", Validator.redeemError(1000, 31, 30));
        assertEquals("Можно списать не больше 30% суммы и доступного баланса", Validator.redeemError(1000, -1, 100));
    }

    @Test public void parseWholeNumbers() {
        assertEquals(Integer.valueOf(12), Validator.parseWhole("12"));
        assertEquals(Integer.valueOf(7), Validator.parseWhole(" 7 "));
        assertNull(Validator.parseWhole(""));
        assertNull(Validator.parseWhole("-5"));
        assertNull(Validator.parseWhole("1.5"));
        assertNull(Validator.parseWhole("9999999999"));
    }
}
