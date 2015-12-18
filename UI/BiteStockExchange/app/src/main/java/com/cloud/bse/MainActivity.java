package com.cloud.bse;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private LoginButton loginButton;
    private TextView info;
    private CallbackManager callbackManager;
    AccessToken accessToken;
    Profile profile;
    ProfileTracker fbProfileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);

        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        accessToken = AccessToken.getCurrentAccessToken();
        profile = Profile.getCurrentProfile();
        info.setText("access Token = " + (accessToken != null ? accessToken.getUserId() : null) +
                " profile = " + (profile != null ? profile.getName() : "null"));

        fbProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                // User logged in or changed profile
                profile = currentProfile;
                info.setText(info.getText().toString() + " profile = " + (profile != null ? profile.getName() : "null"));
                InvokeNavigation();
            }
        };

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = AccessToken.getCurrentAccessToken();
                profile = Profile.getCurrentProfile();
                info.setText("access Token = " + (accessToken != null ? accessToken.getUserId() : null));
                // DataFactory.setUserInfo(loginResult.getAccessToken().getUserId(), "Rakesh Yarlagadda",
                // loginResult.getAccessToken().getToken());
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
        }
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
}
