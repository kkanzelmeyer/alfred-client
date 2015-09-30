package com.kanzelmeyer.alfred.navigation;

/**
 * Class to represent an item in the main navigation drawer
 */
public class NavItem {

    private int mIcon;
    private String mTitle;

    public NavItem(String title, int icon) {
        mIcon = icon;
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }
    public int getIcon() {return mIcon; }
}
