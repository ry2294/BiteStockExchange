package com.cloud.bse;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.cloud.bse.fragments.FriendActivityFragment;
import com.cloud.bse.fragments.FriendInviteFragment;
import com.cloud.bse.fragments.MenuFragment;
import com.cloud.bse.fragments.OrderHistoryFragment;
import com.cloud.bse.fragments.OrderSummaryFragment;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pDialog = new ProgressDialog(this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Log.e("NavHeader", "User name = " + DataFactory.getUsername());
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.nav_header_name)).setText(DataFactory.getUsername());

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new MenuFragment();
        transaction.replace(R.id.navigation_container, fragment);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            FetchMenu fetchMenu = new FetchMenu();
            fetchMenu.execute();
        } else if (id == R.id.nav_order_summary) {
            Fragment fragment = new OrderSummaryFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.navigation_container, fragment);
            transaction.commit();
        } else if (id == R.id.nav_order_history) {
            FetchHistory fetchHistory = new FetchHistory();
            fetchHistory.execute();
        } else if (id == R.id.nav_friend_activity) {
            Fragment fragment = new FriendActivityFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.navigation_container, fragment);
            transaction.commit();
        } else if (id == R.id.nav_friend_invite) {
            Fragment fragment = new FriendInviteFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.navigation_container, fragment);
            transaction.commit();
        } else if (id == R.id.nav_share) {
            Toast.makeText(this, "Sharing your experience in Facebook", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public class FetchMenu extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... param) {
            try {
                DataFactory.fetchMenu();
            } catch (Exception e) {
                publishProgress("Failed to fetch menu. Exception = " + e.toString());
                Log.e("FetchMenu", e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... param) {
        }

        @Override
        protected void onPostExecute(Void param) {
            // Handle the menu
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new MenuFragment();
            transaction.replace(R.id.navigation_container, fragment);
            transaction.commit();
        }
    }

    private class FetchHistory extends AsyncTask<Void, String, Void> {
        @Override
        protected void onPreExecute() {
            pDialog.setMessage("Fetching history...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... param) {
            try {
                DataFactory.fetchOrderHistory();
            } catch (Exception e) {
                publishProgress("Failed to fetch history. Exception = " + e.toString());
                Log.e("PlaceOrder", e.toString());
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... param) {
        }

        @Override
        protected void onPostExecute(Void param) {
            if(pDialog.isShowing()) pDialog.dismiss();
            Fragment fragment = new OrderHistoryFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.navigation_container, fragment);
            transaction.commit();
        }
    }
}
