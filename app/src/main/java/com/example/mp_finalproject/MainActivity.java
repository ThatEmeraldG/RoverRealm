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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mp_finalproject.adapter.TagAdapter;
import com.example.mp_finalproject.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private SharedPreferences preferences;
    private ActivityMainBinding binding;
    private TagAdapter tagAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        List<String> tags = Arrays.asList("Carlotta", "Build Guide", "Must Pull", "High DMG");
        recyclerView = binding.tagsrecyclerView;
        tagAdapter = new TagAdapter(tags);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(tagAdapter);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = ViewCompat.getWindowInsetsController(getWindow().getDecorView());
        if (controller != null) {
            controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }

        preferences = getSharedPreferences("modePreferences", Context.MODE_PRIVATE);
        boolean nightMode = preferences.getBoolean("nightMode", false);
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

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

}