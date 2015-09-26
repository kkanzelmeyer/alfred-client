package com.kanzelmeyer.alfred.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alfred.common.datamodel.StateDevice;
import com.alfred.common.messages.StateDeviceProtos;
import com.kanzelmeyer.alfred.R;
import com.kanzelmeyer.alfred.listeners.GarageDoorToggleListener;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kevin on 9/10/15.
 */
public class DeviceSummaryAdapter extends BaseAdapter {

    private final static String TAG = "DeviceSummaryAdapter";
    private Context mContext;
    private ArrayList<StateDevice> mDeviceList;

    public DeviceSummaryAdapter(ArrayList<StateDevice> list, Context context) {
        mContext = context;
        mDeviceList = list;
    }
    @Override
    public int getCount() {
        return mDeviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDeviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.container_device_summary, null);
        }
        // Get state device info
        StateDevice device = mDeviceList.get(position);
        String deviceState = WordUtils.capitalizeFully(device.getState().toString());
        String deviceName = WordUtils.capitalizeFully(device.getName());
        String deviceId = device.getId();

        // Get view elements
        TextView text = (TextView) convertView.findViewById(R.id.containerDeviceSummary);
        Button button = (Button) convertView.findViewById(R.id.containerDeviceButton);
        ToggleButton toggleButton = (ToggleButton) convertView.findViewById(R.id.containerDeviceToggle);
        Switch stateSwitch = (Switch) convertView.findViewById(R.id.containerDeviceSwitch);

        switch(device.getType()) {
            case GARAGEDOOR:
                // TODO set image icon

                // Set text
                text.setText(deviceName + " is " + deviceState);

                // Set properties
                toggleButton.setVisibility(View.VISIBLE);
                toggleButton.setTextOn("Close");
                toggleButton.setTextOff("Open");

                // hide other elements
                button.setVisibility(View.GONE);
                stateSwitch.setVisibility(View.GONE);

                // Handle toggle display
                if(device.getState() == StateDeviceProtos.StateDeviceMessage.State.OPEN) {
                    toggleButton.setChecked(true);
                } else {
                    toggleButton.setChecked(false);
                }
                toggleButton.setOnCheckedChangeListener(new GarageDoorToggleListener(device));
                break;
            case DOORBELL:
                // TODO set image icon

                // Set text
                // TODO get recent visit count from visitor log
                text.setText("You've had 3 visitors today");

                // hide other elements
                toggleButton.setVisibility(View.GONE);
                stateSwitch.setVisibility(View.GONE);


                // TODO add button click listener

                break;
            default:
                // TODO set image icon

                text.setText(deviceName + " is " + deviceState);

                // hide other elements
                button.setVisibility(View.GONE);
                toggleButton.setVisibility(View.GONE);

                // TODO add button click listener

                break;
        }

        convertView.setActivated(true);
        return convertView;
    }
}
