package com.example.andstore.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.example.andstore.models.ShopTransaction;
import com.example.andstore.models.enums.TransactionStatus;
import com.example.andstore.preferences.CartPreferences;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.TransactionOptions;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartFragment extends Fragment {
    private PayPalConfiguration payPalConfiguration;
    private int PAYPAL_REQUEST_CODE = 7171;
    private String currentTransactionId;
    private FirebaseAuth mAuth;
    private FirebaseFirestore fStore;
    private CollectionReference transactionRef;
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
    private TextView phoneNumber;
    private TextView totalCostText;
    private TextView noItemText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_fragment_layout, container, false);
        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        transactionRef = fStore.collection("transactions");
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
        phoneNumber = view.findViewById(R.id.phone);

        // Get user info from Firestore
        fStore.collection("users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    username.setText(documentSnapshot.getString("fullName"));
                    userAddress.setText(documentSnapshot.getString("address"));
                    phoneNumber.setText(documentSnapshot.getString("phoneNumber"));
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

        // Purchase button
        purchaseButton.setOnClickListener(v -> {
            if (cartItemList.size() == 0) {
                Toast.makeText(requireContext(), "No item in cart", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable button
            purchaseButton.setBackgroundColor(getResources().getColor(R.color.grey));
            purchaseButton.setEnabled(false);
            Toast.makeText(requireContext(), "Processing payment...", Toast.LENGTH_SHORT).show();


            // Create transaction
            ShopTransaction transaction = new ShopTransaction(mAuth.getCurrentUser().getUid(),
                    cartItemList,
                    cartPreferences.getTotalPrice(),
                    TransactionStatus.PENDING,
                    Timestamp.now(),
                    username.getText().toString(),
                    userAddress.getText().toString(),
                    phoneNumber.getText().toString());

            transactionRef.add(transaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    String transactionId = documentReference.getId();
                    performTransaction(transactionId, transaction.getCartList());
                    currentTransactionId = transactionId;
                }
            });
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

    private void getPayment(double amounts, String transactionId) {
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

                        // Clear cart
                        cartPreferences.clearCart();
                        cartItemList.clear();
                        adapter.notifyDataSetChanged();

                        // Update transaction status
                        updateTransactionStatus(currentTransactionId, TransactionStatus.COMPLETED);
                        purchaseButton.setBackgroundColor(getResources().getColor(R.color.black));
                        purchaseButton.setEnabled(true);

                    } catch (JSONException e) {
                        Log.e("PayPal", "JSON parsing error: " + e.getMessage());

                        revertStockUpdate(currentTransactionId);
                        updateTransactionStatus(currentTransactionId, TransactionStatus.FAILED);
                        purchaseButton.setBackgroundColor(getResources().getColor(R.color.black));
                        purchaseButton.setEnabled(true);

                    }
                } else {
                    Log.e("PayPal", "Confirmation is null");
                    Toast.makeText(getContext(), "Payment confirmation is null", Toast.LENGTH_SHORT).show();
                    revertStockUpdate(currentTransactionId);
                    updateTransactionStatus(currentTransactionId, TransactionStatus.CANCELLED);
                    purchaseButton.setBackgroundColor(getResources().getColor(R.color.black));
                    purchaseButton.setEnabled(true);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("PayPal", "Payment canceled");
                Toast.makeText(getContext(), "Payment canceled", Toast.LENGTH_SHORT).show();
                revertStockUpdate(currentTransactionId);
                updateTransactionStatus(currentTransactionId, TransactionStatus.CANCELLED);
                purchaseButton.setBackgroundColor(getResources().getColor(R.color.black));
                purchaseButton.setEnabled(true);
            }
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.e("PayPal", "Invalid payment or PayPalConfiguration");
            Toast.makeText(getContext(), "Invalid payment or PayPal configuration", Toast.LENGTH_SHORT).show();
            String transactionId = data.getStringExtra("transactionId");
            if (transactionId != null) {
                revertStockUpdate(transactionId);
                updateTransactionStatus(transactionId, TransactionStatus.FAILED);
                purchaseButton.setBackgroundColor(getResources().getColor(R.color.black));
                purchaseButton.setEnabled(true);
            }
        }
    }

    private void performTransaction(String transactionId, List<CartItem> cartItems) {
        fStore.runTransaction(new TransactionOptions.Builder().setMaxAttempts(2).build(),
                (Transaction.Function<Void>) firestoreTransaction -> {
                    // Check if all products have enough stock
                    Map<String, Long> productStocks = new HashMap<>();
                    for (CartItem item : cartItems) {
                        DocumentReference productRef = fStore.collection(getString(R.string.product_collection)).document(item.getId());
                        DocumentSnapshot productSnapshot = firestoreTransaction.get(productRef);
                        if (!productSnapshot.exists()) {
                            throw new FirebaseFirestoreException("Product does not exist: " + item.getId(), FirebaseFirestoreException.Code.ABORTED);
                        }
                        Long stock = productSnapshot.getLong("productStockQuantity");
                        if (stock == null || stock < item.getQuantity()) {
                            throw new FirebaseFirestoreException("Not enough stock for product: " + item.getId(), FirebaseFirestoreException.Code.ABORTED);
                        }
                        productStocks.put(item.getId(), stock);
                    }

                    // If we get here, all stocks are sufficient. Now perform all writes
                    for (CartItem item : cartItems) {
                        DocumentReference productRef = fStore.collection(getString(R.string.product_collection)).document(item.getId());
                        long currentStock = productStocks.get(item.getId());
                        long newStock = currentStock - item.getQuantity();
                        firestoreTransaction.update(productRef, "productStockQuantity", newStock);
                    }

                    return null;
                }).addOnSuccessListener(aVoid -> {
            Log.d("Transaction", "Transaction successful");

            // Proceed with PayPal payment
            getPayment(cartPreferences.getTotalPrice(), transactionId);
        }).addOnFailureListener(e -> {
            Log.e("Transaction", "Transaction failed: " + e.getMessage());
            if (e instanceof FirebaseFirestoreException && ((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.ABORTED) {
                Toast.makeText(requireContext(), "There is not enough item in stock", Toast.LENGTH_SHORT).show();
                updateTransactionStatus(transactionId, TransactionStatus.FAILED);
                revertStockUpdate(transactionId);
            } else {
                Toast.makeText(requireContext(), "Transaction failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                updateTransactionStatus(transactionId, TransactionStatus.FAILED);
                revertStockUpdate(transactionId);
            }
            purchaseButton.setBackgroundColor(getResources().getColor(R.color.black));
            purchaseButton.setEnabled(true);
        });
    }


    public void updateTransactionStatus(String transactionId, TransactionStatus status) {
        fStore.collection("transactions").document(transactionId).update("status", status.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Transaction", "Transaction status updated to " + status.toString());
            }
        }).addOnFailureListener(e -> Log.e("Transaction", "Error updating transaction status: " + e.getMessage()));
    }

    public void revertStockUpdate(String transactionId) {
        fStore.collection("transactions").document(transactionId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        ShopTransaction transaction = document.toObject(ShopTransaction.class);
                        List<CartItem> cartItems = transaction.getCartList();

                        for (CartItem item : cartItems) {
                            fStore.collection(getString(R.string.product_collection)).document(item.getId()).update("productStockQuantity", FieldValue.increment(item.getQuantity()));
                        }
                    }
                }
            }
        });
    }
}