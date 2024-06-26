package com.example.andstore.models;

public class ShopItem {
    private String id;
    private String productName;
    private String productImageUrl;
    private String productDesc;
    private String productQuantity;
    public ShopItem(String id,
                    String productName,
                    String productImageUrl,
                    String productDesc,
                    String productQuantity) {
        this.id = id;
        this.productName = productName;
        this.productImageUrl = productImageUrl;
        this.productDesc = productDesc;
        this.productQuantity = productQuantity;
    }

    public String getId() {
        return id;
    }
    public String getProductName() {
        return productName;
    }
    public String getProductDesc() {
        return productDesc;
    }
    public String getProductImageUrl() {
        return productImageUrl;
    }
    public String getProductQuantity() {
        return productQuantity;
    }
    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }
}
