package com.cloud.bse.model;

/**
 * Created by Rakesh on 12/5/15.
 */
public class FriendActivity {
    private String name;
    private String activity;
    public FriendActivity(String name, String activity) {
        this.name = name;
        this.activity = activity;
    }

    public String getName() {
        return name;
    }

    public String getActivity() {
        return activity;
    }
}
