package com.example.andstore.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andstore.BuildConfig;
import com.example.andstore.R;
import com.example.andstore.adapters.CartItemAdapter;
import com.example.andstore.models.CartItem;
import com.example.andstore.preferences.CartPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private PayPalConfiguration payPalConfiguration;
    private int PAYPAL_REQUEST_CODE = 7171;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private CartPreferences cartPreferences;
    private List<CartItem> cartItemList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView clearCartButton;
    private AppCompatButton purchaseButton;
    private TextView totalCost;
    private TextView username;
    private TextView userAddress;
    private TextView totalCostText;
    private TextView noItemText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_fragment_layout, container, false);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        payPalConfiguration = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).clientId(BuildConfig.PAYPAL_CLIENT_ID);
        cartPreferences = CartPreferences.getInstance(view.getContext());

        // Init view
        clearCartButton = view.findViewById(R.id.clear_cart_button);
        totalCost = view.findViewById(R.id.total_cost);
        username = view.findViewById(R.id.receiver);
        userAddress = view.findViewById(R.id.address);
        purchaseButton = view.findViewById(R.id.purchase_button);
        totalCostText = view.findViewById(R.id.total_cost_text);
        noItemText = view.findViewById(R.id.no_items_text);

        // Get user info from Firestore
        fStore.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    username.setText(documentSnapshot.getString("fullName"));
                    userAddress.setText(documentSnapshot.getString("address"));
                }).addOnFailureListener(e -> {
                    TextView updateProfileWarningText = view.findViewById(R.id.update_profile_warning);
                    updateProfileWarningText.setText("Please update your profile to purchase");
                });

        cartPreferences.getCartItemCount().observe(getViewLifecycleOwner(), itemCount -> {
            if (itemCount == 0) {
                noItemText.setVisibility(View.VISIBLE);
                purchaseButton.setText("No item");
                clearCartButton.setVisibility(View.GONE);
                totalCostText.setText("Total cost:");
                totalCost.setText("$ 0.00");
            } else {
                noItemText.setVisibility(View.GONE);
                totalCostText.setText("Total cost " + "(" + itemCount + "):");
                // Setup recycler view
                setupRecyclerView(view);
                totalCost.setText("$ " + String.format("%.2f", cartPreferences.getTotalPrice()));
            }
        });


        // Binding action
        clearCartButton.setOnClickListener(v -> {
            cartPreferences.clearCart();
            cartItemList.clear();
            adapter.notifyDataSetChanged();
        });

        purchaseButton.setOnClickListener(v -> {
            if (cartItemList.size() == 0) {
                Toast.makeText(getContext(), "No item in cart", Toast.LENGTH_SHORT).show();
                return;
            }

            // TODO: Reserve stock quantity
            getPayment();
        });

        return view;
    }

    private void setupRecyclerView(View view) {
        cartItemList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.cart_recycler_view);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        cartItemList = cartPreferences.getCartItems() == null ? new ArrayList<>() : cartPreferences.getCartItems();
        adapter = new CartItemAdapter(view.getContext(), cartItemList);
        recyclerView.setAdapter(adapter);
    }

    private void getPayment() {
        String amounts = totalCost.getText().toString();
        amounts = amounts.substring(2);

        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amounts)), "USD", "Purchase from AndStore", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(requireActivity(), PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfiguration);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);

        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        Log.d("PayPal", "Payment details: " + paymentDetails);
                        // TODO: Product stock quantity update
                        // Clear cart
                        cartPreferences.clearCart();
                        cartItemList.clear();
                        adapter.notifyDataSetChanged();


                    } catch (JSONException e) {
                        Log.e("PayPal", "JSON parsing error: " + e.getMessage());
                        Toast.makeText(getContext(), "Payment details parsing failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("PayPal", "Confirmation is null");
                    Toast.makeText(getContext(), "Payment confirmation is null", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("PayPal", "Payment canceled");
                Toast.makeText(getContext(), "Payment canceled", Toast.LENGTH_SHORT).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e("PayPal", "Invalid payment or PayPalConfiguration");
                Toast.makeText(getContext(), "Invalid payment or PayPal configuration", Toast.LENGTH_SHORT).show();
            }
        }
    }
}