package com.kanzelmeyer.alfred.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfred.common.datamodel.StateDevice;
import com.alfred.common.datamodel.StateDeviceManager;
import com.alfred.common.messages.StateDeviceProtos;
import com.kanzelmeyer.alfred.R;
import com.kanzelmeyer.alfred.VisitorActivity;
import com.kanzelmeyer.alfred.storage.VisitorLog;

import org.apache.commons.lang3.text.WordUtils;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by kevin on 9/10/15.
 */
public class DeviceSummaryAdapter extends RecyclerView.Adapter<DeviceSummaryAdapter.DeviceSummaryViewHolder> {

    private final static String TAG = "DeviceSummaryAdapter";
    private Context mContext;
    private ArrayList<StateDevice> mDeviceList;

    public DeviceSummaryAdapter(ArrayList<StateDevice> list, Context context) {
        mContext = context;
        mDeviceList = list;
    }

    public static class DeviceSummaryViewHolder extends RecyclerView.ViewHolder {

        private CardView cardview;
        private ImageView contextImage;
        private ImageView alertIcon;
        private TextView summary;
        private TextView secondaryAction;
        private TextView primaryAction;

        public CardView getCardview() {
            return cardview;
        }

        public ImageView getContextImage() {
            return contextImage;
        }

        public ImageView getAlertIcon() {
            return alertIcon;
        }

        public TextView getSummary() {
            return summary;
        }

        public TextView getSecondaryAction() {
            return secondaryAction;
        }

        public TextView getPrimaryActionAction() {
            return primaryAction;
        }

        public DeviceSummaryViewHolder(View view) {
            super(view);
            cardview = (CardView) view.findViewById(R.id.deviceCardView);
            contextImage = (ImageView) view.findViewById(R.id.cardviewIcon);
            summary = (TextView) view.findViewById(R.id.cardviewDeviceSummary);
            primaryAction = (TextView) view.findViewById(R.id.cardviewActionPrimary);
            secondaryAction = (TextView) view.findViewById(R.id.cardviewActionSecondary);
            alertIcon = (ImageView) view.findViewById((R.id.alertIcon));
        }


    }

    @Override
    public DeviceSummaryViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_device_summary, parent, false);
        // set the view's size, margins, paddings and layout parameters

        DeviceSummaryViewHolder vh = new DeviceSummaryViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(DeviceSummaryViewHolder vh, int i) {
        // Get state device info
        StateDevice device = mDeviceList.get(i);
        String deviceState = WordUtils.capitalizeFully(device.getState().toString());
        String deviceName = WordUtils.capitalizeFully(device.getName());

        // set elements
        switch(device.getType()) {
            case GARAGEDOOR:
                // set image alertIcon
                vh.getContextImage().setImageResource(R.mipmap.alfred_garage_icon);
                // Set text
                vh.getSummary().setText(deviceName + " is " + deviceState);

                // Set state specific properties
                if(device.getState() == StateDeviceProtos.StateDeviceMessage.State.CLOSED) {
                    vh.getSummary().setTextColor(Color.DKGRAY);
                    vh.getSecondaryAction().setText("OPEN NOW");
                    vh.getAlertIcon().setVisibility(View.INVISIBLE);
                } else {
                    vh.getSecondaryAction().setText("CLOSE NOW");
                    vh.getAlertIcon().setVisibility(View.VISIBLE);
                }

                // TODO add button click listener
                vh.getPrimaryActionAction().setVisibility(View.GONE);

                break;

            case DOORBELL:
                // set image alertIcon
                vh.getContextImage().setImageResource(R.mipmap.alfred_visitor_icon);

                // hide alert alertIcon
                vh.getAlertIcon().setVisibility(View.INVISIBLE);

                // Set description text
                vh.getSummary().setTextColor(Color.DKGRAY);
                int visitsToday = VisitorLog.getVisitsToday(mContext, device.getName());

                // format "visitor" for proper plurality
                String noun = "visitor";
                if(visitsToday != 1) {
                    noun += "s";
                }
                vh.getSummary().setText("You've had " + visitsToday + " " + noun + " at the " + device.getName() + " today");

                // Set click listener(s)
                vh.getCardview().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, VisitorActivity.class);
                        mContext.startActivity(intent);
                    }
                });

                // Primary Action
                // add an option to request a photo if the device is inactive
                vh.getPrimaryActionAction().setText("REQUEST PHOTO");
                if(device.getState() != StateDeviceProtos.StateDeviceMessage.State.ACTIVE) {
                    vh.getPrimaryActionAction().setOnClickListener(new RequestPictureClickListener(device.getId()));
                } else {
                    vh.getPrimaryActionAction().setTextColor(Color.LTGRAY);
                }

                // Secondary Action
                vh.getSecondaryAction().setText("VIEW");

                break;

            default:
                // TODO set image alertIcon

                vh.getSummary().setText(deviceName + " is " + deviceState);

                // TODO add button click listener

                break;
        }


    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Click listener for "take picture" action on doorbell device summary
    class RequestPictureClickListener implements View.OnClickListener {

        private String myDeviceId;

        RequestPictureClickListener(String id) {
            myDeviceId = id;
        }

        @Override
        public void onClick(View v) {
            StateDeviceManager.updateStateDevice(myDeviceId, StateDeviceProtos.StateDeviceMessage.State.ACTIVE);
        }
    }
}
