package com.socaldevs.timelapse.android;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;

import com.actionbarsherlock.view.MenuItem;
import com.socaldevs.timelapse.android.fragments.AlbumFragment;
import com.socaldevs.timelapse.android.fragments.ControlFragment;
import com.socaldevs.timelapse.android.fragments.NewsFeedFragment;
import com.socaldevs.timelapse.android.fragments.SignInFragment;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class MainActivity extends SherlockFragmentActivity{

    private SharedPreferences sp;
    private final String TAG = "I HATE SHIT THAT DOESN'T WORK";
    private SherlockFragment currentFragment;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navDrawerListItems;
    private ActionBarDrawerToggle mDrawerToggle;
    private UpdateReceiver mUpdateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getPreferences(MODE_PRIVATE);

        mDrawerLayout       = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList         = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setBackgroundColor(getResources().getColor(R.color.vintage_orange));
        navDrawerListItems  = getResources().getStringArray(R.array.navigation_items);
        currentFragment     = new SignInFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_main, currentFragment).commit();
        //We want to lock the drawer while in the sign-in page. Once they are out of it, we will
        //unlock it via sending off an intent.
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        /* Here is all of the Drawer Shit.... */
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, navDrawerListItems));

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //This stuff deals with the toggling of the drawer
        final String mTitle = String.valueOf(getTitle());
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //Will receive an intent when we want to unlock the drawer and show the up icons
        mUpdateReceiver = new UpdateReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        };
        registerReceiver(mUpdateReceiver,
                new IntentFilter(Constants.INTENT_UNLOCK_ID));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    protected void onActivityResult(int requestCode, int responseCode, Intent intent){
        super.onActivityResult(requestCode, responseCode, intent);
        currentFragment.onActivityResult(requestCode, responseCode, intent);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

/** Swaps fragments in the main content view */
    public void selectItem(int position) {
        // Insert the fragment by replacing any existing fragment
        String newTitle;
        switch(position){
            case 0:
                currentFragment = new NewsFeedFragment();
                newTitle = "News Feed";
                break;
            case 1:
                currentFragment = new AlbumFragment();
                newTitle = "My Album";
                break;
            case 2:
                currentFragment = new ControlFragment();
                newTitle = "Controls";
                break;
            default:
                currentFragment = new SignInFragment();
                newTitle = "Sign In";
                break;
        }

        getSupportFragmentManager().beginTransaction()
               .replace(R.id.frame_main, currentFragment);

        // Highlight the selected item and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
        getSupportActionBar().setTitle(newTitle);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUpdateReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mUpdateReceiver, new IntentFilter(Constants.INTENT_UNLOCK_ID));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
