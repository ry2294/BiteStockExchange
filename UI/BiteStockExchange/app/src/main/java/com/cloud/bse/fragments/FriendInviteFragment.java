package com.cloud.bse.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.cloud.bse.Constants;
import com.cloud.bse.DataFactory;
import com.cloud.bse.R;
import com.cloud.bse.model.FriendInvite;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by rakesh on 12/20/15.
 */
public class FriendInviteFragment extends Fragment {
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private Button inviteButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_invite, container, false);
        mapFragment = new SupportMapFragment() {
            @Override
            public void onActivityCreated(Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                googleMap = mapFragment.getMap();
                if (googleMap != null) {
                    onMapReady(googleMap);
                }
            }
        };
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.map, mapFragment);
        transaction.commit();
        return view;
    }

    private void onMapReady(GoogleMap map) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Constants.LATITUDE, Constants.LONGITUDE), 16));
        for(FriendInvite invite : DataFactory.getFriendInvites()) {
            Marker melbourne = map.addMarker(new MarkerOptions()
                    .position(invite.getLatLng())
                    .title(invite.getFriendName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );
        }
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                return false;
            }
        });
    }
}
