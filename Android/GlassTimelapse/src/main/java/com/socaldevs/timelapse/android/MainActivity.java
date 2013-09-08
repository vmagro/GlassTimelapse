package com.socaldevs.timelapse.android;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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



public class MainActivity extends SherlockFragmentActivity{

    private SharedPreferences sp;
    private final String TAG = "I HATE SHIT THAT DOESN'T WORK";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navDrawerListItems;
    private ActionBarDrawerToggle mDrawerToggle;
    private UpdateReceiver mUpdateReceiver;
    private SherlockFragment currentFragment, albumFragment, newsFeedFragment, signInFragment, controlFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getPreferences(MODE_PRIVATE);

        mDrawerLayout       = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList         = (ListView) findViewById(R.id.left_drawer);
        navDrawerListItems  = getResources().getStringArray(R.array.navigation_items);

        albumFragment       = new AlbumFragment();
        newsFeedFragment    = new NewsFeedFragment();
        signInFragment      = new SignInFragment();
        controlFragment     = new ControlFragment();

        mDrawerList.setBackgroundColor(getResources().getColor(R.color.vintage_orange));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_main, signInFragment).commit();
        currentFragment = signInFragment;

        //We want to lock the drawer while in the sign-in page. Once they are out of it, we will
        //unlock it via sending off an intent.


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
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
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
                currentFragment = newsFeedFragment;
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
        SherlockFragment incomingFragment;
        switch(position){
            case 0:
                incomingFragment = newsFeedFragment;
                newTitle = "News Feed";
                break;
            case 1:
                incomingFragment = albumFragment;
                newTitle = "My Album";
                break;
            case 2:
                incomingFragment = controlFragment;
                newTitle = "Controls";
                break;
            default:
                incomingFragment = signInFragment;
                newTitle = "Sign In";
                break;
        }

        if(!incomingFragment.equals(currentFragment)){
            mDrawerLayout.closeDrawer(mDrawerList);
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_main, incomingFragment).commit();
            currentFragment = incomingFragment;
        }

        Log.i(TAG, "Switching Fragments to: " + newTitle);
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