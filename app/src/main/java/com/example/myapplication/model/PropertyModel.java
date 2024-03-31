package com.example.myapplication.model;

public class PropertyModel {
    private String id, title,city,locality,imageUrl,price, description;
     private Boolean booked;

    public PropertyModel(String id, String title, String city, String locality, String imageUrl, String price, String description, Boolean booked) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.locality = locality;
        this.imageUrl = imageUrl;
        this.price = price;
        this.description = description;
        this.booked = booked;
    }

    public String getId() {
        return id;
    }

    public Boolean getBooked() {
        return booked;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }

    public String getLocality() {
        return locality;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }
}
