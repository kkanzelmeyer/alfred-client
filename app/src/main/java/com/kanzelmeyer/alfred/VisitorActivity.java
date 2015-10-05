package com.kanzelmeyer.alfred;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alfred.common.datamodel.StateDevice;
import com.alfred.common.datamodel.StateDeviceManager;
import com.alfred.common.messages.StateDeviceProtos;
import com.kanzelmeyer.alfred.navigation.NavAdapter;
import com.kanzelmeyer.alfred.navigation.NavItem;
import com.kanzelmeyer.alfred.storage.Visitor;
import com.kanzelmeyer.alfred.storage.VisitorLog;
import com.kanzelmeyer.alfred.adapters.VisitorAdapter;

import java.util.ArrayList;

public class VisitorActivity extends AppCompatActivity {

    private static final String TAG = "Visitor";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor);
        mContext = getApplicationContext();

        // populate listview from visitor log
        createVisitorCards();

        // get the id of the device that triggered the notification
        // and update the state device
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String deviceId = extras.getString("deviceId");
            StateDevice doorbell = new StateDevice(StateDeviceManager.getDevice(deviceId));
            doorbell.setState(StateDeviceProtos.StateDeviceMessage.State.INACTIVE);
            StateDeviceManager.updateStateDevice(doorbell);
        }

        // enable the back arrow in the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {
            // handle click to the action bar up button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
        }
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        // Activate the navigation drawer toggle
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        mDrawerToggle.syncState();
    }

    /**
     * Helper method to handle the recyclerview adapter
     */
    public void createVisitorCards() {
        ArrayList<Visitor> visitorList = VisitorLog.toArrayList(mContext);
        VisitorAdapter adapter = new VisitorAdapter(visitorList, getApplicationContext());
        RecyclerView visitors = (RecyclerView) findViewById(R.id.visitorRecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);

        // set properties
        visitors.setLayoutManager(llm);
        visitors.setAdapter(adapter);
    }

    /**
     * Helper method to populate the side nav
     */
    public void populateNav() {
        // get list of array resources
        String[] navTitles = getResources().getStringArray(R.array.nav_drawer_items);
        TypedArray navIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        // get the navigation drawer and listview
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerList = (ListView) findViewById(R.id.mainMenu);

        // Add objects to the list of Nav Items
        ArrayList<NavItem> navItems = new ArrayList<>();
        for(int i = 0; i< navTitles.length; i++) {
            navItems.add(new NavItem(navTitles[i], navIcons.getResourceId(i, -1)));
        }

        // set adapter to list view
        NavAdapter navAdapter = new NavAdapter(navItems, getApplicationContext());
        mDrawerList.setAdapter(navAdapter);

        // TODO set click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(VisitorActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Helper method to construct the side nav
     */
    public void buildNav() {

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.i(TAG, "Drawer opened");
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                Log.i(TAG, "Drawer closed");
                getSupportActionBar().setTitle("Alfred");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * Method to create visitor entries for testing
     */
    public void addVisitors() {

        for(int i = 0; i < 3; i++) {
            Visitor v = new Visitor();
            v.setImagePath("");
            v.setLocation("Front Door");
            v.setTime(System.currentTimeMillis());
            VisitorLog.logVisitor(v, mContext);
        }
    }

}
