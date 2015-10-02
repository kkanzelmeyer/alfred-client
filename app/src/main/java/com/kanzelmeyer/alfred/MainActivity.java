package com.kanzelmeyer.alfred;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alfred.common.datamodel.StateDevice;
import com.alfred.common.datamodel.StateDeviceManager;
import com.alfred.common.messages.StateDeviceProtos;
import com.kanzelmeyer.alfred.navigation.NavAdapter;
import com.kanzelmeyer.alfred.navigation.NavItem;
import com.kanzelmeyer.alfred.utils.DeviceSummaryAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        // populate listview from devicemanager
        addDevices();

        // Side menu
        populateNav();
        buildNav();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    public void addDevices() {
        // add devices
        StateDevice doorbell =
                new StateDevice.Builder()
                        .setId("doorbell1")
                        .setName("Front Door")
                        .setType(StateDeviceProtos.StateDeviceMessage.Type.DOORBELL)
                        .setState(StateDeviceProtos.StateDeviceMessage.State.INACTIVE)
                        .build();

        StateDevice garageDoor =
                new StateDevice.Builder()
                        .setId("garage1")
                        .setName("Main Garage")
                        .setType(StateDeviceProtos.StateDeviceMessage.Type.GARAGEDOOR)
                        .setState(StateDeviceProtos.StateDeviceMessage.State.OPEN)
                        .build();

        StateDeviceManager.updateStateDevice(doorbell);
        StateDeviceManager.updateStateDevice(garageDoor);

        // create adapter
        ArrayList<StateDevice> deviceArray = new ArrayList<>(StateDeviceManager.getAllDevices().values());
        DeviceSummaryAdapter adapter = new DeviceSummaryAdapter(deviceArray, getApplicationContext());
        RecyclerView deviceSummary = (RecyclerView) findViewById(R.id.deviceSummaryRecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());

        // set properties
        deviceSummary.setLayoutManager(llm);
        deviceSummary.setAdapter(adapter);
    }

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
        NavAdapter navAdapter = new NavAdapter(navItems, mContext);
        mDrawerList.setAdapter(navAdapter);


        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = null;

                switch(position) {
                    // Home
                    case 0:
                        // Do nothing, already on the home screen
                        break;

                    // Visitor
                    case 1:
                        intent = new Intent(mContext, VisitorActivity.class);
                        break;

                    // Settings
                    case 2:
                        intent = new Intent(mContext, SettingsActivity.class);
                        break;
                }

                if(intent != null) {
                    mContext.startActivity(intent);
                }
            }
        });
    }


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

}
