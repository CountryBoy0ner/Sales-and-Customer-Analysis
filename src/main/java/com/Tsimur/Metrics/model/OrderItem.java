package com.Tsimur.Metrics.model;


public class OrderItem {
    private final String productName;
    private final int quantity;
    private final double price;
    private final Category category;

    public OrderItem(String productName, int quantity, double price, Category category) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.category = category;
    }

    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public Category getCategory() { return category; }
}