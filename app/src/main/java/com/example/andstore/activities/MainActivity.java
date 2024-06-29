package com.example.andstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.andstore.R;
import com.example.andstore.fragments.CartFragment;
import com.example.andstore.fragments.HomeFragment;
import com.example.andstore.fragments.OrderFragment;
import com.example.andstore.fragments.UserProfileFragment;
import com.example.andstore.preferences.CartPreferences;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    private CartPreferences cartPreferences;
    private Fragment currentFragment;
    LinearLayout homeButton;
    LinearLayout profileButton;
    LinearLayout cartButton;
    LinearLayout orderButton;

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
        orderButton = (LinearLayout) findViewById(R.id.nav_order);

        homeButton.setOnClickListener(v -> loadFragment(new HomeFragment()));
        profileButton.setOnClickListener(v -> handleAccountNavigation());
        cartButton.setOnClickListener(v -> loadFragment(new CartFragment()));
        orderButton.setOnClickListener(v -> loadFragment(new OrderFragment()));

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Set up cart notification badge
        cartPreferences = CartPreferences.getInstance(this);
        cartPreferences.getCartItemCount().observe(this, itemCount -> {
            Log.d("MainActivity", "Cart item count changed: " + itemCount);
            updateCartIcon(itemCount);
        });
    }

    private void loadFragment(Fragment fragment) {
        if (currentFragment != null && currentFragment.getClass() == fragment.getClass()) {
            return;
        }

        currentFragment = fragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_place, fragment)
                .commit();
    }

    private void handleAccountNavigation() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        } else {
            loadFragment(new UserProfileFragment());
        }
    }

    private void updateCartIcon(int itemCount) {
        if (itemCount > 0) {
            ImageView notificationCircle = findViewById(R.id.red_notification_circle);
            TextView cartCount = findViewById(R.id.cart_notification_count);
            cartCount.setVisibility(View.VISIBLE);
            notificationCircle.setVisibility(View.VISIBLE);
            cartCount.setText(String.valueOf(itemCount));
        } else {
            TextView cartCount = findViewById(R.id.cart_notification_count);
            ImageView notificationCircle = findViewById(R.id.red_notification_circle);
            notificationCircle.setVisibility(View.GONE);
            cartCount.setVisibility(View.GONE);
        }
    }
}