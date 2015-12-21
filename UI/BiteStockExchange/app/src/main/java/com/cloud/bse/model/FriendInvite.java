package com.cloud.bse.model;

/**
 * Created by rakesh on 12/20/15.
 */
public class FriendInvite {
    private String name;
    private String id;
    public FriendInvite(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }
}
