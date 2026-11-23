package com.example.karta.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.karta.R;
import com.example.karta.fragments.LoyaltyFragment;
import com.example.karta.fragments.ProfileFragment;
import com.example.karta.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainRoot), (v, insets) -> { androidx.core.graphics.Insets bars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars()); v.setPadding(bars.left, bars.top, bars.right, bars.bottom); return insets; });

        sessionManager = new SessionManager(this);

        // Защита: если пользователь не авторизован или его запись удалена - выкидываем на экран входа
        com.example.karta.database.DBHelper dbHelper = new com.example.karta.database.DBHelper(this);
        boolean userExists = sessionManager.isLoggedIn() && dbHelper.getUserById(sessionManager.getUserId()) != null;
        dbHelper.close();
        if (!userExists) {
            sessionManager.logout();
            goToLogin();
            return;
        }

        bottomNav = findViewById(R.id.bottomNav);

        // По умолчанию открываем вкладку "Бонусы" (как на референсе дизайна)
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_bonus);
            loadFragment(LoyaltyFragment.page(0));
        }

        bottomNav.setOnItemSelectedListener(this::onNavItemSelected);
    }

    private boolean onNavItemSelected(@NonNull android.view.MenuItem item) {
        Fragment selectedFragment;

        int itemId = item.getItemId();

        if (itemId == R.id.nav_catalog) {
            selectedFragment = LoyaltyFragment.page(1);
        } else if (itemId == R.id.nav_bonus) {
            selectedFragment = LoyaltyFragment.page(0);
        } else if (itemId == R.id.nav_notifications) {
            selectedFragment = LoyaltyFragment.page(2);
        } else if (itemId == R.id.nav_profile) {
            selectedFragment = new ProfileFragment();
        } else {
            return false;
        }

        loadFragment(selectedFragment);
        return true;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

