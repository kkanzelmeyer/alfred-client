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
import java.util.Collections;

/**
 * Created by kevin on 9/10/15.
 */
public class VisitorLog {

    final static String TAG = "VisitorLog";

    public static JSONArray getLogAsJSONArray(Context context) {
        JSONArray log = new JSONArray();
        try {
            // Check if file exists for initial installation
            File file = new File(context.getFilesDir(), ConstantManager.EVENT_LOG);
            if(!file.exists()) {
                Log.i(TAG, "File being created? " + file.createNewFile());
            }
                FileInputStream in = context.openFileInput(ConstantManager.EVENT_LOG);
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

    private static void saveJSONLog(JSONArray log, Context context) {
        // Write complete array to the file
        try {
            FileOutputStream fos = context.openFileOutput(ConstantManager.EVENT_LOG,
                    Context.MODE_PRIVATE);
            fos.write(log.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public static void logEvent(Visitor visitor, Context context) {
        JSONObject obj = visitor.toJSON();
        JSONArray logArray = getLogAsJSONArray(context);
        Log.i(TAG, visitor.toString());
        logArray.put(obj);
        saveJSONLog(logArray, context);
    }

    public static ArrayList<Visitor> logAsEventList(Context context) {
        ArrayList<Visitor> visitorList = new ArrayList<>();
        JSONObject obj;
        JSONArray eventArray = getLogAsJSONArray(context);
        if(eventArray.length() > 0 ) {
            for (int i = 0; i < eventArray.length(); i++) {
                try {
                    obj = eventArray.getJSONObject(i);
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
}
