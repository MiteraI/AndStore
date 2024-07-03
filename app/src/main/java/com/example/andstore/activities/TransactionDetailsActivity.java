package com.example.andstore.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.andstore.R;
import com.example.andstore.models.CartItem;
import com.example.andstore.models.ShopTransaction;
import com.example.andstore.models.enums.TransactionStatus;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionDetailsActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    ShopTransaction transaction;
    LinearLayout productListContainer;
    TextView transactionDate;
    TextView receiverName;
    TextView receiverAddress;
    TextView receiverPhone;
    TextView transactionId;
    TextView transactionStatus;
    TextView totalPrice;
    ImageView navigationBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.transaction_details_layout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Init views
        transactionDate = findViewById(R.id.transaction_date);
        receiverName = findViewById(R.id.delivery_name);
        receiverAddress = findViewById(R.id.delivery_address);
        receiverPhone = findViewById(R.id.delivery_phone);
        transactionId = findViewById(R.id.transaction_id);
        transactionStatus = findViewById(R.id.transaction_status);
        productListContainer = findViewById(R.id.product_list_container);
        navigationBackButton = findViewById(R.id.navigation_back_button);
        totalPrice = findViewById(R.id.total_price);

        // Set navigation back button
        navigationBackButton.setOnClickListener(v -> finish());

        // Get transaction details by getting the transaction ID from the intent
        String id = getIntent().getStringExtra("Id");
        getTransactionData(id);

    }

    public void getTransactionData(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("transactions").document(documentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            transaction = new ShopTransaction();

                            transaction.setId(documentSnapshot.getId());
                            transaction.setTotalCost(documentSnapshot.getDouble("totalCost"));
                            transaction.setStatus(TransactionStatus.valueOf(documentSnapshot.getString("status")));
                            transaction.setTransactionDate(documentSnapshot.getTimestamp("transactionDate"));
                            transaction.setReceiverName(documentSnapshot.getString("receiverName"));
                            transaction.setReceiverAddress(documentSnapshot.getString("receiverAddress"));
                            transaction.setReceiverPhoneNumber(documentSnapshot.getString("receiverPhoneNumber"));

                            List<CartItem> cartList = new ArrayList<>();
                            List<Map<String, Object>> cartListData = (List<Map<String, Object>>) documentSnapshot.get("cartList");

                            if (cartListData != null) {
                                for (Map<String, Object> cartItemData : cartListData) {
                                    CartItem cartItem = new CartItem(cartItemData.get("id").toString(),
                                            cartItemData.get("productName").toString(),
                                            cartItemData.get("productImageUrl").toString(),
                                            ((Long) cartItemData.get("quantity")).intValue(),
                                            (Double) cartItemData.get("price"));

                                    cartList.add(cartItem);
                                }
                            }

                            transaction.setCartList(cartList);

                            setTransactionDate(transaction);
                        } else {
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        finish();
                    }
                });
    }

    public void setTransactionDate(ShopTransaction transaction) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm", Locale.getDefault());
        Date date = transaction.getTransactionDate().toDate();
        transactionDate.setText(sdf.format(date));
        transactionId.setText(transaction.getId());
        transactionStatus.setText(transaction.getStatus().toString());
        receiverName.setText(transaction.getReceiverName());
        receiverAddress.setText(transaction.getReceiverAddress());
        receiverPhone.setText(transaction.getReceiverPhoneNumber());
        // Set the total price of the transaction
        totalPrice.setText("$ " + String.valueOf(transaction.getTotalCost()));

        // Inflate cart items to product list container
        for (CartItem cartItem : transaction.getCartList()) {
            LinearLayout productItem = (LinearLayout) getLayoutInflater().inflate(R.layout.transaction_item_card, null);
            TextView productName = productItem.findViewById(R.id.product_name);
            ImageView productImage = productItem.findViewById(R.id.transaction_item_image);
            TextView productQuantity = productItem.findViewById(R.id.quantity);
            TextView productTotalPrice = productItem.findViewById(R.id.product_total_price);

            Glide.with(productImage.getContext())
                    .load(cartItem.getProductImageUrl())
                    .into(productImage);
            productName.setText(cartItem.getProductName());
            productQuantity.setText(String.valueOf(cartItem.getQuantity()));
            productTotalPrice.setText("$ " + String.format("%.2f", cartItem.getPrice() * cartItem.getQuantity()));

            // Add the divider after each item (except the last one)
            View divider = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1  // 1dp height
            );

            divider.setLayoutParams(params);
            divider.setBackgroundColor(Color.parseColor("#888888"));

            productListContainer.addView(productItem);
            productListContainer.addView(divider);
        }
    }
}