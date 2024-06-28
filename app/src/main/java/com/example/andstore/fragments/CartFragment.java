package com.example.andstore.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_fragment_layout, container, false);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        cartPreferences = new CartPreferences(view.getContext());

        setupRecyclerView(view);

        return view;
    }

    private void setupRecyclerView(View view) {
        cartItemList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.cart_recycler_view);

        layoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(layoutManager);

        cartItemList = cartPreferences.getCartItems();
        adapter = new CartItemAdapter(view.getContext(), cartItemList);
        recyclerView.setAdapter(adapter);
    }
}