package com.example.andstore.models;

import com.example.andstore.models.enums.TransactionStatus;
import com.google.firebase.Timestamp;
import com.google.type.DateTime;

import java.util.List;

    public class ShopTransaction {
        private String id;
        private String userId;
        private List<CartItem> cartList;
        private double totalCost;
        private TransactionStatus status;
        private Timestamp transactionDate;
        private String receiverName;
        private String receiverAddress;
        private String receiverPhoneNumber;

    public ShopTransaction() {
    }

    public ShopTransaction(String userId, List<CartItem> cartList, double totalCost, TransactionStatus status, Timestamp transactionDate, String receiverName, String receiverAddress) {
        this.userId = userId;
        this.cartList = cartList;
        this.totalCost = totalCost;
        this.status = status;
        this.transactionDate = transactionDate;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
    }

    public ShopTransaction(String userId,
                           List<CartItem> cartList,
                           double totalCost,
                           TransactionStatus status,
                           Timestamp transactionDate,
                           String receiverName,
                           String receiverAddress,
                           String receiverPhoneNumber) {
        this.userId = userId;
        this.cartList = cartList;
        this.totalCost = totalCost;
        this.status = status;
        this.transactionDate = transactionDate;
        this.receiverName = receiverName;
        this.receiverAddress = receiverAddress;
        this.receiverPhoneNumber = receiverPhoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CartItem> getCartList() {
        return cartList;
    }
    public void setCartList(List<CartItem> cartList) {
        this.cartList = cartList;
    }
    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getReceiverPhoneNumber() {
        return receiverPhoneNumber;
    }

    public void setReceiverPhoneNumber(String receiverPhoneNumber) {
        this.receiverPhoneNumber = receiverPhoneNumber;
    }
}
