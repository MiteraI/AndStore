package com.example.andstore.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.andstore.R;
import com.example.andstore.activities.ProductDetailsActivity;
import com.example.andstore.activities.TransactionDetailsActivity;
import com.example.andstore.models.ShopTransaction;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionCardViewHolder> {
    List<ShopTransaction> transactionList;

    public static class TransactionCardViewHolder extends RecyclerView.ViewHolder {
        TextView totalProduct;
        TextView totalCost;
        TextView orderStatus;
        ImageView transactionDetails;
        ImageView productImage;
        TextView productName;
        TextView productTotalPrice;
        TextView productQuantity;
        TextView itemText;

        public TransactionCardViewHolder(@NonNull View itemView) {
            super(itemView);
            totalProduct = itemView.findViewById(R.id.total_product);
            totalCost = itemView.findViewById(R.id.total_cost);
            orderStatus = itemView.findViewById(R.id.order_status);
            transactionDetails = itemView.findViewById(R.id.transaction_details_button);
            productImage = itemView.findViewById(R.id.transaction_item_image);
            productName = itemView.findViewById(R.id.product_name);
            productTotalPrice = itemView.findViewById(R.id.product_total_price);
            productQuantity = itemView.findViewById(R.id.quantity);
            itemText = itemView.findViewById(R.id.total_product_text);
        }
    }

    public TransactionAdapter(List<ShopTransaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transaction_card, parent, false);
        return new TransactionCardViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionCardViewHolder holder, int position) {
        ShopTransaction shopTransaction = transactionList.get(position);
        holder.totalCost.setText("$ " + shopTransaction.getTotalCost());
        holder.totalProduct.setText(String.valueOf(shopTransaction.getCartList().size()));
        holder.orderStatus.setText(shopTransaction.getStatus().toString().toUpperCase());
        holder.productName.setText(shopTransaction.getCartList().get(0).getProductName());
        holder.productTotalPrice.setText("$ " + String.valueOf(shopTransaction.getCartList().get(0).getPrice() * shopTransaction.getCartList().get(0).getQuantity()));
        holder.productQuantity.setText(String.valueOf(shopTransaction.getCartList().get(0).getQuantity()));
        Glide.with(holder.productImage.getContext())
                .load(shopTransaction.getCartList().get(0).getProductImageUrl())
                .into(holder.productImage);
        if (transactionList.size() > 1) {
            holder.itemText.setText("items");
        } else {
            holder.itemText.setText("item");
        }

        holder.transactionDetails.setOnClickListener(v -> {
            // Start activity transaction details
            Intent intent = new Intent(v.getContext(), TransactionDetailsActivity.class);
            intent.putExtra(v.getContext().getString(R.string.id), shopTransaction.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }
}
