package com.example.andstore.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.andstore.R;
import com.example.andstore.adapters.TransactionAdapter;
import com.example.andstore.models.ShopTransaction;
import com.example.andstore.models.enums.TransactionStatus;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseFirestore fStore;
    List<ShopTransaction> transactionList;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment_layout, container, false);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        setupRecyclerView(view);

        return view;
    }

    private void setupRecyclerView(View view) {
        transactionList = new ArrayList<ShopTransaction>();
        recyclerView = view.findViewById(R.id.transaction_recycler_view);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(adapter);

        // Fetch transactions from Firestore, filter by user ID and status "COMPLETED"
        fStore.collection(getString(R.string.transaction_collection))
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                .whereEqualTo("status", TransactionStatus.COMPLETED.toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ShopTransaction transaction = document.toObject(ShopTransaction.class);
                            transaction.setId(document.getId());
                            transactionList.add(transaction);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        // Log error
                        Log.e("OrderFragment", "Error getting transactions", task.getException());
                    }
                });
    }
}