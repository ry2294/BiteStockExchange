package com.cloud.bse;

import android.util.Log;

import com.cloud.bse.model.MenuItem;
import com.cloud.bse.model.OrderSummaryItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rakesh on 12/5/15.
 */
public class DataFactory {
    private static HashMap<String, OrderSummaryItem> orderSummaryItemHashMap = new HashMap<>();
    private static HashMap<String, MenuItem> menuItemHashMap = new HashMap<>();
    private static DataFactory ourInstance = new DataFactory();
    private static String user_id;
    private static String user_name;
    private static String fb_token;

    public static DataFactory getInstance() {
        return ourInstance;
    }

    private DataFactory() {
        menuItemHashMap.put("Appetizer", new MenuItem("1", "Chilli Chicken", 10, 15, "Appetizer"));

        menuItemHashMap.put("Soups", new MenuItem("1", "Corn Soup", 7, 9, "Soups"));

        menuItemHashMap.put("Main Course", new MenuItem("1", "Rice with Beans", 15, 20, "Main Course"));

        menuItemHashMap.put("Desserts", new MenuItem("1", "Vanilla", 5, 10, "Desserts"));
    }

    public static ArrayList<MenuItem> getMenuForCategory(String category) {
        ArrayList<MenuItem> items = new ArrayList<>();
        for(MenuItem item : menuItemHashMap.values()) {
            if(item.getCategory().equals(category))
                items.add(item);
        }
        return items;
    }

    public static void addItemToOrder(String itemId, String itemName, int itemPrice, int quantity) {
        if(!orderSummaryItemHashMap.containsKey(itemId))
            orderSummaryItemHashMap.put(itemId, new OrderSummaryItem(itemId, itemName, itemPrice, quantity));
    }

    public static void addQuantity(String itemId) {
        if(orderSummaryItemHashMap.containsKey(itemId))
            orderSummaryItemHashMap.get(itemId).addQuantity();
    }

    public static void removeQuantity(String itemId) {
        if(orderSummaryItemHashMap.containsKey(itemId))
            orderSummaryItemHashMap.get(itemId).removeQuantity();
    }

    public static ArrayList<OrderSummaryItem> getOrderSummaryItems() {
        ArrayList<OrderSummaryItem> orderSummaryItems = new ArrayList<>();
        for(OrderSummaryItem item : orderSummaryItemHashMap.values()) {
            orderSummaryItems.add(item);
        }
        return orderSummaryItems;
    }

    public static void setUserInfo(String user_id, String user_name, String fb_token) {
        DataFactory.user_id = user_id;
        DataFactory.user_name = user_name;
        DataFactory.fb_token = fb_token;
    }

    public static void fetchMenu() throws IOException {
        JSONArray response;
        try {
            URL url = new URL("http://10.0.2.2:16386/api/menu");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String responseString = readStream(conn.getInputStream());
                Log.v("CatalogClient", responseString);
                response = new JSONArray(responseString);
                Log.e("fetchMenu", response.toString());
                for(int i = 0; i < response.length(); i++) {
                    JSONObject menuItemJSON = response.getJSONObject(i);
                    MenuItem menuItem = new MenuItem(menuItemJSON.getString("item_id"),
                            menuItemJSON.getString("item_name"), menuItemJSON.getInt("low_price"),
                            menuItemJSON.getInt("high_price"), menuItemJSON.getString("item_category"));
                    menuItemHashMap.put(menuItemJSON.getString("item_id"), menuItem);
                }
            } else {
                Log.e("fetchMenu", "Response code:" + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    public static String registerUser() throws IOException {
        String response = "";

        URL url = new URL("http://10.0.2.2:16386/api/user/register");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        HashMap<String, String> postDataParams = new HashMap<>();
        postDataParams.put("user_id", user_id);
        postDataParams.put("fb_token", fb_token);
        writer.write(getPostDataString(postDataParams));

        writer.flush();
        writer.close();
        os.close();
        int responseCode=conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            String line = "Request Success";
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line=br.readLine()) != null) {
                response+=line;
            }
        }
        else {
            response="Request Failed";
        }

        return response;
    }

    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}