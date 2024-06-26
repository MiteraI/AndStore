package com.example.andstore.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.andstore.R;
import com.example.andstore.models.ShopItem;

import java.util.List;

public class ShopItemAdapter extends RecyclerView.Adapter<ShopItemAdapter.ItemViewHolder> {
    List<ShopItem> itemList;
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemImage;
        public TextView itemTitle;
        public TextView textViewNumber;

        public ImageButton buttonMinus;
        public ImageButton buttonPlus;
        public AppCompatButton buyButton;
        public ItemViewHolder(View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            itemTitle = itemView.findViewById(R.id.itemTitle);
            buttonMinus = itemView.findViewById(R.id.buttonMinus);
            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            buttonPlus = itemView.findViewById(R.id.buttonPlus);
            buyButton = itemView.findViewById(R.id.buyButton);
        }
    }

    public ShopItemAdapter(List<ShopItem> itemList) {
        this.itemList = itemList;
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

        // Assuming you have an image URL or resource ID to set the image
        // holder.itemImage.setImageResource(currentItem.getImageResourceId()); // if you have a drawable resource ID
        // or use a library like Glide or Picasso if you have an image URL
        // Glide.with(holder.itemView.getContext()).load(currentItem.getImageUrl()).into(holder.itemImage);

        holder.textViewNumber.setText(currentItem.getProductQuantity());

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
            // Handle the buy button click
            // You might want to add some logic here, for example:
            // open a new activity, show a dialog, etc.
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}
