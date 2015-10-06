package com.kanzelmeyer.alfred;

import android.content.Context;
import android.content.res.Configuration;
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
import android.widget.ListView;

import com.alfred.common.datamodel.StateDeviceManager;
import com.alfred.common.messages.StateDeviceProtos;
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
            Log.i(TAG, "Updating device " + deviceId);
            StateDeviceManager.updateStateDevice(deviceId, StateDeviceProtos.StateDeviceMessage.State.INACTIVE);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
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
}
