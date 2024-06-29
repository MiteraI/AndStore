package com.example.andstore.models;

public class CartItem {
    private String id;
    private String productName;
    private String productImageUrl;
    private int quantity;
    private double price;

    public CartItem() { }
    public CartItem(String id, String productName, String productImageUrl, int quantity, double price) {
        this.id = id;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() {
        return id;
    }
    public String getProductName() {
        return productName;
    }
    public String getProductImageUrl() {
        return productImageUrl;
    }
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }
}
