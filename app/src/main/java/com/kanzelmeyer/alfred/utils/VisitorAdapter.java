package com.kanzelmeyer.alfred.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import static android.text.format.DateUtils.*;

import com.kanzelmeyer.alfred.R;
import com.kanzelmeyer.alfred.storage.Visitor;

import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Used to create the card view activity for the visitor log
 * Created by kevin on 9/10/15.
 */
public class VisitorAdapter extends RecyclerView.Adapter<VisitorAdapter.VisitorViewHolder> {

    private final static String TAG = "VisitorAdapter";
    private Context mContext;
    private ArrayList<Visitor> mDeviceList;

    public VisitorAdapter(ArrayList<Visitor> list, Context context) {
        mContext = context;
        mDeviceList = list;
    }

    public static class VisitorViewHolder extends RecyclerView.ViewHolder {

        private CardView cardview;
        private ImageView mVisitorImage;
        private TextView mLocation;
        private TextView mDate;

        public CardView getCardview() {
            return cardview;
        }

        public ImageView getVisitorImage() {
            return mVisitorImage;
        }

        public TextView getLocation() {
            return mLocation;
        }

        public TextView getDate() {
            return mDate;
        }

        public VisitorViewHolder(View view) {
            super(view);
            cardview = (CardView) view.findViewById(R.id.deviceCardView);
            mVisitorImage = (ImageView) view.findViewById(R.id.cardviewIcon);
            mLocation = (TextView) view.findViewById(R.id.cardviewDeviceSummary);
            mDate = (TextView) view.findViewById(R.id.cardviewActionText);
        }

    }

    @Override
    public VisitorViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_visitor, parent, false);
        // set the view's size, margins, paddings and layout parameters

        VisitorViewHolder vh = new VisitorViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(VisitorViewHolder vh, int i) {
        // Get state device info
        Visitor visitor = mDeviceList.get(i);

        // set location
        String location = WordUtils.capitalizeFully(visitor.getLocation().toString());
        vh.getLocation().setText(location);

        // set time
        vh.getDate().setText(visitor.getDisplayTime());

        // set image
        File image = new File(visitor.getImagePath());
        if(image.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
            vh.getVisitorImage().setImageBitmap(bitmap);
        } else {
            Log.e(TAG, "File not found");
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
