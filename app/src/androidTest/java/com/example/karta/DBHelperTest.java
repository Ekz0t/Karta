package com.example.karta;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.karta.database.DBHelper;
import com.example.karta.models.User;
import com.example.karta.utils.PasswordUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/** Интеграционные проверки DBHelper на устройстве. База создаётся в памяти и не затрагивает данные приложения. */
@RunWith(AndroidJUnit4.class)
public class DBHelperTest {
    private DBHelper db;
    private final String hash = PasswordUtils.hashPassword("123456");

    @Before public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = new DBHelper(context, null);
    }

    @After public void tearDown() {
        db.close();
    }

    @Test public void registerCreatesInactiveCard() {
        User user = db.registerUser("Анна", "79991234567", hash);
        assertNotNull(user);
        assertEquals(12, user.getCardNumber().length());
        assertFalse(user.isCardActive());
        assertEquals(0, user.getBonusBalance());
        assertEquals("Бронзовый", user.getStatus());
        assertNull(db.registerUser("Анна", "79991234567", hash));
    }

    @Test public void loginByPhoneOrCard() {
        User user = db.registerUser("Анна", "79991234567", hash);
        assertEquals(user.getId(), db.loginUser("79991234567", hash).getId());
        assertEquals(user.getId(), db.loginUser(user.getCardNumber(), hash).getId());
        assertNull(db.loginUser("79991234567", PasswordUtils.hashPassword("654321")));
    }

    @Test public void purchasesUpdateBalanceAndActivateCard() {
        int id = db.registerUser("Анна", "79991234567", hash).getId();
        String receipt = db.purchase(id, "Тест", 1000, 0);
        assertTrue(receipt.contains("Начислено: +30 бонусов"));
        User user = db.getUserById(id);
        assertEquals(30, user.getBonusBalance());
        assertTrue(user.isCardActive());
        db.purchase(id, "Тест", 500, 30);
        assertEquals(14, db.getUserById(id).getBonusBalance());
        assertEquals(1470, db.totalPaid(id));
    }

    @Test public void levelChangesFromNextPurchase() {
        int id = db.registerUser("Анна", "79991234567", hash).getId();
        db.purchase(id, "Тест", 5000, 0);
        User user = db.getUserById(id);
        assertEquals(150, user.getBonusBalance());
        assertEquals("Серебряный", user.getStatus());
        db.purchase(id, "Тест", 1000, 0);
        assertEquals(200, db.getUserById(id).getBonusBalance());
    }

    @Test public void invalidPurchaseChangesNothing() {
        int id = db.registerUser("Анна", "79991234567", hash).getId();
        db.purchase(id, "Тест", 1000, 0);
        try {
            db.purchase(id, "Тест", 1000, 31);
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals("Можно списать не больше 30% суммы и доступного баланса", expected.getMessage());
        }
        try {
            db.purchase(id, "Тест", 0, 0);
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals("Сумма: от 1 до 100 000 ₽", expected.getMessage());
        }
        assertEquals(30, db.getUserById(id).getBonusBalance());
        assertEquals(1, db.history(id).size());
    }

    @Test public void historyNewestFirst() {
        int id = db.registerUser("Анна", "79991234567", hash).getId();
        db.purchase(id, "Первая", 500, 0);
        db.purchase(id, "Вторая", 2000, 0);
        List<String> history = db.history(id);
        assertEquals(2, history.size());
        assertTrue(history.get(0).contains("Вторая"));
    }

    @Test public void unknownUserCannotBuy() {
        try {
            db.purchase(999, "Тест", 1000, 0);
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals("Войдите в аккаунт заново", expected.getMessage());
        }
    }

    @Test public void renameUser() {
        int id = db.registerUser("Анна", "79991234567", hash).getId();
        assertTrue(db.updateUserName(id, "Мария"));
        assertEquals("Мария", db.getUserById(id).getName());
    }
}
