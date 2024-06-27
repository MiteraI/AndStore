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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.andstore.R;
import com.example.andstore.adapters.ShopItemAdapter;
import com.example.andstore.models.ShopItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<ShopItem> shopItemList;
    TextView welcomeText;
    TextView usernameText;

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

        //Init views
        welcomeText = (TextView) findViewById(R.id.welcome_text);
        usernameText = (TextView) findViewById(R.id.username_text);

        //Check if user has logged in, if not show default text
        if (mAuth.getCurrentUser() != null) {
            welcomeText.setText("Welcome");
            usernameText.setText(mAuth.getCurrentUser().getEmail());
        }

        //Banner slider settings
        ArrayList<SlideModel> imageList = new ArrayList<SlideModel>();

        imageList.add(new SlideModel("https://bit.ly/2YoJ77H", "The animal population decreased by 58 percent in 42 years.", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://bit.ly/2BteuF2", "Elephants and tigers may become extinct.", ScaleTypes.FIT));
        imageList.add(new SlideModel("https://bit.ly/3fLJf72", "And people do that.", ScaleTypes.FIT));

        ImageSlider imageSlider = (ImageSlider) findViewById(R.id.banner_slider);
        imageSlider.setImageList(imageList);


        //Set item recycler view
        shopItemList = new ArrayList<ShopItem>();
        recyclerView = (RecyclerView) findViewById(R.id.shop_item_recycler_view);
        layoutManager = new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);

        //Init adapter for recycler view
        adapter = new ShopItemAdapter(shopItemList);

        //Get items from firestore
        fStore.collection("shop-products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    shopItemList.add(new ShopItem(
                            document.getId(),
                            document.getString("productName"),
                            document.getString("productImageUrl"),
                            document.getString("productDesc"),
                            "1",
                            document.getDouble("productPrice"),
                            document.getLong("productStockQuantity").intValue()
                    ));
                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(MainActivity.this, "Error getting documents", Toast.LENGTH_SHORT).show();
                Log.d("Error", "Error getting documents: ", task.getException());
            }
        });
        recyclerView.setAdapter(adapter);

        //Intent to login
        LinearLayout accountNavigation = (LinearLayout) findViewById(R.id.nav_account);
        accountNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                } else {
                    Toast.makeText(MainActivity.this, "You are already logged in, waiting to implement Profile page", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}