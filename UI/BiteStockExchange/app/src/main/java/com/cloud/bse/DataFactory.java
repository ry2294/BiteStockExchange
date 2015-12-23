package com.cloud.bse;

import android.location.Location;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.cloud.bse.model.FriendActivity;
import com.cloud.bse.model.FriendInvite;
import com.cloud.bse.model.MenuItem;
import com.cloud.bse.model.OrderSummaryItem;
import com.google.android.gms.maps.model.LatLng;

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
import java.net.URI;
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
    private static ArrayList<FriendActivity> friendActivities = new ArrayList<>();
    private static String user_id;
    private static String user_name;
    private static String fb_token;
    private static Uri image;
    private static String topic;
    private static boolean inner = false;
    private static boolean outer = false;
    private static ArrayList<FriendInvite> friendInvites = new ArrayList<>();
    private static DataFactory ourInstance = new DataFactory();
    private static int total_price = 0;

    public static DataFactory getInstance() {
        return ourInstance;
    }

    private DataFactory() {
        menuItemHashMap.put("Appetizer", new MenuItem("1", "Chilli Chicken", 10, 15, "Appetizer"));

        menuItemHashMap.put("Soups", new MenuItem("2", "Corn Soup", 7, 9, "Soups"));

        menuItemHashMap.put("Main Course", new MenuItem("3", "Rice with Beans", 15, 20, "Main Course"));

        menuItemHashMap.put("Desserts", new MenuItem("4", "Vanilla", 5, 10, "Desserts"));

        friendInvites.add(new FriendInvite("Sindhura Jhansi", "23498568383798", new LatLng(40.810776, -73.958588)));
        friendInvites.add(new FriendInvite("Varun Shetty", "34984328383798", new LatLng(40.809409, -73.960046)));
        friendInvites.add(new FriendInvite("Diksha Haresh", "98568383798", new LatLng(40.808816, -73.959491)));
        friendInvites.add(new FriendInvite("Tanaya", "8568383798", new LatLng(40.809774, -73.958501)));
    }

    public static ArrayList<FriendInvite> getFriendInvites() {
        return friendInvites;
    }

    public static void setTopic (String topic) {
        DataFactory.topic = topic;
    }

    public static String getTopic() {
        return topic;
    }

    public static boolean isInner() {
        return inner;
    }

    public static boolean isOuter() {
        return outer;
    }

    public static void setInner(boolean inner) {
        DataFactory.inner = inner;
    }

    public static void setOuter(boolean outer) {
        DataFactory.outer = outer;
    }

    public static ArrayList<MenuItem> getMenuForCategory(String category) {
        ArrayList<MenuItem> items = new ArrayList<>();
        for(MenuItem item : menuItemHashMap.values()) {
            if(item.getCategory().equals(category))
                items.add(item);
        }
        return items;
    }

    public static int getTotal_price() {
        return total_price;
    }

    public static void addItemToOrder(String itemId, String itemName, int itemPrice, int quantity, FragmentActivity fragmentActivity) {
        if(!orderSummaryItemHashMap.containsKey(itemId)) {
            Toast.makeText(fragmentActivity, "Item added to Kart", Toast.LENGTH_SHORT).show();
            orderSummaryItemHashMap.put(itemId, new OrderSummaryItem(itemId, itemName, itemPrice, quantity));
            total_price += itemPrice * quantity;
        } else {
            Toast.makeText(fragmentActivity, "Item already added to Kart", Toast.LENGTH_SHORT).show();
        }
    }

    public static void addQuantity(String itemId) {
        if(orderSummaryItemHashMap.containsKey(itemId)) {
            orderSummaryItemHashMap.get(itemId).addQuantity();
            total_price += orderSummaryItemHashMap.get(itemId).getItemPrice();
        }
    }

    public static void removeQuantity(String itemId) {
        if(orderSummaryItemHashMap.containsKey(itemId)) {
            orderSummaryItemHashMap.get(itemId).removeQuantity();
            total_price -= orderSummaryItemHashMap.get(itemId).getItemPrice();
        }
    }

    public static ArrayList<OrderSummaryItem> getOrderSummaryItems() {
        ArrayList<OrderSummaryItem> orderSummaryItems = new ArrayList<>();
        for(OrderSummaryItem item : orderSummaryItemHashMap.values()) {
            orderSummaryItems.add(item);
        }
        return orderSummaryItems;
    }

    public static void setUserInfo(String user_id, String user_name, Uri image) {
        DataFactory.user_id = user_id;
        DataFactory.user_name = user_name;
        DataFactory.image = image;
    }

    public static void setFb_token(String fb_token) {
        DataFactory.fb_token = fb_token;
    }

    public static String getUsername() {
        return DataFactory.user_name;
    }

    public static Uri getImage() {
        return DataFactory.image;
    }

    public static void fetchFriendActivity() throws IOException {
        JSONArray response;
        friendActivities.clear();
        try {
            URL url = new URL(Constants.SERVER + "/api/friend/activity/" + user_id);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {
                String responseString = readStream(conn.getInputStream());
                Log.v("FetchFriendActivity", responseString);
                response = new JSONArray(responseString);
                for(int i=0; i < response.length(); i++) {
                    JSONObject activity = response.getJSONObject(i);
                    FriendActivity friendActivity = new FriendActivity(activity.getString("friend_name"),
                            "Ordered " + activity.getString("count") + " " + activity.getString("item_name"));
                    friendActivities.add(friendActivity);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<FriendActivity> getFriendActivities() {
        return friendActivities;
    }

    public static void fetchMenu() throws IOException {
        JSONArray response;
        menuItemHashMap.clear();
        try {
            URL url = new URL(Constants.SERVER + "/api/menu");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String responseString = readStream(conn.getInputStream());
                Log.v("FetchMenu", responseString);
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

    public static String exitGeoFence() throws IOException {
        String response = "";
        URL url = new URL(Constants.SERVER + "/api/user/exit");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        Log.e("ExitGeoFence", "Openning connection");

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));

        HashMap<String, String> postDataParams = new HashMap<>();
        Log.e("ExitGeoFence", "user_id = " + user_id);
        postDataParams.put("user_id", user_id);
        Log.e("ExitGeoFence", "Before flushing Response");
        writer.write(getPostDataString(postDataParams));
        writer.flush();
        writer.close();
        os.close();
        int responseCode=conn.getResponseCode();
        Log.e("ExitGeoFence", "Getting Response");

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

    public static String enterGeoFence(String lat, String lng) throws IOException {
        String response = "";
        URL url = new URL(Constants.SERVER + "/api/user/enter");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        Log.e("EnterGeoFence", "Openning connection");

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));

        HashMap<String, String> postDataParams = new HashMap<>();
        Log.e("EnterGeoFence", "user_id = " + user_id);
        Log.e("EnterGeoFence", "fb_token = " + fb_token);
        postDataParams.put("user_id", user_id);
        postDataParams.put("lat", lat);
        postDataParams.put("lng", lng);
        Log.e("EnterGeoFence", "Before flushing Response");
        writer.write(getPostDataString(postDataParams));
        writer.flush();
        writer.close();
        os.close();
        int responseCode=conn.getResponseCode();
        Log.e("EnterGeoFence", "Getting Response");

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

    public static String convertItems() {
        String items = "";
        for(OrderSummaryItem orderSummaryItem : getOrderSummaryItems()) {
            for(int i = 1; i <= orderSummaryItem.getQuantity(); i++) {
                items+= "{\"item_id\": \"" + orderSummaryItem.getItemId()
                        + "\", \"price\": \"" + orderSummaryItem.getItemPrice() + "\"},";
            }
        }
        items = items.substring(0, items.length() - 1);
        items = "[" + items + "]";
        return items;
    }

    public static String placeOrder() throws IOException {
        String response = "";
        Log.e("PlaceOrder", "Before Openning connection");

        URL url = new URL(Constants.SERVER + "/api/menu/order");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        Log.e("PlaceOrder", "Openning connection");

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));

        HashMap<String, String> postDataParams = new HashMap<>();
        Log.e("PlaceOrder", "user_id = " + user_id);
        postDataParams.put("user_id", user_id);

        Log.e("PlaceOrder", "user_id = " + user_id);
        postDataParams.put("order_summary", convertItems());

        Log.e("PlaceOrder", "Before flushing Response");
        writer.write(getPostDataString(postDataParams));
        writer.flush();
        writer.close();
        os.close();
        int responseCode=conn.getResponseCode();
        Log.e("PlaceOrder", "Getting Response");

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

    public static String registerUser() throws IOException {

        String response = "";
        Log.e("Registration", "Before Openning connection");

        URL url = new URL(Constants.SERVER + "/api/user/register");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        Log.e("Registration", "Openning connection");

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));

        HashMap<String, String> postDataParams = new HashMap<>();
        Log.e("Registration", "user_id = " + user_id);
        Log.e("Registration", "fb_token = " + fb_token);
        postDataParams.put("user_id", user_id);
        postDataParams.put("fb_token", fb_token);
        Log.e("Registration", "Before flushing Response");
        writer.write(getPostDataString(postDataParams));
        writer.flush();
        writer.close();
        os.close();
        int responseCode=conn.getResponseCode();
        Log.e("Registration", "Getting Response");

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
