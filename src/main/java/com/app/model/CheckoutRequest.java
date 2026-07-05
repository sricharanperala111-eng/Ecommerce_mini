package com.app.model;

import java.util.List;

public class CheckoutRequest {

    public static class CartLine {
        public Long productId;
        public Integer quantity;
    }

    private String customerName;
    private String customerEmail;
    private String shippingAddress;
    private List<CartLine> items;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public List<CartLine> getItems() { return items; }
    public void setItems(List<CartLine> items) { this.items = items; }
}
