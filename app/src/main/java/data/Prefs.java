package data;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import app.MyApp;
import utils.Utils;

/**
 * Created by Reiky on 2016/5/20.
 */
public class Prefs {

    final static String TAG = "Prefs";

    public static void save(String key, Object data) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
        Log.e("ssssss", TAG + Utils.getLineNumber(new Exception()));
        prefs.edit().putString(key, String.valueOf(data)).apply();
    }

    public static boolean getBoolean(String key, boolean def) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
        return Boolean.parseBoolean(prefs.getString(key, String.valueOf(def)));
    }

    public static boolean getBoolean(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
        return Boolean.parseBoolean(prefs.getString(key, ""));
    }

    public static int getInt(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
        return Integer.parseInt(prefs.getString(key, "-1"));
    }

    public static String getString(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext());
        return prefs.getString(key, null);
    }

}
