package com.cloud.bse.fragments;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
public class FriendInviteFragment extends Fragment implements OnMapReadyCallback {
    private SupportMapFragment mapFragment;
    private GoogleMap googleMap;
    private Button inviteButton;
    private ProgressDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend_invite, container, false);

        pDialog = new ProgressDialog(getActivity());

        mapFragment = SupportMapFragment.newInstance();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.map, mapFragment);
        transaction.commit();
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Constants.LATITUDE, Constants.LONGITUDE), 16));
        for(FriendInvite invite : DataFactory.getFriendInvites()) {
            Marker melbourne = map.addMarker(new MarkerOptions()
                    .position(invite.getLatLng())
                    .title(invite.getFriendName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            );
        }

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng latLng = marker.getPosition();
                for(FriendInvite invite : DataFactory.getFriendInvites()) {
                    if(invite.getLatLng().latitude == latLng.latitude
                            && invite.getLatLng().longitude == latLng.longitude) {
                        DataFactory.friendInvite = invite;
                        InviteFriend inviteFriend = new InviteFriend();
                        inviteFriend.execute();
                    }
                }
            }
        });
    }

    private class InviteFriend extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Inviting...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... param) {
            try {
                DataFactory.inviteFriend();
            } catch (Exception e) {
                publishProgress("Failed to invite. Exception = " + e.toString());
                Log.e("InviteFriend", e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... param) {
        }

        @Override
        protected void onPostExecute(Void param) {
            if(pDialog.isShowing()) pDialog.dismiss();
        }
    }
}
