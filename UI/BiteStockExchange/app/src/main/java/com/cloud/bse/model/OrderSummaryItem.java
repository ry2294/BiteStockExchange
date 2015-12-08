package com.cloud.bse.model;

/**
 * Created by Rakesh on 12/5/15.
 */
public class OrderSummaryItem {
    private String itemId, itemName;
    private int quantity, itemPrice;

    public OrderSummaryItem(String itemId, String itemName, int quantity, int itemPrice) {
        this.itemId = itemId;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void addQuantity() {
        quantity++;
    }

    public String getItemName() {
        return itemName;
    }

    public void removeQuantity() {
        if(quantity > 0) quantity--;
    }
}
