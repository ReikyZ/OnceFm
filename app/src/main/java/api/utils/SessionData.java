package api.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by panl on 15/7/7.
 */
public class SessionData {
    private static final String SESSION = "session";
    private static Context context;

    public static void initApi(Context ctx) {
        context = ctx;
    }

    //User Session相关信息
    public static boolean saveAccessToken(String token) {
        SharedPreferences preferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        return preferences.edit().putString("access_token", token).commit();
    }

    public static String getAccessToken() {
        SharedPreferences preferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        return preferences.getString("access_token", "");
    }

    public static boolean saveRefreshToken(String token) {
        SharedPreferences preferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        return preferences.edit().putString("refresh_token", token).commit();
    }

    public static String getRefreshToken() {
        SharedPreferences preferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        return preferences.getString("refresh_token", "");
    }

    public static boolean saveExpireAt(String time) {
        SharedPreferences preferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        return preferences.edit().putString("expire_at", time).commit();
    }

    public static String getExpireAt() {
        SharedPreferences preferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        return preferences.getString("expire_at", "");
    }

    public static boolean saveUserId(int userId) {
        SharedPreferences preferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        return preferences.edit().putInt("user_id", userId).commit();
    }

    public static int getUserId() {
        SharedPreferences preferences = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        return preferences.getInt("user_id", -1);
    }

    public static void clearTokenMessage() {
        SharedPreferences session = context.getSharedPreferences(SESSION, Context.MODE_PRIVATE);
        session.edit().clear().apply();
    }

    public static Context getContext() {
        return context;
    }

}
