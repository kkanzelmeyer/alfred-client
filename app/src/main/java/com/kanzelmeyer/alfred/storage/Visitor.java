package com.kanzelmeyer.alfred.storage;

import android.text.format.DateUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.text.format.DateUtils.MINUTE_IN_MILLIS;

/**
 * Created by kevin on 9/27/15.
 */
public class Visitor implements Comparable<Visitor> {

    private long time;
    private String location;
    private String imagePath;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("time", getTime());
            obj.put("location", getLocation());
            obj.put("imagePath", getImagePath());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public String toString() {
        return "Time: " + getFormatTime() +
                "\nLocation: " + getLocation() +
                "\nImage Path: " + getImagePath();
    }

    /**
     * Display time in format like March 19, 2013 11:31 am EST
     * @return
     */
    public String getFormatTime() {
        Date parsed = new Date();
        try {
            SimpleDateFormat format =
                    new SimpleDateFormat("M dd, yyyy h:m:s a zzz");
            parsed = format.parse(String.valueOf(getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsed.toString();
    }

    /**
     * Display time for Android display like 3 minutes ago
     * @return
     */
    public String getDisplayTime() {
        Calendar now = Calendar.getInstance();
        String displayTime = (String) DateUtils.getRelativeTimeSpanString(getTime(), now.getTimeInMillis(), MINUTE_IN_MILLIS);
        return displayTime;
    }

    @Override
    public int compareTo(Visitor another) {
        return (int) (another.getTime() - this.getTime());
    }
}
