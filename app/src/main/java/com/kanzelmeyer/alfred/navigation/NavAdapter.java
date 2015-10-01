package com.kanzelmeyer.alfred.navigation;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kanzelmeyer.alfred.R;

import java.util.ArrayList;

/**
 * Adapter for navigation side menu
 */
public class NavAdapter extends BaseAdapter {
    private ArrayList<NavItem> mList;
    private Context mContext;

    public NavAdapter(ArrayList<NavItem> list, Context context) {
        mList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_list_item, null);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.navIcon);
        TextView title = (TextView) convertView.findViewById(R.id.navTitle);

        NavItem navItem = mList.get(position);

        icon.setImageResource(navItem.getIcon());
        title.setText(navItem.getTitle());

        convertView.setActivated(true);
        return convertView;
    }
}