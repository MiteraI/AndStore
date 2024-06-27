package com.example.andstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.andstore.R;
import com.example.andstore.fragments.HomeFragment;
import com.example.andstore.fragments.UserProfileFragment;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity implements UserProfileFragment.ProfileFragmentListener {
    private Fragment currentFragment;
    LinearLayout homeButton;
    LinearLayout profileButton;
    LinearLayout cartButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        homeButton = (LinearLayout) findViewById(R.id.nav_home);
        profileButton = (LinearLayout) findViewById(R.id.nav_account);
        cartButton = (LinearLayout) findViewById(R.id.nav_cart);

        homeButton.setOnClickListener(v -> loadFragment(new HomeFragment()));
        profileButton.setOnClickListener(v -> handleAccountNavigation());
        //cartButton.setOnClickListener(v -> loadFragment(new CartFragment()));

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        if (currentFragment != null && currentFragment.getClass() == fragment.getClass()) {
            // If the requested fragment is already displayed, do nothing
            return;
        }

        Log.d("MainActivity", "Loading fragment: " + fragment.getClass().getSimpleName());
        currentFragment = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_place, fragment)
                .commit();
    }

    private void handleAccountNavigation() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // User is not logged in, start LoginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            // User is logged in, load ProfileFragment
            loadFragment(new UserProfileFragment());
        }
    }

    @Override
    public void onSwitchToHomeTab() {
        loadFragment(new HomeFragment());
    }
}