package com.example.andstore.fragments;

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

import com.example.andstore.R;
import com.example.andstore.adapters.CartItemAdapter;
import com.example.andstore.models.CartItem;
import com.example.andstore.preferences.CartPreferences;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private CartPreferences cartPreferences;
    private List<CartItem> cartItemList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView clearCartButton;
    private AppCompatButton purchaseButton;
    private TextView totalPrice;
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
        cartPreferences = CartPreferences.getInstance(view.getContext());

        // Init view
        clearCartButton = view.findViewById(R.id.clear_cart_button);
        totalPrice = view.findViewById(R.id.total_cost);
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
                totalPrice.setText("$ 0.00");
            } else {
                noItemText.setVisibility(View.GONE);
                totalCostText.setText("Total cost " + "(" + itemCount + "):");
                // Setup recycler view
                setupRecyclerView(view);
                totalPrice.setText("$ " + String.format("%.2f", cartPreferences.getTotalPrice()));
            }
        });


        // Binding action
        clearCartButton.setOnClickListener(v -> {
            cartPreferences.clearCart();
            cartItemList.clear();
            adapter.notifyDataSetChanged();
        });

        purchaseButton.setOnClickListener(v -> {
            cartPreferences.clearCart();
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
}