package com.kanzelmeyer.alfred.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.kanzelmeyer.alfred.VisitorActivity;


/**
 * Created by kevin on 10/2/15.
 */
public class ViewVisitors implements View.OnClickListener {

    private Context mContext;

    public ViewVisitors(Context context) {
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext, VisitorActivity.class);
        mContext.startActivity(intent);
    }
}
