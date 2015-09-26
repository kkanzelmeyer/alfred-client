package com.kanzelmeyer.alfred.listeners;

import android.widget.CompoundButton;

import com.alfred.common.datamodel.StateDevice;

/**
 * Created by kevin on 9/25/15.
 */
public class GarageDoorToggleListener implements CompoundButton.OnCheckedChangeListener {

    private StateDevice mDevice;

    public GarageDoorToggleListener(StateDevice device) {
        mDevice = device;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO update device state in StateDeviceManager
    }
}
