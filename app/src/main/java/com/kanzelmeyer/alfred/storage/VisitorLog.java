package com.kanzelmeyer.alfred.storage;

import android.content.Context;
import android.util.Log;

import com.kanzelmeyer.alfred.utils.ConstantManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

/**
 * Created by kevin on 9/10/15.
 */
public class VisitorLog {

    final static String TAG = "VisitorLog";
    private static int visitsToday;

    /**
     * Method to count the number of visitors today
     * @param context
     * @return
     */
    public static int getVisitsToday(Context context, String location) {
        JSONArray log = getLogAsJSONArray(context);
        ArrayList<Visitor> visitorList = new ArrayList<>();
        JSONArray returnArray = new JSONArray();
        JSONObject obj;
        Visitor v;

        visitsToday = 0;
        if(log.length() > 0 ) {
            for (int i = 0; i < log.length(); i++) {
                try {
                    obj = log.getJSONObject(i);
                    v = toVisitor(obj);

                    // update the count of visitors today
                    if(isToday(v.getTime()) && v.getLocation().equals(location)) {
                        visitsToday ++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.i(TAG, "Log is empty");
        }
        return visitsToday;
    }

    /**
     * This method reads the current visitor log and returns it as a JSON Array
     * @param context
     * @return
     */
    public static JSONArray getLogAsJSONArray(Context context) {
        JSONArray log = new JSONArray();
        try {
            // Check if file exists for initial installation
            File file = new File(context.getFilesDir(), ConstantManager.VISITOR_LOG);
            if(!file.exists()) {
                Log.i(TAG, "File being created? " + file.createNewFile());
            }
                FileInputStream in = context.openFileInput(ConstantManager.VISITOR_LOG);
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                // Read the existing content in the JSON file and add it to the
                // string builder
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                if (sb.toString() != "") {
                    log = new JSONArray(sb.toString());
                }
        } catch (IOException e) {
            e.printStackTrace();
        }

        catch (JSONException e) {
            e.printStackTrace();
        }
        return log;
    }

    /**
     * This method saves the input JSON array as the new log file
     * @param log
     * @param context
     */
    private static void saveJSONLog(JSONArray log, Context context) {

        JSONArray adjustedLog = countAndTruncate(log, context);

        // Write complete array to the file
        try {
            FileOutputStream fos = context.openFileOutput(ConstantManager.VISITOR_LOG,
                    Context.MODE_PRIVATE);
            fos.write(adjustedLog.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method counts and truncates a JSON array with a number entries
     * higher than a threshold
     * @param log
     * @return
     */
    private static JSONArray countAndTruncate(JSONArray log, Context context) {
        ArrayList<Visitor> visitorList = new ArrayList<>();
        JSONArray returnArray = new JSONArray();
        JSONObject obj;
        Visitor v;

        // TODO make this a preference
        int entriesToKeep = 15;

        if(log.length() > 0 ) {
            for (int i = 0; i < log.length(); i++) {
                try {
                    obj = log.getJSONObject(i);
                    v = toVisitor(obj);
                    visitorList.add(v);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.i(TAG, "Log is empty");
        }
        // Sort the visitor log and delete old entries
        Collections.sort(visitorList);

        if (visitorList.size() > entriesToKeep) {
            for (int i = entriesToKeep; i < visitorList.size(); i++) {
                v = visitorList.get(i);

                // check if file exists
                File imageDirectory = new File(context.getFilesDir() + ConstantManager.IMAGE_DIR);
                if (!imageDirectory.exists()) {
                    Log.e(TAG, "Directory not found - cannot delete image");
                } else {
                    // check if image exists
                    File image = new File(imageDirectory, v.getImagePath());
                    if (!image.exists()) {
                        Log.e(TAG, "Image not found - cannot delete image");
                    } else {
                        // delete file
                        Log.i(TAG, "Deleting image");
                        image.delete();
                    }
                }
                Log.i(TAG, "Deleting log entry");
                visitorList.remove(i);
            }
        }

        // return the cleaned list as a JSON Array
        for(Visitor visitor : visitorList) {
            returnArray.put(visitor.toJSON());
        }
        return returnArray;
    }

    /**
     * This method takes a JSON object and converts it to a Visitor object
     * @param obj
     * @return
     */
    private static Visitor toVisitor(JSONObject obj) {
        Visitor visitor = new Visitor();
        try {
            visitor.setImagePath(obj.getString("imagePath"));
            visitor.setTime(obj.getLong("time"));
            visitor.setLocation(obj.getString("location"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return visitor;
    }

    /**
     * This method logs a visitor event
     * @param visitor
     * @param context
     */
    public static void logVisitor(Visitor visitor, Context context) {
        Log.i(TAG, "Saving visitor");
        JSONObject obj = visitor.toJSON();
        JSONArray logArray = getLogAsJSONArray(context);
        logArray.put(obj);
        saveJSONLog(logArray, context);
    }

    /**
     * This method returns the current visitor log as an array list
     * @param context
     * @return
     */
    public static ArrayList<Visitor> toArrayList(Context context) {
        ArrayList<Visitor> visitorList = new ArrayList<>();
        JSONObject obj;
        JSONArray visitorArray = getLogAsJSONArray(context);
        if(visitorArray.length() > 0 ) {
            for (int i = 0; i < visitorArray.length(); i++) {
                try {
                    obj = visitorArray.getJSONObject(i);
                    visitorList.add(toVisitor(obj));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Log.i(TAG, "Log is empty");
        }
        Collections.sort(visitorList);
        return visitorList;
    }

    /**
     * This method checks if a given date occurs today
     * @param time
     * @return
     */
    public static boolean isToday(long time) {
        Calendar visit = Calendar.getInstance();
        visit.setTimeInMillis(time);

        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());

        return (visit.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR));
    }

}
