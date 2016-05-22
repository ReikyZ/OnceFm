package utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import app.MyApp;

/**
 * Created by Reiky on 2016/5/22.
 */
public class PermissionHelper {

    final static String TAG = "===PermissionHelper===";

    // Permission CODE
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0;
    public static final int PERMISSION_INTERNET = PERMISSION_WRITE_EXTERNAL_STORAGE + 1;


    public static boolean checkPermission(Activity context, String permission) {
        Log.e("sssss", TAG + Utils.getLineNumber(new Exception()));
        int code = getPermissionCode(permission);
        int checkCallPhonePermission = ContextCompat.checkSelfPermission(MyApp.getContext(),
                "android.permission." + permission);
        if (Build.VERSION.SDK_INT >= 23) {
            Log.e("sssss", TAG + Utils.getLineNumber(new Exception()));
            ActivityCompat.requestPermissions(context,
                    new String[]{"android.permission." + permission}, code);
            return false;
        } else {
            Log.e("sssss", TAG + Utils.getLineNumber(new Exception()));
            if (checkCallPhonePermission == PackageManager.PERMISSION_GRANTED) {
                Log.e("sssss", TAG + Utils.getLineNumber(new Exception()));
                return true;
            } else {
                Log.e("sssss", TAG + Utils.getLineNumber(new Exception()));
                ActivityCompat.requestPermissions(context,
                        new String[]{"android.permission." + permission}, code);
            }
        }
        return false;
    }


    private static int getPermissionCode(String permission) {
        switch (permission) {
            case "WRITE_EXTERNAL_STORAGE":
                return PERMISSION_WRITE_EXTERNAL_STORAGE;
            default:
                return -1;
        }
    }
}
