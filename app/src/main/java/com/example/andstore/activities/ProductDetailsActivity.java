package com.example.andstore.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.andstore.R;
import com.example.andstore.models.ShopItem;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductDetailsActivity extends AppCompatActivity {
    FirebaseFirestore db;
    String productId;
    ShopItem currentItem = new ShopItem();
    ImageView backButton;
    ImageView productImage;
    TextView productName;
    TextView productPrice;
    TextView productDescription;
    TextView productStockQuantity;
    TextView productQuantity;
    ImageButton buttonMinus;
    ImageButton buttonPlus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.product_details_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Init views
        backButton = findViewById(R.id.back_button);
        productImage = findViewById(R.id.product_image);
        productName = findViewById(R.id.product_name);
        productPrice = findViewById(R.id.product_price);
        productDescription = findViewById(R.id.product_description_details);
        productStockQuantity = findViewById(R.id.product_stock);
        productQuantity = findViewById(R.id.product_quantity);
        buttonMinus = findViewById(R.id.buttonMinus);
        buttonPlus = findViewById(R.id.buttonPlus);

        productId = getIntent().getStringExtra(getString(R.string.id));
        db = FirebaseFirestore.getInstance();

        db.collection(getString(R.string.product_collection)).document(productId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                currentItem = task.getResult().toObject(ShopItem.class);
                productName.setText(currentItem.getProductName());
                productPrice.setText("$ " + currentItem.getProductPrice());
                productDescription.setText(currentItem.getProductDesc());
                productStockQuantity.setText(String.valueOf(currentItem.getProductStockQuantity()));
                productQuantity.setText("1");
                Glide.with(this).load(currentItem.getProductImageUrl()).into(productImage);
            } else {
                Log.d("ProductDetailsActivity", "get failed with ", task.getException());
            }
        });

        buttonMinus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(productQuantity.getText().toString());
            if (quantity > 1) {
                quantity--;
                productQuantity.setText(String.valueOf(quantity));
                currentItem.setProductQuantity(String.valueOf(quantity));
            }
        });

        buttonPlus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(productQuantity.getText().toString());
            quantity++;
            productQuantity.setText(String.valueOf(quantity));
            currentItem.setProductQuantity(String.valueOf(quantity));
        });

        backButton.setOnClickListener(v -> finish());
    }
}