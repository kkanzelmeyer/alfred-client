package com.kanzelmeyer.alfred.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kanzelmeyer.alfred.R;
import com.kanzelmeyer.alfred.storage.Visitor;
import com.kanzelmeyer.alfred.utils.ConstantManager;

import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

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
            mVisitorImage = (ImageView) view.findViewById(R.id.visitorImage);
            mLocation = (TextView) view.findViewById(R.id.visitorLocation);
            mDate = (TextView) view.findViewById(R.id.visitorTime);
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
        String location = WordUtils.capitalizeFully(visitor.getLocation());
        vh.getLocation().setText(location);

        // set time
        vh.getDate().setText(visitor.getDisplayTime());

        // set image
        File imageDirectory = new File(mContext.getFilesDir() + ConstantManager.IMAGE_DIR);
        if(!imageDirectory.exists()) {
            Log.e(TAG, "Image directory not found - setting default image");
            // set to default image
            vh.getVisitorImage().setImageResource(R.drawable.garage_16x9);
        } else {
            File image = new File(imageDirectory, visitor.getImagePath());
            if (!image.exists()) {
                Log.e(TAG, "File not found - setting default image");
                // set to default image
                vh.getVisitorImage().setImageResource(R.drawable.garage_16x9);
            } else {
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                vh.getVisitorImage().setImageBitmap(bitmap);
            }
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
