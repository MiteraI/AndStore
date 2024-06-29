package com.example.andstore.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.andstore.models.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartPreferences {
    private static final String PREFS_NAME = "cart_prefs";
    private static final String CART_ITEMS = "cart_items";
    private SharedPreferences sharedPreferences;
    List<CartItem> cartItems;
    private Gson gson;

    public CartPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveCartItems(List<CartItem> cartItems) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(cartItems);
        editor.putString(CART_ITEMS, json);
        editor.apply();
    }

    public void addCartItem(CartItem newItem) {
        cartItems = getCartItems();
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }

        // Check if the item is already in the cart
        for (CartItem item : cartItems) {
            if (item.getId().equals(newItem.getId())) {
                item.setQuantity(item.getQuantity() + newItem.getQuantity());
                saveCartItems(cartItems);
                return;
            }
        }
        cartItems.add(newItem);
        saveCartItems(cartItems);
    }

    public List<CartItem> getCartItems() {
        String json = sharedPreferences.getString(CART_ITEMS, null);
        Type type = new TypeToken<List<CartItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void updateCartItemQuantityByProductId(String productId, int quantity) {
        cartItems = getCartItems();
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                if (item.getId().equals(productId)) {
                    item.setQuantity(quantity);
                    break;
                }
            }
            saveCartItems(cartItems);
        }
    }

    public void clearCart() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(CART_ITEMS);
        editor.apply();
    }

    public double getTotalPrice() {
        cartItems = getCartItems();
        double totalPrice = 0;
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                totalPrice += item.getPrice() * item.getQuantity();
            }
        }
        return totalPrice;
    }
}
