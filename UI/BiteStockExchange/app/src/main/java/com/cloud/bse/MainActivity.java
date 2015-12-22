package com.cloud.bse;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, ResultCallback<Status> {
    private ProgressDialog pDialog;
    private LoginButton loginButton;
    private TextView info;
    private CallbackManager callbackManager;
    AccessToken accessToken;
    Profile profile;
    ProfileTracker fbProfileTracker;
    GoogleApiClient mGoogleApiClient;
    protected static final String TAG = "MainActivity";

    protected Location mLastLocation;
    protected TextView mLatitudeText;
    protected TextView mLongitudeText;
    protected TextView gcmText;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    protected ArrayList<Geofence> mGeofenceList = new ArrayList<>();
    private PendingIntent mGeofencePendingIntent;
    private GeofencingRequest geofencingRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);

        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        mLatitudeText = (TextView) findViewById((R.id.latitude_text));
        mLongitudeText = (TextView) findViewById((R.id.longitude_text));

        accessToken = AccessToken.getCurrentAccessToken();
        if(accessToken != null) invokeGCM(accessToken.getUserId());
        profile = Profile.getCurrentProfile();
        info.setText("User Id = " + (accessToken != null ? accessToken.getUserId() : null) +
                " profile = " + (profile != null ? profile.getName() : "null"));

        fbProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                // User logged in or changed profile
                profile = currentProfile;
                invokeGCM(profile != null ? profile.getId() : "global");
                info.setText(info.getText().toString() + " profile = " + (profile != null ? profile.getName() : "null"));
                if(profile != null) {
                    DataFactory.setUserInfo(profile.getId(), profile.getName());
                    InvokeNavigation();
                }
            }
        };

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = AccessToken.getCurrentAccessToken();
                profile = Profile.getCurrentProfile();
                info.setText("access Token = " + (accessToken != null ? accessToken.getUserId() : null));
                DataFactory.setFb_token(loginResult.getAccessToken().getToken());
                // RegisterUser registerUser = new RegisterUser();
                // registerUser.execute();
            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");
            }
        });

        if(profile != null) {
            InvokeNavigation();
            DataFactory.setUserInfo(profile.getId(), profile.getName());
        }

        mGeofenceList.add(new Geofence.Builder()
                .setRequestId(Constants.GEO_INNER)
                .setCircularRegion(Constants.LATITUDE, Constants.LONGITUDE, 25)
                .setExpirationDuration(Long.MAX_VALUE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        geofencingRequest = builder.build();

        Intent geoFenceIntent = new Intent(this, GeofenceTransitionsIntentService.class);

        mGeofencePendingIntent = PendingIntent.getService(this, 0, geoFenceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        ((Button) findViewById(R.id.start_app_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InvokeNavigation();
            }
        });

        gcmText = (TextView) findViewById(R.id.gcm_text);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    gcmText.setText(getString(R.string.gcm_send_message));
                } else {
                    gcmText.setText(getString(R.string.token_error_message));
                }
            }
        };
    }

    private void invokeGCM(String topic) {
        DataFactory.setTopic(topic);
        // Start IntentService to register this application with GCM.
        Intent gcmIntent = new Intent(getApplicationContext(), RegistrationIntentService.class);
        startService(gcmIntent);
    }

    private class RegisterUser extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... param) {
            try {
                DataFactory.fetchMenu();
            } catch (Exception e) {
                publishProgress("Failed to Register user. Exception = " + e.toString());
                Log.e("Registration", e.toString());
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... param) {
        }

        @Override
        protected void onPostExecute(Void param) {
        }
    }

    public void InvokeNavigation() {
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        int permission = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        mLastLocation = (permission == PackageManager.PERMISSION_GRANTED) ?
                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient) : null;
        if (mLastLocation != null) {
            mLatitudeText.setText("Latitude = " + mLastLocation.getLatitude());
            mLongitudeText.setText("Longitude = " + mLastLocation.getLongitude());
        } else {
            mLatitudeText.setText("No location detected");
            mLongitudeText.setText("No location detected");
            Toast.makeText(this, "No location detected", Toast.LENGTH_LONG).show();
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    geofencingRequest,
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    mGeofencePendingIntent
            ).setResultCallback(this); // Result processed in onResult().
        } catch (Exception e) {

        }
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            Toast.makeText(this, "Added Geo Fence", Toast.LENGTH_LONG);
            Log.e(TAG, "Success Status Code = " + status.getStatusCode());
        } else {
            Log.e(TAG, "Error Status Code = " + status.getStatusCode());
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Verifies the proper version of Google Play Services exists on the device.
        checkGooglePlayServices(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public static boolean checkGooglePlayServices(final Activity activity) {
        final int googlePlayServicesCheck = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        switch (googlePlayServicesCheck) {
            case ConnectionResult.SUCCESS:
                return true;
            case ConnectionResult.SERVICE_DISABLED:
            case ConnectionResult.SERVICE_INVALID:
            case ConnectionResult.SERVICE_MISSING:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(googlePlayServicesCheck, activity, 0);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        activity.finish();
                    }
                });
                dialog.show();
        }
        return false;
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
