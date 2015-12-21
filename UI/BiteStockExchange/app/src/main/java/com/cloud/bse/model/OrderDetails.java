package com.cloud.bse.model;

import java.util.ArrayList;

/**
 * Created by rakesh on 12/21/15.
 */
public class OrderDetails {
    private String orderId;
    private ArrayList<OrderSummaryItem> orderedItems;

    public OrderDetails(String orderId, ArrayList<OrderSummaryItem> orderedItems) {
        this.orderId = orderId; this.orderedItems = orderedItems;
    }

    public String getOrderId() {
        return orderId;
    }
    public ArrayList<OrderSummaryItem> getOrderedItems() {
        return orderedItems;
    }
}
