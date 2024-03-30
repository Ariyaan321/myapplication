package com.example.myapplication.model;

public class PropertyModel {
    private String id, title,city,locality,imageUrl,price, description;

    public PropertyModel(String id, String title, String city, String locality, String imageUrl, String price, String description) {
        this.id = id;
        this.title = title;
        this.city = city;
        this.locality = locality;
        this.imageUrl = imageUrl;
        this.price = price;
        this.description = description;
    }

    public String getId() {
        return id;
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
