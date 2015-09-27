package com.kanzelmeyer.alfred;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alfred.common.datamodel.StateDevice;
import com.alfred.common.datamodel.StateDeviceManager;
import com.alfred.common.messages.StateDeviceProtos;
import com.kanzelmeyer.alfred.utils.DeviceSummaryAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // populate listview from devicemanager
        addDevices();

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

        return super.onOptionsItemSelected(item);
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
                        .setState(StateDeviceProtos.StateDeviceMessage.State.CLOSED)
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

}
