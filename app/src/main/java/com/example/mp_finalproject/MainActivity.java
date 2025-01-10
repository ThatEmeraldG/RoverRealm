package com.example.mp_finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private SharedPreferences modePreferences;
    private boolean nightMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (controller != null) {
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }

        modePreferences = getSharedPreferences("modePreferences", Context.MODE_PRIVATE);
        nightMode = modePreferences.getBoolean("nightMode", false);
        applyNightMode();

        bottomNav = findViewById(R.id.bottomNavigation);
        HomeFragment homeFragment = new HomeFragment();
        ProfileFragment profileFragment = new ProfileFragment();

        if (savedInstanceState == null) {
            setFragment(homeFragment);
        }

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.nav_home) {
                    setFragment(homeFragment);
                    return true;
                } else if(item.getItemId() == R.id.nav_createPost) {
                    Intent intent = new Intent(MainActivity.this, CreatePostActivity.class);
                    startActivity(intent);
                    return true;
                } else if(item.getItemId() == R.id.nav_profile){
                    setFragment(profileFragment);
                    return true;
                }
                return false;
            }
        });
    }

    public void setFragment(Fragment selectedFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (fragmentManager.findFragmentById(R.id.fragmentContainer) != selectedFragment) {
            fragmentTransaction.replace(R.id.fragmentContainer, selectedFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    public void setNightMode(boolean nightMode) {
        this.nightMode = nightMode;
        SharedPreferences.Editor modeEditor = modePreferences.edit();
        modeEditor.putBoolean("nightMode", nightMode);
        modeEditor.apply();
        applyNightMode();
    }

    public boolean getNightMode() {
        return nightMode;
    }

    public void applyNightMode() {
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}