package com.example.andstore.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.andstore.R;
import com.example.andstore.models.CartItem;
import com.example.andstore.models.ShopItem;
import com.example.andstore.preferences.CartPreferences;

import java.text.DecimalFormat;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder> {
    List<CartItem> cartItems;
    CartPreferences cartPreferences;
    OnCartItemChangeListener onCartItemChangeListener;

    public interface OnCartItemChangeListener {
        void onCartItemChange();
    }

    public static class CartItemViewHolder extends RecyclerView.ViewHolder {
        ImageView cartItemImage;
        TextView cartItemTitle;
        TextView cartItemPrice;
        TextView cartQuantity;
        TextView cartItemTotalPrice;
        ImageButton buttonMinus;
        ImageButton buttonPlus;

        public CartItemViewHolder(@NonNull View itemView) {
            super(itemView);
            cartItemImage = itemView.findViewById(R.id.cart_item_image);
            cartItemTitle = itemView.findViewById(R.id.cart_item_name);
            cartItemPrice = itemView.findViewById(R.id.cart_item_price);
            cartQuantity = itemView.findViewById(R.id.item_quantity);
            cartItemTotalPrice = itemView.findViewById(R.id.total_price);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
        }
    }

    public CartItemAdapter(Context context, List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.cartItems = cartItems;
        this.cartPreferences = new CartPreferences(context);
        this.onCartItemChangeListener = listener;
    }

    @NonNull
    @Override
    public CartItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cart_item_card, parent, false);
        return new CartItemAdapter.CartItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemViewHolder holder, int position) {
        CartItem currentItem = cartItems.get(position);
        holder.cartItemTitle.setText(currentItem.getProductName());
        holder.cartItemPrice.setText("$ " + String.format("%.2f", currentItem.getPrice()));

        Glide.with(holder.itemView.getContext()).load(currentItem.getProductImageUrl()).into(holder.cartItemImage);

        holder.cartQuantity.setText(String.valueOf(currentItem.getQuantity()));
        holder.cartItemTotalPrice.setText("$ " + String.format("%.2f", currentItem.getPrice() * currentItem.getQuantity()));

        holder.buttonMinus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.cartQuantity.getText().toString());
            if (quantity > 1) {
                quantity--;
                cartPreferences.updateCartItemQuantityByProductId(currentItem.getId(), quantity);
                holder.cartQuantity.setText(String.valueOf(quantity));
                currentItem.setQuantity(quantity);
                holder.cartItemTotalPrice.setText("$ " + String.format("%.2f", currentItem.getPrice() * quantity));
                onCartItemChangeListener.onCartItemChange();
            }
        });

        holder.buttonPlus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.cartQuantity.getText().toString());
            quantity++;
            cartPreferences.updateCartItemQuantityByProductId(currentItem.getId(), quantity);
            holder.cartQuantity.setText(String.valueOf(quantity));
            currentItem.setQuantity(quantity);
            holder.cartItemTotalPrice.setText("$ " + String.format("%.2f", currentItem.getPrice() * quantity));
            onCartItemChangeListener.onCartItemChange();
        });

    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }
}
