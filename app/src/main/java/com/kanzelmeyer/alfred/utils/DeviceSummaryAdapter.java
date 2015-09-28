package com.kanzelmeyer.alfred.utils;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alfred.common.datamodel.StateDevice;
import com.kanzelmeyer.alfred.R;

import org.apache.commons.lang3.text.WordUtils;

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
        private ImageView icon;
        private TextView summary;
        private TextView action;

        public CardView getCardview() {
            return cardview;
        }

        public ImageView getContextImage() {
            return contextImage;
        }

        public ImageView getIcon() {
            return icon;
        }

        public TextView getSummary() {
            return summary;
        }

        public TextView getAction() {
            return action;
        }

        public DeviceSummaryViewHolder(View view) {
            super(view);
            cardview = (CardView) view.findViewById(R.id.deviceCardView);
            contextImage = (ImageView) view.findViewById(R.id.cardviewContextImage);
            summary = (TextView) view.findViewById(R.id.cardviewDeviceSummary);
            action = (TextView) view.findViewById(R.id.cardviewActionText);
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
                // TODO set image icon

                // Set text
                vh.getSummary().setText(deviceName + " is " + deviceState);

                // Set properties
                break;
            case DOORBELL:
                // TODO set image icon

                // Set text
                // TODO get recent visit count from visitor log
                vh.getSummary().setText("You've had 3 visitors today");
                vh.getAction().setText("VIEW");


                // TODO add button click listener

                break;
            default:
                // TODO set image icon

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
}
