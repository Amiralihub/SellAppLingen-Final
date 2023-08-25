package com.example.sellapplingen;

import android.location.Address;

public class Settings {

    private String token;
    private String storeName;
    private String owner;
    private Address2 address;
    private String telephone;
    private String email;
    private String logo;
    private String backgroundImage;

    // Konstruktor
    public Settings(String token, String storeName, String owner, Address2 address,
                    String telephone, String email, String logo, String backgroundImage) {
        this.token = token;
        this.storeName = storeName;
        this.owner = owner;
        this.address = address;
        this.telephone = telephone;
        this.email = email;
        this.logo = logo;
        this.backgroundImage = backgroundImage;
    }

    // Getter und Setter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Address2 getAddress() {
        return address;
    }

    public void setAddress(Address2 address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
}

