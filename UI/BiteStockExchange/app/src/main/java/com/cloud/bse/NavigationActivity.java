package com.cloud.bse;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.cloud.bse.fragments.FriendActivityFragment;
import com.cloud.bse.fragments.ItemsFragment;
import com.cloud.bse.fragments.MenuFragment;
import com.cloud.bse.fragments.OrderSummaryFragment;
import com.cloud.bse.model.OrderSummaryItem;

import java.util.ArrayList;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private MenuFragment menuFragment = new MenuFragment();
    private FriendActivityFragment friendActivityFragment = new FriendActivityFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fm = getSupportFragmentManager();
        Fragment contentFragment = fm.findFragmentById(R.id.navigation_container);
        if(contentFragment == null) {
            contentFragment = new ItemsFragment();
            Bundle args = new Bundle();
            args.putSerializable("menuItems", DataFactory.getMenuForCategory("Appetizer"));
            args.putSerializable("title", "Appetizer");
            contentFragment.setArguments(args);
            fm.beginTransaction()
                    .add(R.id.navigation_container, contentFragment)
                    .commit();
        }
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

        if (id == R.id.nav_appetizers) {
            // Handle the menu
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new ItemsFragment();
            Bundle args = new Bundle();
            args.putSerializable("menuItems", DataFactory.getMenuForCategory("Appetizer"));
            args.putSerializable("title", "Appetizer");
            fragment.setArguments(args);
            transaction.replace(R.id.navigation_container, fragment);
            transaction.commit();
        } else if (id == R.id.nav_soups) {
            // Handle the menu
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new ItemsFragment();
            Bundle args = new Bundle();
            args.putSerializable("menuItems", DataFactory.getMenuForCategory("Soups"));
            args.putSerializable("title", "Soups");
            fragment.setArguments(args);
            transaction.replace(R.id.navigation_container, fragment);
            transaction.commit();
        } else if (id == R.id.nav_maincourse) {
            // Handle the menu
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new ItemsFragment();
            Bundle args = new Bundle();
            args.putSerializable("menuItems", DataFactory.getMenuForCategory("Main Course"));
            args.putSerializable("title", "Main Course");
            fragment.setArguments(args);
            transaction.replace(R.id.navigation_container, fragment);
            transaction.commit();
        } else if (id == R.id.nav_desserts) {
            // Handle the menu
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment fragment = new ItemsFragment();
            Bundle args = new Bundle();
            args.putSerializable("menuItems", DataFactory.getMenuForCategory("Desserts"));
            args.putSerializable("title", "Desserts");
            fragment.setArguments(args);
            transaction.replace(R.id.navigation_container, fragment);
            transaction.commit();
        } else if (id == R.id.nav_order_summary) {
            Fragment fragment = new OrderSummaryFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.navigation_container, fragment);
            transaction.commit();
        } else if (id == R.id.nav_friend_activity) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.navigation_container, friendActivityFragment);
            transaction.commit();
        } else if (id == R.id.nav_share) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
