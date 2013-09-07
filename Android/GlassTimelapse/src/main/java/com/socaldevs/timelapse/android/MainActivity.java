package com.socaldevs.timelapse.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
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
    private FragmentManager fragmentManager;
    private SherlockFragment currentFragment;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String[] navDrawerListItems;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getPreferences(MODE_PRIVATE);

        mDrawerLayout       = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList         = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setBackgroundColor(getResources().getColor(R.color.vintage_orange));
        navDrawerListItems  = getResources().getStringArray(R.array.navigation_items);
        fragmentManager     = getSupportFragmentManager();
        currentFragment     = new SignInFragment();

        //Finally we get to the normal Fragment Transaction stuff
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            selectItem(42);
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
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

        switch(position){
            case 0:
                currentFragment = new NewsFeedFragment();
                break;
            case 1:
                currentFragment = new AlbumFragment();
                break;
            case 2:
                currentFragment = new ControlFragment();
                break;
            default:
                currentFragment = new SignInFragment();
                break;
        }

        getSupportFragmentManager().beginTransaction()
               .replace(R.id.frame_main, currentFragment);

        // Highlight the selected item and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
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
}
