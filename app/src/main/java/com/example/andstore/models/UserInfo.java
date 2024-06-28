package com.example.andstore.models;

public class UserInfo {
    private String id;
    private String fullName;
    private String phoneNumber;
    private String address;

    public UserInfo() { }

    public UserInfo(String id, String fullName, String phoneNumber, String address) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getId() {
        return id;
    }
    public String getFullName() {
        return fullName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getAddress() {
        return address;
    }
}
