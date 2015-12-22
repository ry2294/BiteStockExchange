package com.cloud.bse.model;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by rakesh on 12/20/15.
 */
public class FriendInvite {
    private String friendName;
    private String friendId;
    private LatLng latLng;
    public FriendInvite(String name, String id, LatLng latLng) {
        this.friendName = name;
        this.friendId = id;
        this.latLng = latLng;
    }

    public String getFriendName() {
        return friendName;
    }

    public String getFriendId() {
        return friendId;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
