package com.example.andstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.andstore.R;
import com.example.andstore.activities.ProductDetailsActivity;
import com.example.andstore.models.CartItem;
import com.example.andstore.models.ShopItem;
import com.example.andstore.preferences.CartPreferences;

import java.util.List;

public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.ItemViewHolder> {
    List<ShopItem> itemList;
    CartPreferences cartPreferences;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public TextView itemTitle;
        public TextView itemPrice;
        public TextView textViewNumber;
        public ImageButton buttonMinus;
        public ImageButton buttonPlus;
        public AppCompatButton buyButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            buyButton = itemView.findViewById(R.id.buyButton);
        }
    }

    public ShopItemAdapter(Context context, List<ShopItem> itemList) {
        this.itemList = itemList;
        this.cartPreferences = CartPreferences.getInstance(context);
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        ShopItem currentItem = itemList.get(position);
        holder.itemTitle.setText(currentItem.getProductName());
        holder.itemPrice.setText("$ " + currentItem.getProductPrice());

        // or use a library like Glide or Picasso if you have an image URL
        Glide.with(holder.itemView.getContext()).load(currentItem.getProductImageUrl()).into(holder.itemImage);

        holder.textViewNumber.setText(currentItem.getProductQuantity());

        // Intent to product details from item image, item title, item price
        holder.itemImage.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
            intent.putExtra(v.getContext().getString(R.string.id), currentItem.getId());
            v.getContext().startActivity(intent);
        });
        holder.itemTitle.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
            intent.putExtra(v.getContext().getString(R.string.id), currentItem.getId());
            v.getContext().startActivity(intent);
        });
        holder.itemPrice.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailsActivity.class);
            intent.putExtra(v.getContext().getString(R.string.id), currentItem.getId());
            v.getContext().startActivity(intent);
        });


        holder.buttonMinus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.textViewNumber.getText().toString());
            if (quantity > 1) {
                quantity--;
                holder.textViewNumber.setText(String.valueOf(quantity));
                currentItem.setProductQuantity(String.valueOf(quantity));
            }
        });

        holder.buttonPlus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(holder.textViewNumber.getText().toString());
            quantity++;
            holder.textViewNumber.setText(String.valueOf(quantity));
            currentItem.setProductQuantity(String.valueOf(quantity));
        });

        holder.buyButton.setOnClickListener(v -> {
            // Create a CartItem from the current ShopItem
            CartItem cartItem = new CartItem(
                    currentItem.getId(),
                    currentItem.getProductName(),
                    currentItem.getProductImageUrl(),
                    Integer.parseInt(currentItem.getProductQuantity()),
                    currentItem.getProductPrice()
            );

            // Add the CartItem to the cart using CartPreferences
            cartPreferences.addCartItem(cartItem);

            // Provide feedback to the user, e.g., show a Toast message
            Toast.makeText(v.getContext(), currentItem.getProductName() + " added to cart", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}
