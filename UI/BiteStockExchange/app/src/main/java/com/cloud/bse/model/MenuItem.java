package com.cloud.bse.model;

import java.util.Random;

/**
 * Created by Rakesh on 12/5/15.
 */
public class MenuItem {
    private String item_name, category, item_id;
    private int item_low_price, item_high_price, item_price;

    public MenuItem(String item_id, String item_name, int item_low_price, int item_high_price, String category) {
        this.item_name = item_name; this.category = category;
        this.item_high_price = item_high_price; this.item_low_price = item_low_price; this.item_id = item_id;
        Random r = new Random();
        this.item_price = r.nextInt(item_high_price-item_low_price) + item_low_price;
    }

    public String getItemName() {
        return item_name;
    }

    public int getActualPrice() {
        return this.item_price;
    }

    public void setActualPrice() {
        Random r = new Random();
        this.item_price = r.nextInt(item_high_price-item_low_price) + item_low_price;
    }

    public int getLowPrice() {
        return item_low_price;
    }

    public int getItemHighPrice() {
        return item_high_price;
    }

    public String getCategory() {
        return category;
    }

    public String getItemId() {
        return item_id;
    }
}
